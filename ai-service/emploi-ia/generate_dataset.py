import random
import pandas as pd

random.seed(42)
rows = []

for _ in range(20000):
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

    score = 0
    if field_available == 0:           score -= 10
    if   fatigue <= 3:                 score += 4
    elif fatigue <= 6:                 score += 2
    elif fatigue <= 8:                 score -= 1
    else:                              score -= 4
    if   16 <= hour <= 20:             score += 3
    elif hour == 14:                   score += 1
    elif hour == 22:                   score -= 2
    elif hour <= 10:                   score -= 1
    if   field_pressure < 0.4:         score += 2
    elif field_pressure > 0.8:         score -= 2
    if field_capacity >= player_count: score += 1
    else:                              score -= 3
    if day in [5, 6] and matches_week >= 3: score -= 2
    if level == 3 and duration >= 90:       score += 1

    label = 1 if score >= 3 else 0

    rows.append([
        elo, player_count, level, victories, defeats,
        matches_week, trainings_week, fatigue,
        hour, day, duration,
        field_available, field_pressure, field_capacity,
        label
    ])

cols = [
    "elo", "player_count", "level", "victories", "defeats",
    "matches_week", "trainings_week", "fatigue",
    "hour", "day", "duration",
    "field_available", "field_pressure", "field_capacity",
    "label"
]

df = pd.DataFrame(rows, columns=cols)
df.to_csv("dataset_raw.csv", index=False)
print("Dataset genere :", df.shape)
print(df["label"].value_counts())