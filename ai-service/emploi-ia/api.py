from flask import Flask, request, jsonify
import joblib
import pandas as pd

app    = Flask(__name__)
model  = joblib.load("calendar_model.pkl")
scaler = joblib.load("scaler.pkl")
print("Modele charge, API prete sur port 5001 !")

def build_features(d):
    mw = d["matchesWeek"]
    tw = d["trainingsWeek"]
    pc = d["playerCount"]
    fc = d["fieldCapacity"]
    v  = d.get("victories", 10)
    de = d.get("defeats", 5)
    return pd.DataFrame([{
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
    }])

@app.route("/predict", methods=["POST"])
def predict():
    data     = request.json
    features = build_features(data)
    scaled   = scaler.transform(features)
    proba    = model.predict_proba(scaled)[0][1]
    score    = round(float(proba), 4)

    if score >= 0.75:  rec = "EXCELLENT"
    elif score >= 0.5: rec = "ACCEPTABLE"
    else:              rec = "DECONSEILLE"

    return jsonify({
        "score":          score,
        "recommendation": rec,
        "confidence":     f"{score*100:.1f}%"
    })

@app.route("/suggest", methods=["POST"])
def suggest():
    slots   = request.json["slots"]
    results = []
    for slot in slots:
        features = build_features(slot)
        scaled   = scaler.transform(features)
        proba    = model.predict_proba(scaled)[0][1]
        results.append({**slot, "score": round(float(proba), 4)})
    best = sorted(results, key=lambda x: x["score"], reverse=True)[:3]
    return jsonify({"bestSlots": best, "total": len(slots)})

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "model": "calendar_model.pkl"})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)