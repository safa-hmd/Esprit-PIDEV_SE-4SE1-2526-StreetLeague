from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import numpy as np
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Diet Recommendation API")

# CORS pour Angular
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Charger les modèles
model = joblib.load("diet_model.pkl")
scaler = joblib.load("scaler.pkl")
goal_encoder = joblib.load("goal_encoder.pkl")

# Modèle Pydantic pour validation des données
class DietRequest(BaseModel):
    age: int
    bmi: float

class DietResponse(BaseModel):
    goal: str
    status: str
    daily_calories: int
    diet_plan: list[str]

# Plans de repas
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

@app.get("/")
def root():
    return {"message": "Diet Recommendation API", "docs": "/docs"}

@app.post("/predict", response_model=DietResponse)
async def predict(request: DietRequest):
    try:
        # Normaliser
        input_scaled = scaler.transform([[request.age, request.bmi]])
        
        # Prédire
        prediction = model.predict(input_scaled)[0]
        goal = goal_encoder.inverse_transform([prediction])[0]
        
        # Récupérer le plan
        result = diet_plans[goal]
        
        return DietResponse(
            goal=goal,
            status=result["status"],
            daily_calories=result["calories"],
            diet_plan=result["plan"]
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
def health():
    return {"status": "healthy"}

# Pour lancer avec uvicorn directement
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)