from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import numpy as np
import pandas as pd
import os

app = Flask(__name__)
CORS(app)

# ── Chargement des modèles ─────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

model     = joblib.load(os.path.join(BASE_DIR, "models", "dynamic_pricing_model.pkl"))
le_dict   = joblib.load(os.path.join(BASE_DIR, "models", "label_encoders.pkl"))
scaler    = joblib.load(os.path.join(BASE_DIR, "models", "scaler.pkl"))
features  = joblib.load(os.path.join(BASE_DIR, "models", "feature_names.pkl"))

print("✅ Modèle chargé :", type(model).__name__)
print("✅ Features attendues :", features)

# ── Champs catégoriels ─────────────────────────────────────────────────
CAT_COLS = ["sport_type", "location", "season", "payment_method", "payment_status", "status"]

# ── Valeurs valides par champ (pour validation) ────────────────────────
VALID_VALUES = {
    "sport_type":     ["FOOTBALL", "BASKETBALL", "TENNIS", "PADEL", "VOLLEYBALL", "OTHER"],
    "season":         ["spring", "summer", "autumn", "winter"],
    "payment_method": ["CARD", "CASH", "ONLINE", "NONE"],
    "payment_status": ["PAID", "PENDING", "REFUNDED", "FAILED", "NONE"],
    "status":         ["APPROVED", "PENDING", "CANCELLED", "REJECTED"],
}


def get_season(month: int) -> str:
    if month in [12, 1, 2]:
        return "winter"
    if month in [3, 4, 5]:
        return "spring"
    if month in [6, 7, 8]:
        return "summer"
    return "autumn"


def validate_input(data: dict) -> list:
    errors = []
    required = [
        "sport_type", "location", "capacity", "base_price_per_hour",
        "duration_hours", "day_of_week", "hour_of_day",
        "is_weekend", "is_peak_hour", "month",
        "occupation_rate", "cancellation_rate",
    ]
    for field in required:
        if field not in data:
            errors.append(f"Champ manquant : {field}")

    for field, valid in VALID_VALUES.items():
        if field in data and data[field] not in valid:
            errors.append(f"Valeur invalide pour {field} : {data[field]}. Attendu : {valid}")

    if "occupation_rate" in data and not (0 <= data["occupation_rate"] <= 1):
        errors.append("occupation_rate doit être entre 0 et 1")
    if "cancellation_rate" in data and not (0 <= data["cancellation_rate"] <= 1):
        errors.append("cancellation_rate doit être entre 0 et 1")
    if "base_price_per_hour" in data and data["base_price_per_hour"] <= 0:
        errors.append("base_price_per_hour doit être > 0")

    return errors


def preprocess(data: dict) -> pd.DataFrame:
    # Dériver season depuis month si non fourni
    if "season" not in data:
        data["season"] = get_season(data.get("month", 6))

    # Valeurs par défaut pour payment (pas encore payé lors d'une estimation)
    data.setdefault("payment_method", "NONE")
    data.setdefault("payment_status", "NONE")
    data.setdefault("status", "PENDING")

    df = pd.DataFrame([data])

    for col in CAT_COLS:
        if col in df.columns:
            val = df[col].iloc[0]
            if val in le_dict[col].classes_:
                df[col] = le_dict[col].transform([val])
            else:
                df[col] = 0  # valeur inconnue → classe 0

    df = df[features]
    return df


# ── ROUTES ─────────────────────────────────────────────────────────────

@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "model": type(model).__name__,
        "features_count": len(features),
    }), 200


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(silent=True)
    if not data:
        return jsonify({"error": "Body JSON manquant"}), 400

    errors = validate_input(data)
    if errors:
        return jsonify({"errors": errors}), 422

    try:
        df_input = preprocess(data)
        predicted = model.predict(df_input)[0]
        predicted = round(float(predicted), 2)

        return jsonify({
            "suggested_price": predicted,
            "base_price":      data["base_price_per_hour"],
            "delta_percent":   round(((predicted / data["base_price_per_hour"]) - 1) * 100, 1),
            "model":           type(model).__name__,
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/features", methods=["GET"])
def get_features():
    """Retourne la liste des features attendues — utile pour le debug Spring Boot"""
    return jsonify({"features": features, "categorical": CAT_COLS}), 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
