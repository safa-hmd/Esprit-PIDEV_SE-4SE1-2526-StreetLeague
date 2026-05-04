from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import joblib
import numpy as np
from fastapi.middleware.cors import CORSMiddleware
import traceback

app = FastAPI(title="StreetLeague Recommender API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Charger le modèle + la matrice d'entraînement
model = None
user_indices = None
product_cols = None
scaler = None
X_train = None  # ✅ Nouvelle variable pour la matrice

try:
    print("📦 Chargement de recommender.joblib...")
    # 5 éléments maintenant : + X_train_scaled
    model, user_indices, product_cols, scaler, X_train = joblib.load("recommender.joblib")
    print(f"✅ Modèle chargé : {len(user_indices)} utilisateurs, {len(product_cols)} produits")
except Exception as e:
    print(f"❌ ERREUR : {e}")
    traceback.print_exc()

class RecommendRequest(BaseModel):
    user_id: int
    top_k: int = 5
    exclude_ids: List[int] = []

class RecommendResponse(BaseModel):
    user_id: int
    recommendations: List[int]
    confidence: float = 0.0

@app.get("/health")
def health():
    return {
        "status": "ok",
        "model_loaded": model is not None,
        "users": len(user_indices) if user_indices else 0,
        "products": len(product_cols) if product_cols else 0
    }

@app.post("/recommend", response_model=RecommendResponse)
def recommend(req: RecommendRequest):
    try:
        print(f"\n🔍 Requête : user_id={req.user_id}, top_k={req.top_k}")

        if model is None or X_train is None:
            raise HTTPException(status_code=503, detail="Modèle ou données d'entraînement non chargés")

        # Cold Start
        if req.user_id not in user_indices:
            print(f"⚠️ Cold Start pour user {req.user_id}")
            recs = [p for p in product_cols[:req.top_k] if p not in req.exclude_ids]
            return RecommendResponse(user_id=req.user_id, recommendations=recs, confidence=0.0)

        # Utilisateur connu
        u_idx = user_indices.index(req.user_id)
        user_vec = X_train[u_idx].reshape(1, -1)  # ✅ Utiliser X_train au lieu de model._fit_data

        # Trouver les voisins similaires
        distances, sim_indices = model.kneighbors(user_vec, n_neighbors=req.top_k + 1)
        neighbor_idxs = sim_indices[0][1:]  # Exclure l'utilisateur lui-même

        if len(neighbor_idxs) == 0:
            return RecommendResponse(user_id=req.user_id, recommendations=[], confidence=0.0)

        # Agréger les scores des voisins (depuis X_train)
        neighbor_data = X_train[neighbor_idxs]
        scores = np.sum(neighbor_data, axis=0)

        # Exclure produits déjà achetés par l'utilisateur
        bought_idx = np.where(user_vec.flatten() > 0)[0]
        scores[bought_idx] = -1

        # Exclure les exclude_ids
        for pid in req.exclude_ids:
            if pid in product_cols:
                pid_idx = product_cols.index(pid)
                scores[pid_idx] = -1

        # Top K recommandations
        top_idxs = np.argsort(scores)[::-1][:req.top_k]
        final_recs = [product_cols[i] for i in top_idxs]

        # Confiance
        confidence = float(1 - np.mean(distances[0][1:])) if len(distances[0]) > 1 else 0.0

        print(f"✅ Réponses : {final_recs}")
        return RecommendResponse(user_id=req.user_id, recommendations=final_recs, confidence=round(confidence, 3))

    except HTTPException:
        raise
    except Exception as e:
        print(f"❌ ERREUR : {e}")
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))