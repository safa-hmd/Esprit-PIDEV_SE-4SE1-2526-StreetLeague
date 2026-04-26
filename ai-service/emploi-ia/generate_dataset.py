import random
import math
import pandas as pd

random.seed(42)
rows = []

def haversine(lat1, lng1, lat2, lng2):
    R = 6371
    dlat = math.radians(lat2 - lat1)
    dlng = math.radians(lng2 - lng1)
    a = math.sin(dlat/2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlng/2)**2
    return R * 2 * math.asin(math.sqrt(a))

# Tunisie bounding box (pour simuler positions réalistes)
LAT_MIN, LAT_MAX = 33.5, 37.0
LNG_MIN, LNG_MAX = 8.0, 11.5

WEATHER_CONDITIONS = ["clear", "cloudy", "rain", "wind", "storm"]
WEATHER_SCORES     = {"clear": 1.0, "cloudy": 0.7, "rain": 0.2, "wind": 0.5, "storm": 0.0}

for _ in range(30000):
    # ── Features existantes ──────────────────────────────────────
    elo             = random.randint(800, 1600)
    player_count    = random.randint(6, 20)
    level           = random.randint(1, 3)
    victories       = random.randint(0, 30)
    defeats         = random.randint(0, 30)
    matches_week    = random.randint(0, 5)
    trainings_week  = random.randint(0, 7)
    fatigue         = min(10, matches_week * 2 + trainings_week)
    hour            = random.choice([8, 10, 14, 16, 18, 20, 22])
    day             = random.randint(0, 6)
    duration        = random.choice([60, 90, 120])
    field_available = random.choice([0, 1])
    field_pressure  = round(random.uniform(0, 1), 2)
    field_capacity  = random.choice([10, 15, 20, 30])

    # ── Nouvelles features GPS ───────────────────────────────────
    user_lat = round(random.uniform(LAT_MIN, LAT_MAX), 5)
    user_lng = round(random.uniform(LNG_MIN, LNG_MAX), 5)
    field_lat = round(user_lat + random.uniform(-0.1, 0.1), 5)
    field_lng = round(user_lng + random.uniform(-0.1, 0.1), 5)
    distance_km = round(haversine(user_lat, user_lng, field_lat, field_lng), 2)

    # ── Nouvelles features Météo ─────────────────────────────────
    weather         = random.choice(WEATHER_CONDITIONS)
    weather_score   = WEATHER_SCORES[weather]
    temperature     = round(random.uniform(5, 42), 1)   # °C
    # température idéale pour jouer : 15-28°C
    temp_comfort    = 1.0 if 15 <= temperature <= 28 else (0.5 if 10 <= temperature <= 35 else 0.2)

    # ── Nouvelles features Tournament ───────────────────────────
    has_tournament      = random.choice([0, 0, 0, 1])   # 25% chance
    tournament_priority = random.choice([1, 2, 3]) if has_tournament else 0
    # si tournament → besoin d'un grand terrain
    field_tournament_fit = 1 if (has_tournament == 0 or field_capacity >= 20) else 0

    # ── Score logique (label) ────────────────────────────────────
    score = 0

    # Terrain dispo
    if field_available == 0: score -= 10

    # Fatigue
    if   fatigue <= 3: score += 4
    elif fatigue <= 6: score += 2
    elif fatigue <= 8: score -= 1
    else:              score -= 4

    # Heure
    if   16 <= hour <= 20: score += 3
    elif hour == 14:       score += 1
    elif hour == 22:       score -= 2
    elif hour <= 10:       score -= 1

    # Pression terrain
    if   field_pressure < 0.4: score += 2
    elif field_pressure > 0.8: score -= 2

    # Capacité
    if field_capacity >= player_count: score += 1
    else:                              score -= 3

    # Weekend surchargé
    if day in [5, 6] and matches_week >= 3: score -= 2

    # Level haut + durée longue
    if level == 3 and duration >= 90: score += 1

    # ── NOUVEAU : Distance ───────────────────────────────────────
    if   distance_km <= 2:  score += 4   # très proche → excellent
    elif distance_km <= 5:  score += 2   # proche → bien
    elif distance_km <= 10: score += 0   # acceptable
    elif distance_km <= 20: score -= 2   # loin → déconseillé
    else:                   score -= 5   # très loin → mauvais

    # ── NOUVEAU : Météo ──────────────────────────────────────────
    if   weather == "clear":  score += 3
    elif weather == "cloudy": score += 1
    elif weather == "wind":   score -= 1
    elif weather == "rain":   score -= 4
    elif weather == "storm":  score -= 10

    if   temperature >= 10 and temperature <= 28: score += 1
    elif temperature > 35 or temperature < 5:     score -= 3

    # ── NOUVEAU : Tournament ─────────────────────────────────────
    if has_tournament:
        if field_tournament_fit: score += 2   # grand terrain → parfait
        else:                    score -= 3   # petit terrain → mauvais pour tournament
        if tournament_priority == 1: score += 2  # haute priorité

    label = 1 if score >= 3 else 0

    rows.append([
        elo, player_count, level, victories, defeats,
        matches_week, trainings_week, fatigue,
        hour, day, duration,
        field_available, field_pressure, field_capacity,
        # nouvelles colonnes
        distance_km,
        weather_score, temp_comfort,
        has_tournament, tournament_priority, field_tournament_fit,
        label
    ])

cols = [
    "elo", "player_count", "level", "victories", "defeats",
    "matches_week", "trainings_week", "fatigue",
    "hour", "day", "duration",
    "field_available", "field_pressure", "field_capacity",
    # nouvelles
    "distance_km",
    "weather_score", "temp_comfort",
    "has_tournament", "tournament_priority", "field_tournament_fit",
    "label"
]

df = pd.DataFrame(rows, columns=cols)
df.to_csv("dataset_raw.csv", index=False)
print("✅ Dataset genere :", df.shape)
print(df["label"].value_counts())
print("\nNouvelles features ajoutees :")
print("  📍 distance_km        → distance user ↔ terrain")
print("  🌤️  weather_score      → qualite meteo (0=storm, 1=clear)")
print("  🌡️  temp_comfort       → confort temperature")
print("  🏆 has_tournament     → 1 si tournament cette semaine")
print("  🏆 tournament_priority → priorite du tournament (1-3)")
print("  🏆 field_tournament_fit → terrain assez grand pour tournament")