from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import requests
from datetime import datetime

app = Flask(__name__)
CORS(app)

SPRING_BOOT_URL = "http://localhost:8080/api"

MOCK_FIELDS = [
    {"id": 1, "name": "Stade Olympique", "lat": 36.8305, "lng": 10.1855,
     "location": "Tunis Centre", "sportType": "Football",
     "capacity": 5000, "pricePerHour": 50.0, "isAvailable": True, "pressure": 0.3},
    {"id": 2, "name": "Tennis Club", "lat": 36.8450, "lng": 10.1900,
     "location": "La Marsa", "sportType": "Tennis",
     "capacity": 4, "pricePerHour": 20.0, "isAvailable": True, "pressure": 0.2},
    {"id": 3, "name": "Piscine Municipale", "lat": 36.8200, "lng": 10.1700,
     "location": "El Menzah", "sportType": "Swimming",
     "capacity": 100, "pricePerHour": 15.0, "isAvailable": True, "pressure": 0.4},
]


def haversine(lat1, lon1, lat2, lon2):
    R = 6371
    lat1, lon1, lat2, lon2 = map(np.radians, [lat1, lon1, lat2, lon2])
    dlat = lat2 - lat1
    dlon = lon2 - lon1
    a = np.sin(dlat / 2) ** 2 + np.cos(lat1) * np.cos(lat2) * np.sin(dlon / 2) ** 2
    return R * 2 * np.arcsin(np.sqrt(a))


def get_fields_from_spring():
    try:
        r = requests.get(f"{SPRING_BOOT_URL}/fields", timeout=5)
        if r.status_code == 200:
            return r.json()
    except Exception:
        pass
    return MOCK_FIELDS


def score_field(field, ref_lat, ref_lng):
    """Score a field using heuristic neural-network-style logic (0-100)."""
    score = 50.0
    distance = 0.0

    field_lat = field.get('lat') or field.get('latitude')
    field_lng = field.get('lng') or field.get('longitude')

    if ref_lat and ref_lng and field_lat and field_lng:
        distance = haversine(float(ref_lat), float(ref_lng),
                             float(field_lat), float(field_lng))
        # Distance scoring: 0 km = +50, 25 km = 0
        score += max(0, 50 - (distance * 2))

    # Availability
    if not field.get('isAvailable', True):
        score -= 25

    # Pressure (0-1, higher = more crowded = worse)
    pressure = float(field.get('pressure', 0))
    score -= pressure * 15

    # Capacity bonus
    capacity = int(field.get('capacity', 0))
    if capacity > 1000:
        score += 15
    elif capacity > 100:
        score += 10
    elif capacity > 20:
        score += 5

    score = round(min(max(score, 0), 100), 2)
    ai_score = round(score / 100, 2)

    if ai_score >= 0.75:
        rec = "EXCELLENT"
    elif ai_score >= 0.50:
        rec = "ACCEPTABLE"
    else:
        rec = "DECONSEILLE"

    location = field.get('location') or f"Lat {field_lat}, Lng {field_lng}"

    return {
        "fieldId":        field.get('id', 0),
        "fieldName":      field.get('name', 'Unknown'),
        "fieldLocation":  location,
        "fieldCapacity":  capacity,
        "pricePerHour":   float(field.get('pricePerHour', 0)),
        "fieldAvailable": bool(field.get('isAvailable', True)),
        "aiScore":        ai_score,
        "recommendation": rec,
        "distanceKm":     round(distance, 2),
        "weather":        "clear",
        "weatherScore":   1.0,
        "bestSlots":      []
    }


# ── Health ─────────────────────────────────────────────────────────────
@app.route('/api/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "service": "AI Recommendation Service",
        "timestamp": datetime.now().isoformat()
    })


# ── Field Recommendations ──────────────────────────────────────────────
@app.route('/api/recommend/fields', methods=['POST'])
def recommend_fields():
    try:
        data = request.get_json(force=True, silent=True) or {}

        user_lat = data.get('userLat') or data.get('latitude')
        user_lng = data.get('userLng') or data.get('longitude')

        # Use match/event location if provided, fallback to user location
        ref_lat = data.get('eventLat') or user_lat
        ref_lng = data.get('eventLng') or user_lng

        fields = data.get('fields')
        if not fields:
            fields = get_fields_from_spring()

        if not fields:
            return jsonify({"error": "Aucun terrain disponible"}), 404

        recommendations = [score_field(f, ref_lat, ref_lng) for f in fields]
        recommendations.sort(key=lambda x: x['aiScore'], reverse=True)

        return jsonify(recommendations[:10])

    except Exception as e:
        import traceback
        traceback.print_exc()
        return jsonify({"error": str(e)}), 500


# ── Slot Recommendations ───────────────────────────────────────────────
@app.route('/api/recommend/slots', methods=['POST'])
def recommend_slots():
    try:
        data = request.get_json(force=True, silent=True) or {}
        field_id = data.get('fieldId')
        date = data.get('date', datetime.now().strftime('%Y-%m-%d'))

        if not field_id:
            return jsonify({"error": "fieldId is required"}), 400

        time_slots = []
        for hour in range(8, 22):
            if 18 <= hour <= 21:
                score = 95
            elif 14 <= hour <= 17:
                score = 88
            elif 10 <= hour <= 13:
                score = 72
            elif hour == 8 or hour == 9:
                score = 55
            else:
                score = 40

            ai = round(score / 100, 2)
            rec = "EXCELLENT" if ai >= 0.75 else "ACCEPTABLE" if ai >= 0.50 else "DECONSEILLE"

            time_slots.append({
                "startTime": f"{hour:02d}:00",
                "endTime":   f"{hour + 1:02d}:00",
                "label":     f"{hour:02d}h00 – {hour + 1:02d}h00",
                "dayName":   datetime.now().strftime('%A'),
                "hour":      hour,
                "isAvailable": True,
                "score":     ai,
                "rec":       rec,
                "recommendationScore": score
            })

        time_slots.sort(key=lambda x: x['score'], reverse=True)

        return jsonify(time_slots[:5])

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# ── Week Schedule ──────────────────────────────────────────────────────
@app.route('/api/schedule/week', methods=['POST'])
def get_week_schedule():
    try:
        data = request.get_json(force=True, silent=True) or {}

        events = data.get('events', [])
        fields = data.get('fields', [])
        user_lat = data.get('userLat')
        user_lng = data.get('userLng')

        top_fields = []
        if user_lat and user_lng and fields:
            scored = [score_field(f, user_lat, user_lng) for f in fields]
            scored.sort(key=lambda x: x['aiScore'], reverse=True)
            top_fields = scored[:3]

        return jsonify({'events': events, 'topFields': top_fields})

    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    print("=" * 60)
    print("  AI Recommendation Service  —  StreetLeague")
    print("=" * 60)
    print("  Flask : http://localhost:5001")
    print("  POST   /api/recommend/fields")
    print("  POST   /api/recommend/slots")
    print("  POST   /api/schedule/week")
    print("  GET    /api/health")
    print("=" * 60)
    app.run(host='0.0.0.0', port=5001, debug=True)