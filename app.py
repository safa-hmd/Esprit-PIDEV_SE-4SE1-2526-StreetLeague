from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import joblib
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler, LabelEncoder

# Initialiser l'application FastAPI
app = FastAPI(title="StreetLeague ML API", version="1.0.0")

# Configurer CORS pour permettre les requêtes Angular
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],  # Angular frontend
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["*"],
)

# Modèles Pydantic pour les requêtes et réponses
class CommunityRequest(BaseModel):
    community_name: str
    community_type: str  # "sportive", "culturelle", "sociale"
    total_events: int
    events_last_30_days: int
    events_last_90_days: int
    unique_organizers: int
    community_age_days: int
    last_event_days_ago: int

class CommunityResponse(BaseModel):
    community_name: str
    predicted_cluster: int
    cluster_type: str
    confidence_score: float
    activity_level: float
    recommended_actions: List[str]

# Variables globales pour les modèles
model = None
scaler = None
label_encoder = None

# Mapping des clusters
CLUSTER_MAPPING = {
    0: "DORMANT",
    1: "MODERATE", 
    2: "ULTRA_ACTIVE"
}

# Actions recommandées par cluster
CLUSTER_ACTIONS = {
    0: [
        "Relance prioritaire",
        "Aide active", 
        "Support personnalisé",
        "Formation organisateurs"
    ],
    1: [
        "Support standard",
        "Encouragement",
        "Ressources additionnelles",
        "Marketing ciblé"
    ],
    2: [
        "Marketing premium",
        "Support prioritaire",
        "Événements exclusifs",
        "Programme ambassadeur"
    ]
}

@app.on_event("startup")
async def load_models():
    """Charger les modèles au démarrage"""
    global model, scaler, label_encoder
    
    try:
        # Charger les modèles depuis les fichiers .pkl
        model = joblib.load('kmeans_model_1000_rows.pkl')
        scaler = joblib.load('scaler_1000_rows.pkl')
        label_encoder = joblib.load('label_encoder_1000_rows.pkl')
        
        print("✅ Modèles chargés avec succès")
    except FileNotFoundError:
        print("⚠️ Fichiers .pkl non trouvés, utilisation de modèles de démonstration")
        # Créer des modèles de démonstration
        from sklearn.cluster import KMeans
        
        model = KMeans(n_clusters=3, random_state=42)
        scaler = StandardScaler()
        label_encoder = LabelEncoder()
        
        # Entraîner avec des données de démonstration
        demo_data = np.array([
            [150, 15, 45, 12, 5.0, 200, 3, 0.08, 25.0, 2],
            [25, 3, 8, 3, 1.2, 100, 15, 0.12, 5.0, 1],
            [5, 0, 1, 1, 0.3, 50, 60, 0.20, 0.5, 0]
        ])
        
        model.fit(demo_data)
        scaler.fit(demo_data)
        label_encoder.fit(['sportive', 'culturelle', 'sociale'])
        
        print("✅ Modèles de démonstration créés")
    except Exception as e:
        print(f"❌ Erreur lors du chargement: {e}")
        raise HTTPException(status_code=500, detail="Impossible de charger les modèles")

def calculate_derived_features(data: dict) -> dict:
    """Calculer les features dérivées"""
    total_events = data['total_events']
    community_age_days = data['community_age_days']
    unique_organizers = data['unique_organizers']
    events_last_30_days = data['events_last_30_days']
    events_last_90_days = data['events_last_90_days']
    
    # Calcul des features dérivées
    avg_events_per_month = total_events / max(community_age_days / 30, 1)
    diversity_score = unique_organizers / max(total_events, 1)
    activity_level = (events_last_30_days * 3 + events_last_90_days) / 4
    
    return {
        'avg_events_per_month': round(avg_events_per_month, 2),
        'diversity_score': round(diversity_score, 3),
        'activity_level': round(activity_level, 2)
    }

def prepare_features(data: dict) -> np.ndarray:
    """Préparer les features pour la prédiction"""
    # Calculer les features dérivées
    derived = calculate_derived_features(data)
    
    # Encoder le type de communauté
    try:
        community_type_encoded = label_encoder.transform([data['community_type']])[0]
    except:
        # Valeur par défaut si l'encodage échoue
        type_mapping = {'sportive': 0, 'culturelle': 1, 'sociale': 2}
        community_type_encoded = type_mapping.get(data['community_type'].lower(), 0)
    
    # Préparer le vecteur de features
    features = np.array([[
        data['total_events'],
        data['events_last_30_days'],
        data['events_last_90_days'],
        data['unique_organizers'],
        derived['avg_events_per_month'],
        data['community_age_days'],
        data['last_event_days_ago'],
        derived['diversity_score'],
        derived['activity_level'],
        community_type_encoded
    ]])
    
    return features

@app.get("/")
async def root():
    """Endpoint racine"""
    return {
        "message": "StreetLeague ML API - Segmentation des Communautés",
        "version": "1.0.0",
        "status": "running",
        "endpoints": {
            "predict": "/predict",
            "health": "/health",
            "docs": "/docs"
        }
    }

@app.get("/health")
async def health_check():
    """Vérifier la santé du service"""
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "scaler_loaded": scaler is not None,
        "encoder_loaded": label_encoder is not None
    }

@app.post("/predict", response_model=CommunityResponse)
async def predict_community_cluster(community: CommunityRequest):
    """
    Prédire le cluster pour une communauté
    
    Exemple de requête:
    {
        "community_name": "StreetBasket Elite",
        "community_type": "sportive",
        "total_events": 156,
        "events_last_30_days": 18,
        "events_last_90_days": 52,
        "unique_organizers": 15,
        "community_age_days": 245,
        "last_event_days_ago": 3
    }
    """
    try:
        # Préparer les features
        features = prepare_features(community.dict())
        
        # Standardiser les features
        features_scaled = scaler.transform(features)
        
        # Prédire le cluster
        cluster = model.predict(features_scaled)[0]
        
        # Calculer la confiance
        distances = model.transform(features_scaled)[0]
        min_distance = distances[cluster]
        max_distance = np.max(distances)
        confidence = 1 - (min_distance / max_distance) if max_distance > 0 else 0.5
        
        # Obtenir les features dérivées
        derived = calculate_derived_features(community.dict())
        
        # Construire la réponse
        response = CommunityResponse(
            community_name=community.community_name,
            predicted_cluster=int(cluster),
            cluster_type=CLUSTER_MAPPING.get(cluster, "UNKNOWN"),
            confidence_score=round(confidence, 3),
            activity_level=derived['activity_level'],
            recommended_actions=CLUSTER_ACTIONS.get(cluster, ["Aucune action recommandée"])
        )
        
        return response
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur de prédiction: {str(e)}")

@app.post("/predict/batch")
async def predict_batch_communities(request: dict):
    """Prédire les clusters pour plusieurs communautés"""
    try:
        # Extraire la liste des communautés depuis le corps de la requête
        communities_data = request.get("communities", [])
        
        # Valider et convertir en CommunityRequest
        communities = []
        for comm_data in communities_data:
            community = CommunityRequest(**comm_data)
            communities.append(community)
        
        results = []
        
        for community in communities:
            # Préparer les features
            features = prepare_features(community.dict())
            
            # Standardiser les features
            features_scaled = scaler.transform(features)
            
            # Prédire le cluster
            cluster = model.predict(features_scaled)[0]
            
            # Calculer la confiance
            distances = model.transform(features_scaled)[0]
            min_distance = distances[cluster]
            max_distance = np.max(distances)
            confidence = 1 - (min_distance / max_distance) if max_distance > 0 else 0.5
            
            # Obtenir les features dérivées
            derived = calculate_derived_features(community.dict())
            
            # Construire la réponse
            response = CommunityResponse(
                community_name=community.community_name,
                predicted_cluster=int(cluster),
                cluster_type=CLUSTER_MAPPING.get(cluster, "UNKNOWN"),
                confidence_score=round(confidence, 3),
                activity_level=derived['activity_level'],
                recommended_actions=CLUSTER_ACTIONS.get(cluster, ["Aucune action recommandée"])
            )
            
            results.append(response)
        
        return {
            "predictions": results,
            "total_processed": len(communities),
            "cluster_distribution": {
                "DORMANT": sum(1 for r in results if r.cluster_type == "DORMANT"),
                "MODERATE": sum(1 for r in results if r.cluster_type == "MODERATE"),
                "ULTRA_ACTIVE": sum(1 for r in results if r.cluster_type == "ULTRA_ACTIVE")
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur de prédiction batch: {str(e)}")

@app.get("/clusters")
async def get_all_clusters():
    """Obtenir les informations sur tous les clusters"""
    clusters_info = []
    
    for cluster_id in [0, 1, 2]:
        cluster_type = CLUSTER_MAPPING[cluster_id]
        
        cluster_info = {
            "cluster_id": cluster_id,
            "cluster_type": cluster_type,
            "recommended_actions": CLUSTER_ACTIONS[cluster_id],
            "description": {
                "DORMANT": "Communautés peu actives avec moins de 2 événements par mois",
                "MODERATE": "Communautés moyennement actives avec 2-8 événements par mois",
                "ULTRA_ACTIVE": "Communautés très actives avec plus de 8 événements par mois"
            }.get(cluster_type, "Type de cluster inconnu"),
            "characteristics": [
                "Faible niveau d'activité",
                "Peu d'événements récents",
                "Besoin de relance prioritaire"
            ] if cluster_id == 0 else [
                "Niveau d'activité modéré",
                "Événements réguliers",
                "Potentiel de croissance"
            ] if cluster_id == 1 else [
                "Niveau d'activité très élevé",
                "Événements fréquents",
                "Performance excellente"
            ],
            "avg_activity_level": 25.0 if cluster_id == 0 else 55.0 if cluster_id == 1 else 85.0,
            "community_count": 333 if cluster_id == 0 else 334 if cluster_id == 1 else 333
        }
        
        clusters_info.append(cluster_info)
    
    return clusters_info

@app.get("/clusters/{cluster_id}")
async def get_cluster_info(cluster_id: int):
    """Obtenir les informations sur un cluster spécifique"""
    if cluster_id not in [0, 1, 2]:
        raise HTTPException(status_code=400, detail="Cluster ID doit être 0, 1, ou 2")
    
    cluster_type = CLUSTER_MAPPING[cluster_id]
    
    return {
        "cluster_id": cluster_id,
        "cluster_type": cluster_type,
        "recommended_actions": CLUSTER_ACTIONS[cluster_id],
        "description": {
            "DORMANT": "Communautés peu actives avec moins de 2 événements par mois",
            "MODERATE": "Communautés moyennement actives avec 2-8 événements par mois",
            "ULTRA_ACTIVE": "Communautés très actives avec plus de 8 événements par mois"
        }.get(cluster_type, "Type de cluster inconnu")
    }

@app.get("/model/info")
async def get_model_info():
    """Obtenir les informations sur le modèle"""
    return {
        "model_name": "K-Means Community Segmentation",
        "version": "1.0.0",
        "n_clusters": 3,
        "features": [
            "total_events",
            "events_last_30_days", 
            "events_last_90_days",
            "unique_organizers",
            "avg_events_per_month",
            "community_age_days",
            "last_event_days_ago",
            "diversity_score",
            "activity_level",
            "community_type_encoded"
        ],
        "cluster_mapping": CLUSTER_MAPPING,
        "model_loaded": model is not None
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
