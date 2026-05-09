import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

export interface HealthStatus {
  status: 'healthy' | 'unhealthy';
  timestamp: string;
  models_loaded: boolean;
  api_version: string;
}

@Injectable({
  providedIn: 'root'
})
export class MlSegmentationService {
  private readonly apiUrl = 'http://localhost:8000';

  constructor(private http: HttpClient) {}

  predictCommunityCluster(communityData: CommunityData): Observable<PredictionResult> {
    return this.http.post<PredictionResult>(`${this.apiUrl}/predict`, communityData);
  }

  predictBatch(communities: CommunityData[]): Observable<PredictionResult[]> {
    return this.http.post<any>(`${this.apiUrl}/predict/batch`, { communities })
      .pipe(map(response => response.predictions || []));
  }

  getModelInfo(): Observable<ModelInfo> {
    return this.http.get<ModelInfo>(`${this.apiUrl}/model/info`);
  }

  getClusterDetails(): Observable<ClusterInfo[]> {
    return this.http.get<ClusterInfo[]>(`${this.apiUrl}/clusters`);
  }

  getClusterById(clusterId: number): Observable<ClusterInfo> {
    return this.http.get<ClusterInfo>(`${this.apiUrl}/clusters/${clusterId}`);
  }

  getHealthStatus(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${this.apiUrl}/health`);
  }

  getServiceStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }

  getPredictionHistory(limit?: number): Observable<any[]> {
    const params = limit ? new HttpParams().set('limit', limit.toString()) : undefined;
    return this.http.get<any[]>(`${this.apiUrl}/predictions/history`, { params });
  }

  exportPredictions(format: 'csv' | 'json' = 'csv'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export/${format}`, { responseType: 'blob' });
  }

  validateCommunityData(data: CommunityData): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];
    if (!data.community_name || data.community_name.trim().length === 0)
      errors.push('Le name de la community est requis');
    if (!['sportive', 'culturelle', 'sociale'].includes(data.community_type))
      errors.push('Le type de community doit être: sportive, culturelle, ou sociale');
    if (data.total_events < 0)
      errors.push('Le nombre total d\'events doit être positif');
    if (data.events_last_30_days < 0 || data.events_last_30_days > data.total_events)
      errors.push('Le nombre d\'events des 30 derniers days est invalide');
    if (data.events_last_90_days < 0 || data.events_last_90_days > data.total_events)
      errors.push('Le nombre d\'events des 90 derniers days est invalide');
    if (data.unique_organizers < 0 || data.unique_organizers > data.total_events)
      errors.push('Le nombre d\'organisateurs uniques est invalide');
    if (data.community_age_days < 0)
      errors.push('L\'âge de la community doit être positif');
    if (data.last_event_days_ago < 0)
      errors.push('Le nombre de days depuis le dernier event doit être positif');
    return { isValid: errors.length === 0, errors };
  }
}