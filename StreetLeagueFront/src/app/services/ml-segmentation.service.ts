import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface CommunityData {
  community_name: string;
  community_type: 'sportive' | 'culturelle' | 'sociale';
  total_events: number;
  events_last_30_days: number;
  events_last_90_days: number;
  unique_organizers: number;
  community_age_days: number;
  last_event_days_ago: number;
}

export interface PredictionResult {
  community_name: string;
  predicted_cluster: number;
  cluster_type: 'DORMANT' | 'MODERATE' | 'ULTRA_ACTIVE';
  confidence_score: number;
  activity_level: number;
  recommended_actions: string[];
}

export interface ModelInfo {
  model_type: string;
  algorithm: string;
  n_clusters: number;
  features_used: string[];
  model_version: string;
  training_date: string;
  accuracy_metrics: any;
}

export interface ClusterInfo {
  cluster_id: number;
  cluster_type: string;
  description: string;
  characteristics: string[];
  recommended_actions: string[];
  avg_activity_level: number;
  community_count: number;
}

export interface HealthStatus { status: 'healthy' | 'unhealthy';
  timestamp: string;
  models_loaded: boolean;
  api_version: string;
}

@Injectable({
  providedIn: 'root'
})
export class MlSegmentationService {
  private readonly apiUrl = environment.mlApiUrl || 'http://localhost:8000';

  constructor(private http: HttpClient) {}

  /**
   * Prédire le cluster pour une community
   */
  predictCommunityCluster(communityData: CommunityData): Observable<PredictionResult> {
    return this.http.post<PredictionResult>(`${this.apiUrl}/predict`, communityData);
  }

  /**
   * Prédire pour plusieurs communities (batch)
   */
  predictBatch(communities: CommunityData[]): Observable<PredictionResult[]> {
    return this.http.post<any>(`${this.apiUrl}/predict/batch`, { communities })
      .pipe(
        map(response => response.predictions || [])
      );
  }

  /**
   * Obtenir les informations sur le modèle
   */
  getModelInfo(): Observable<ModelInfo> {
    return this.http.get<ModelInfo>(`${this.apiUrl}/model/info`);
  }

  /**
   * Obtenir les details de all clusters
   */
  getClusterDetails(): Observable<ClusterInfo[]> {
    return this.http.get<ClusterInfo[]>(`${this.apiUrl}/clusters`);
  }

  /**
   * Obtenir les details d'un cluster spécifique
   */
  getClusterById(clusterId: number): Observable<ClusterInfo> {
    return this.http.get<ClusterInfo>(`${this.apiUrl}/clusters/${clusterId}`);
  }

  /**
   * Vérifier le status de health de l'API
   */
  getHealthStatus(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${this.apiUrl}/health`);
  }

  /**
   * Obtenir les statistics du service
   */
  getServiceStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }

  /**
   * Obtenir l'historique des prédictions (si disponible)
   */
  getPredictionHistory(limit?: number): Observable<any[]> {
    const params = limit ? new HttpParams().set('limit', limit.toString()) : undefined;
    return this.http.get<any[]>(`${this.apiUrl}/predictions/history`, { params });
  }

  /**
   * Exporter les résultats de prédiction
   */
  exportPredictions(format: 'csv' | 'json' = 'csv'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export/${format}`, {
      responseType: 'blob'
    });
  }

  /**
   * Validate les données avant prédiction
   */
  validateCommunityData(data: CommunityData): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];

    if (!data.community_name || data.community_name.trim().length === 0) {
      errors.push('Le name de la community est requis');
    }

    if (!['sportive', 'culturelle', 'sociale'].includes(data.community_type)) {
      errors.push('Le type de community doit être: sportive, culturelle, ou sociale');
    }

    if (data.total_events < 0) {
      errors.push('Le namebre total d\'events doit être positif');
    }

    if (data.events_last_30_days < 0 || data.events_last_30_days > data.total_events) {
      errors.push('Le namebre d\'events des 30 derniers days est invalide');
    }

    if (data.events_last_90_days < 0 || data.events_last_90_days > data.total_events) {
      errors.push('Le namebre d\'events des 90 derniers days est invalide');
    }

    if (data.unique_organizers < 0 || data.unique_organizers > data.total_events) {
      errors.push('Le namebre d\'organisateurs uniques est invalide');
    }

    if (data.community_age_days < 0) {
      errors.push('L\'âge de la community doit être positif');
    }

    if (data.last_event_days_ago < 0) {
      errors.push('Le namebre de days depuis le dernier event doit être positif');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }
}


