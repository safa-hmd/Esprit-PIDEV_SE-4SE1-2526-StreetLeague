from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load model & scaler
model = joblib.load("diet_model.pkl")
scaler = joblib.load("scaler.pkl")
goal_encoder = joblib.load("goal_encoder.pkl")

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()

    age = data["age"]
    bmi = data["bmi"]

    # Scale input
    input_scaled = scaler.transform([[age, bmi]])

    # Predict
    prediction = model.predict(input_scaled)[0]
    goal = goal_encoder.inverse_transform([prediction])[0]

    # Diet plan suivant le goal
    diet_plans = {
        "GAIN_MUSCLE": {
            "status": "Underweight",
            "calories": 2800,
            "plan": [
                "Petit déjeuner: Flocons d'avoine + banane + lait",
                "Déjeuner: Poulet grillé + riz + légumes",
                "Dîner: Oeufs + pain complet + avocat",
                "Snack: Fruits secs + yaourt"
            ]
        },
        "LOSE_WEIGHT": {
            "status": "Overweight / Obesity",
            "calories": 1500,
            "plan": [
                "Petit déjeuner: Yaourt nature + fruits frais",
                "Déjeuner: Salade + thon + légumes vapeur",
                "Dîner: Soupe de légumes + pain complet",
                "Snack: Pomme + eau citronnée"
            ]
        },
        "MAINTAIN": {
            "status": "Normal",
            "calories": 2000,
            "plan": [
                "Petit déjeuner: Oeufs + pain complet + jus",
                "Déjeuner: Viande + couscous + salade",
                "Dîner: Poisson + riz + légumes",
                "Snack: Fruits + noix"
            ]
        }
    }

    result = diet_plans[goal]

    return jsonify({
        "goal": goal,
        "status": result["status"],
        "daily_calories": result["calories"],
        "diet_plan": result["plan"]
    })

if __name__ == "__main__":
    app.run(debug=True, port=5000)