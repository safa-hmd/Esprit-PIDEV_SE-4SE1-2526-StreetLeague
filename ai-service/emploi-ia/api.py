from flask import Flask, request, jsonify
import joblib
import pandas as pd
import numpy as np
import math
import requests
import os

# ── TensorFlow ──────────────────────────────────────────────────
import tensorflow as tf

app    = Flask(__name__)

# ── Chargement modèles ───────────────────────────────────────────
# Supporte l'ancien (GradientBoosting) ET le nouveau (Neural Network)
try:
    model = tf.keras.models.load_model("calendar_model_dl.keras")
    MODEL_TYPE = "deep_learning"
    print("✅ Modèle Deep Learning chargé (calendar_model_dl.keras)")
except Exception:
    try:
        model = tf.keras.models.load_model("calendar_model_dl.h5")
        MODEL_TYPE = "deep_learning"
        print("✅ Modèle Deep Learning chargé (calendar_model_dl.h5)")
    except Exception:
        import joblib as jl
        model = jl.load("calendar_model.pkl")
        MODEL_TYPE = "gradient_boosting"
        print("⚠️  Modèle GradientBoosting chargé (fallback)")

scaler = joblib.load("scaler.pkl")
print(f"✅ Scaler chargé | Mode : {MODEL_TYPE} | API prête sur port 5001 !")

# ── Config OpenWeatherMap ────────────────────────────────────────
OWM_API_KEY = os.environ.get("OWM_API_KEY", "VOTRE_CLE_ICI")
OWM_URL     = "https://api.openweathermap.org/data/2.5/weather"

WEATHER_SCORE_MAP = {
    "Clear":       1.0,
    "Clouds":      0.7,
    "Drizzle":     0.4,
    "Rain":        0.2,
    "Thunderstorm":0.0,
    "Snow":        0.1,
    "Mist":        0.5,
    "Fog":         0.4,
    "Wind":        0.5,
}

# ── Haversine distance ───────────────────────────────────────────
def haversine(lat1, lng1, lat2, lng2):
    R = 6371
    dlat = math.radians(lat2 - lat1)
    dlng = math.radians(lng2 - lng1)
    a = (math.sin(dlat/2)**2 +
         math.cos(math.radians(lat1)) *
         math.cos(math.radians(lat2)) *
         math.sin(dlng/2)**2)
    return round(R * 2 * math.asin(math.sqrt(a)), 2)

# ── Appel OpenWeatherMap ─────────────────────────────────────────
def get_weather(lat, lng):
    """Retourne (weather_score, temp_comfort, description)"""
    try:
        if OWM_API_KEY == "VOTRE_CLE_ICI":
            # Mode simulation si pas de clé configurée
            return 0.8, 1.0, "clear (simulation)"

        resp = requests.get(OWM_URL, params={
            "lat": lat, "lon": lng,
            "appid": OWM_API_KEY,
            "units": "metric"
        }, timeout=3)
        data = resp.json()

        main_weather = data["weather"][0]["main"]
        temperature  = data["main"]["temp"]
        description  = data["weather"][0]["description"]

        weather_score = WEATHER_SCORE_MAP.get(main_weather, 0.6)
        temp_comfort  = (1.0 if 15 <= temperature <= 28
                    else 0.5 if 10 <= temperature <= 35
                    else 0.2)

        return weather_score, temp_comfort, description
    except Exception as e:
        print(f"⚠️  Météo API erreur: {e} → fallback simulation")
        return 0.7, 0.8, "unavailable"

# ── Construction features (compatibles ancien + nouveau dataset) ─
def build_features(d, distance_km=None, weather_score=None,
                   temp_comfort=None, has_tournament=0,
                   tournament_priority=0, field_tournament_fit=1):
    mw = d["matchesWeek"]
    tw = d["trainingsWeek"]
    pc = d["playerCount"]
    fc = d["fieldCapacity"]
    v  = d.get("victories", 10)
    de = d.get("defeats", 5)

    # Features de base (existantes)
    base = {
        "elo":             d["elo"],
        "player_count":    pc,
        "level":           d["level"],
        "matches_week":    mw,
        "trainings_week":  tw,
        "fatigue":         min(10, mw * 2 + tw),
        "hour":            d["hour"],
        "day":             d["day"],
        "duration":        d["duration"],
        "field_available": d["fieldAvailable"],
        "field_pressure":  d["fieldPressure"],
        "field_capacity":  fc,
        "win_ratio":       v / (v + de + 1),
        "team_load":       mw + tw,
        "is_weekend":      1 if d["day"] >= 5 else 0,
        "is_peak_hour":    1 if 16 <= d["hour"] <= 20 else 0,
        "field_fits":      1 if fc >= pc else 0,
    }

    # Nouvelles features (GPS + météo + tournament)
    if distance_km is not None:
        base["distance_km"]           = distance_km
        base["weather_score"]         = weather_score if weather_score is not None else 0.8
        base["temp_comfort"]          = temp_comfort  if temp_comfort  is not None else 1.0
        base["has_tournament"]        = has_tournament
        base["tournament_priority"]   = tournament_priority
        base["field_tournament_fit"]  = field_tournament_fit

    return pd.DataFrame([base])

# ── Prédiction selon type de modèle ─────────────────────────────
def predict_score(features):
    scaled = scaler.transform(features)
    if MODEL_TYPE == "deep_learning":
        proba = float(model.predict(scaled, verbose=0)[0][0])
    else:
        proba = float(model.predict_proba(scaled)[0][1])
    return round(proba, 4)

def get_recommendation(score):
    if score >= 0.75: return "EXCELLENT"
    if score >= 0.5:  return "ACCEPTABLE"
    return "DECONSEILLE"

# ════════════════════════════════════════════════════════════════
# ROUTES
# ════════════════════════════════════════════════════════════════

# ── /predict (ancien endpoint, toujours compatible) ─────────────
@app.route("/predict", methods=["POST"])
def predict():
    d = request.json

    # GPS optionnel
    distance_km   = None
    weather_score = None
    temp_comfort  = None
    weather_desc  = "N/A"

    if "userLat" in d and "fieldLat" in d:
        distance_km = haversine(
            d["userLat"], d["userLng"],
            d["fieldLat"], d["fieldLng"]
        )
        weather_score, temp_comfort, weather_desc = get_weather(
            d["fieldLat"], d["fieldLng"]
        )

    features = build_features(
        d,
        distance_km=distance_km,
        weather_score=weather_score,
        temp_comfort=temp_comfort,
        has_tournament=d.get("hasTournament", 0),
        tournament_priority=d.get("tournamentPriority", 0),
        field_tournament_fit=d.get("fieldTournamentFit", 1),
    )

    score = predict_score(features)
    rec   = get_recommendation(score)

    return jsonify({
        "score":          score,
        "recommendation": rec,
        "confidence":     f"{score*100:.1f}%",
        "distanceKm":     distance_km,
        "weather":        weather_desc,
        "modelType":      MODEL_TYPE,
    })


# ── /suggest-fields : Top N terrains proches ────────────────────
@app.route("/suggest-fields", methods=["POST"])
def suggest_fields():
    """
    Reçoit : userLat, userLng, fields[], contexte (heure, équipe...)
    Retourne : fields triés par score DL (distance + météo + dispo)

    Body JSON:
    {
      "userLat": 36.8, "userLng": 10.1,
      "matchesWeek": 2, "trainingsWeek": 3,
      "playerCount": 10, "elo": 1200, "level": 2,
      "hour": 18, "day": 5, "duration": 90,
      "victories": 8, "defeats": 4,
      "hasTournament": 0, "tournamentPriority": 0,
      "fields": [
        { "id": 1, "name": "Terrain Ibn Khaldoun",
          "lat": 36.82, "lng": 10.12,
          "capacity": 20, "isAvailable": true,
          "pressure": 0.3 },
        ...
      ]
    }
    """
    d      = request.json
    fields = d.get("fields", [])

    if not fields:
        return jsonify({"error": "Aucun terrain fourni"}), 400

    results = []
    for f in fields:
        dist = haversine(d["userLat"], d["userLng"], f["lat"], f["lng"])
        ws, tc, wdesc = get_weather(f["lat"], f["lng"])

        fc = f.get("capacity", 15)
        pc = d.get("playerCount", 10)
        ht = d.get("hasTournament", 0)
        field_tournament_fit = 1 if (ht == 0 or fc >= 20) else 0

        features = build_features({
            "elo":           d.get("elo", 1000),
            "playerCount":   pc,
            "level":         d.get("level", 2),
            "matchesWeek":   d.get("matchesWeek", 0),
            "trainingsWeek": d.get("trainingsWeek", 0),
            "victories":     d.get("victories", 5),
            "defeats":       d.get("defeats", 5),
            "hour":          d.get("hour", 17),
            "day":           d.get("day", 5),
            "duration":      d.get("duration", 90),
            "fieldAvailable":1 if f.get("isAvailable", True) else 0,
            "fieldPressure": f.get("pressure", 0.3),
            "fieldCapacity": fc,
        },
            distance_km=dist,
            weather_score=ws, temp_comfort=tc,
            has_tournament=ht,
            tournament_priority=d.get("tournamentPriority", 0),
            field_tournament_fit=field_tournament_fit,
        )

        score = predict_score(features)

        results.append({
            "fieldId":        f.get("id"),
            "fieldName":      f.get("name", "Terrain"),
            "distanceKm":     dist,
            "score":          score,
            "recommendation": get_recommendation(score),
            "weather":        wdesc,
            "weatherScore":   ws,
            "isAvailable":    f.get("isAvailable", True),
            "capacity":       fc,
        })

    # Tri par score décroissant
    results.sort(key=lambda x: x["score"], reverse=True)

    return jsonify({
        "bestFields": results[:5],   # Top 5
        "total":      len(results),
        "modelType":  MODEL_TYPE,
    })


# ── /suggest-slot : Meilleur créneau pour la semaine ────────────
@app.route("/suggest-slot", methods=["POST"])
def suggest_slot():
    """
    Reçoit : contexte équipe + terrain sélectionné
    Retourne : Top 3 créneaux de la semaine

    Body JSON:
    {
      "userLat": 36.8, "userLng": 10.1,
      "fieldLat": 36.82, "fieldLng": 10.12,
      "fieldCapacity": 20, "fieldPressure": 0.3,
      "fieldAvailable": 1,
      "playerCount": 10, "elo": 1200, "level": 2,
      "matchesWeek": 2, "trainingsWeek": 3,
      "victories": 8, "defeats": 4,
      "duration": 90,
      "hasTournament": 0, "tournamentPriority": 0
    }
    """
    d = request.json

    dist = haversine(
        d["userLat"], d["userLng"],
        d["fieldLat"], d["fieldLng"]
    )
    ws, tc, wdesc = get_weather(d["fieldLat"], d["fieldLng"])

    slots  = []
    hours  = [8, 10, 14, 16, 17, 18, 19, 20]
    days   = list(range(7))  # lundi=0 … dimanche=6

    for day in days:
        for hour in hours:
            features = build_features({
                "elo":           d.get("elo", 1000),
                "playerCount":   d.get("playerCount", 10),
                "level":         d.get("level", 2),
                "matchesWeek":   d.get("matchesWeek", 0),
                "trainingsWeek": d.get("trainingsWeek", 0),
                "victories":     d.get("victories", 5),
                "defeats":       d.get("defeats", 5),
                "hour":          hour,
                "day":           day,
                "duration":      d.get("duration", 90),
                "fieldAvailable":d.get("fieldAvailable", 1),
                "fieldPressure": d.get("fieldPressure", 0.3),
                "fieldCapacity": d.get("fieldCapacity", 15),
            },
                distance_km=dist,
                weather_score=ws, temp_comfort=tc,
                has_tournament=d.get("hasTournament", 0),
                tournament_priority=d.get("tournamentPriority", 0),
                field_tournament_fit=d.get("fieldTournamentFit", 1),
            )

            score = predict_score(features)
            day_names = ["Lun","Mar","Mer","Jeu","Ven","Sam","Dim"]
            slots.append({
                "day":      day,
                "dayName":  day_names[day],
                "hour":     hour,
                "label":    f"{day_names[day]} {hour}h",
                "score":    score,
                "rec":      get_recommendation(score),
                "weather":  wdesc,
                "distKm":   dist,
            })

    slots.sort(key=lambda x: x["score"], reverse=True)

    return jsonify({
        "bestSlots": slots[:3],
        "allSlots":  slots,
        "total":     len(slots),
        "weather":   wdesc,
        "modelType": MODEL_TYPE,
    })


# ── /suggest (ancien endpoint conservé) ─────────────────────────
@app.route("/suggest", methods=["POST"])
def suggest():
    slots   = request.json["slots"]
    results = []
    for slot in slots:
        features = build_features(slot)
        score    = predict_score(features)
        results.append({**slot, "score": score})
    best = sorted(results, key=lambda x: x["score"], reverse=True)[:3]
    return jsonify({"bestSlots": best, "total": len(slots)})


# ── /health ──────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status":    "ok",
        "model":     MODEL_TYPE,
        "features":  "gps + weather + tournament + fatigue",
        "endpoints": ["/predict", "/suggest-fields", "/suggest-slot", "/suggest", "/health"],
        "owm_key":   "configured" if OWM_API_KEY != "VOTRE_CLE_ICI" else "simulation_mode",
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)