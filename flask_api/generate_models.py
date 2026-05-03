"""
StreetLeague — generate_models.py
Lance ce script UNE SEULE FOIS dans ton venv pour générer les .pkl
compatibles avec ta version de scikit-learn.

Usage :
    (venv) python generate_models.py
"""

import pandas as pd
import numpy as np
import joblib
import random
import os
from datetime import datetime, timedelta

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.metrics import mean_absolute_error, r2_score

np.random.seed(42)
random.seed(42)

# ── Config ─────────────────────────────────────────────────────────────
SPORT_TYPES = ["FOOTBALL", "BASKETBALL", "TENNIS", "PADEL", "VOLLEYBALL", "OTHER"]
RES_STATUSES = ["APPROVED", "CANCELLED", "REJECTED", "PENDING"]
PAY_METHODS = ["CARD", "CASH", "ONLINE"]

BASE_PRICE = {"FOOTBALL": 30, "BASKETBALL": 25, "TENNIS": 40,
              "PADEL": 50, "VOLLEYBALL": 20, "OTHER": 20}
CAPACITY   = {"FOOTBALL": 22, "BASKETBALL": 10, "TENNIS": 4,
              "PADEL": 4, "VOLLEYBALL": 12, "OTHER": 10}

# ── Génération du dataset ───────────────────────────────────────────────
print("Génération du dataset...")
locations = ["Tunis", "Ariana", "Ben Arous", "Manouba", "Sfax", "Sousse"]
fields = []
for i in range(1, 13):
    sport = random.choice(SPORT_TYPES)
    fields.append({"field_id": i, "sport_type": sport,
                   "location": random.choice(locations),
                   "capacity": CAPACITY[sport], "price_per_hour": BASE_PRICE[sport]})
fields_df = pd.DataFrame(fields)

records = []
start_date = datetime(2024, 1, 1)
end_date   = datetime(2025, 3, 31)

for res_id in range(1, 1201):
    field = fields_df.sample(1).iloc[0]
    days_range = (end_date - start_date).days
    rand_day   = start_date + timedelta(days=random.randint(0, days_range))
    start_hour = random.randint(16, 22) if random.random() < 0.6 else random.randint(8, 15)
    duration   = random.choice([1, 1, 1, 2, 2, 3])
    start_dt   = rand_day.replace(hour=start_hour, minute=0, second=0)
    end_dt     = start_dt + timedelta(hours=duration)
    if end_dt.hour > 23:
        end_dt   = end_dt.replace(hour=23, minute=0)
        duration = end_dt.hour - start_dt.hour

    day_of_week  = start_dt.weekday()
    is_weekend   = int(day_of_week >= 5)
    is_peak_hour = int(18 <= start_hour <= 22)
    month        = start_dt.month
    season       = ("winter" if month in [12, 1, 2] else
                    "spring" if month in [3, 4, 5]  else
                    "summer" if month in [6, 7, 8]  else "autumn")

    status = random.choices(RES_STATUSES, weights=[0.65, 0.20, 0.05, 0.10])[0]
    pay_method = random.choice(PAY_METHODS) if status in ["APPROVED","CANCELLED"] else None
    pay_status = None
    if status == "APPROVED":
        pay_status = random.choices(["PAID","PENDING","FAILED"], weights=[0.85,0.10,0.05])[0]
    elif status == "CANCELLED":
        pay_status = random.choices(["REFUNDED","FAILED","PENDING"], weights=[0.6,0.2,0.2])[0]

    records.append({
        "reservation_id": res_id, "field_id": field["field_id"],
        "sport_type": field["sport_type"], "location": field["location"],
        "capacity": field["capacity"], "base_price_per_hour": field["price_per_hour"],
        "start_time": start_dt, "duration_hours": duration,
        "day_of_week": day_of_week, "hour_of_day": start_hour,
        "is_weekend": is_weekend, "is_peak_hour": is_peak_hour,
        "month": month, "season": season, "status": status,
        "payment_method": pay_method if pay_method else "NONE",
        "payment_status": pay_status if pay_status else "NONE",
        "player_id": random.randint(1, 100),
    })

df = pd.DataFrame(records)

df["week"] = df["start_time"].dt.isocalendar().week
df["year"] = df["start_time"].dt.isocalendar().year
occ = df.groupby(["field_id","year","week"]).size().reset_index(name="reservations_that_week")
df  = df.merge(occ, on=["field_id","year","week"])
df["occupation_rate"] = (df["reservations_that_week"] / 105).clip(0, 1).round(3)

cancel_rate = (df.groupby("field_id")
               .apply(lambda x: (x["status"]=="CANCELLED").sum() / len(x))
               .reset_index(name="cancellation_rate"))
df = df.merge(cancel_rate, on="field_id")
df["cancellation_rate"] = df["cancellation_rate"].round(3)

def compute_suggested_price(row):
    p = row["base_price_per_hour"]
    if row["is_weekend"]:       p *= 1.15
    if row["is_peak_hour"]:     p *= 1.20
    if row["occupation_rate"] > 0.8:  p *= 1.10
    elif row["occupation_rate"] < 0.3: p *= 0.90
    if row["sport_type"] in ["PADEL","TENNIS"]: p *= 1.05
    if row["season"] == "summer":  p *= 1.10
    elif row["season"] == "winter": p *= 0.95
    p *= np.random.uniform(0.95, 1.05)
    return round(p, 2)

df["suggested_price"] = df.apply(compute_suggested_price, axis=1)

dataset = df[["reservation_id","field_id","sport_type","location","capacity",
              "base_price_per_hour","duration_hours","day_of_week","hour_of_day",
              "is_weekend","is_peak_hour","season","month","occupation_rate",
              "cancellation_rate","payment_method","payment_status","status","suggested_price"]]

dataset.to_csv("streetleague_dynamic_pricing.csv", index=False)
print(f"✅ Dataset généré : {len(dataset)} lignes")

# ── Prétraitement ───────────────────────────────────────────────────────
print("Entraînement du modèle...")
df_ml = dataset.drop(columns=["reservation_id","field_id"])
CAT_COLS = ["sport_type","location","season","payment_method","payment_status","status"]

le_dict = {}
for col in CAT_COLS:
    le = LabelEncoder()
    df_ml[col] = le.fit_transform(df_ml[col].astype(str))
    le_dict[col] = le

X = df_ml.drop(columns=["suggested_price"])
y = df_ml["suggested_price"]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
scaler     = StandardScaler()
X_train_sc = scaler.fit_transform(X_train)
X_test_sc  = scaler.transform(X_test)

model = GradientBoostingRegressor(n_estimators=100, learning_rate=0.1, random_state=42)
model.fit(X_train, y_train)

y_pred = model.predict(X_test)
print(f"  MAE : {mean_absolute_error(y_test, y_pred):.4f}")
print(f"  R²  : {r2_score(y_test, y_pred):.4f}")

# ── Sauvegarde dans models/ ─────────────────────────────────────────────
os.makedirs("models", exist_ok=True)
joblib.dump(model,           "models/dynamic_pricing_model.pkl")
joblib.dump(scaler,          "models/scaler.pkl")
joblib.dump(le_dict,         "models/label_encoders.pkl")
joblib.dump(list(X.columns), "models/feature_names.pkl")

print("✅ Fichiers sauvegardés dans models/")
print("   - dynamic_pricing_model.pkl")
print("   - scaler.pkl")
print("   - label_encoders.pkl")
print("   - feature_names.pkl")
print("\nLance maintenant : python app.py")
