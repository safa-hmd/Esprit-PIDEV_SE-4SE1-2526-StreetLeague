import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MlSegmentationService, CommunityData, PredictionResult, ClusterInfo, HealthStatus } from '../../services/ml-segmentation.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-ml-segmentation',
  templateUrl: './ml-segmentation.component.html',
  styleUrls: ['./ml-segmentation.component.scss']
})
export class MlSegmentationComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Formulaires
  predictionForm!: FormGroup;
  batchForm!: FormGroup;

  // États
  loading = false;
  error: string | null = null;
  success: string | null = null;

  // Données
  currentPrediction: PredictionResult | null = null;
  batchPredictions: PredictionResult[] = [];
  clusterInfo: ClusterInfo[] = [];
  healthStatus: HealthStatus | null = null;

  // Options pour les types de communities
  communityTypes = [
    { value: 'sportive', label: 'Sportive' },
    { value: 'culturelle', label: 'Culturelle' },
    { value: 'sociale', label: 'Sociale' }
  ];

  // Messages pour les clusters
  clusterMessages = {
    DORMANT: {
      titre: 'Community Dormante',
      color: 'danger',
      icon: 'fas fa-bed',
      description: 'Cette community a besoin d\'une relance prioritaire'
    },
    MODERATE: {
      titre: 'Community Modérée',
      color: 'warning',
      icon: 'fas fa-chart-line',
      description: 'Cette community a un potentiel de croissance'
    },
    ULTRA_ACTIVE: {
      titre: 'Community Ultra-Active',
      color: 'success',
      icon: 'fas fa-fire',
      description: 'Cette community est très performante'
    }
  };

  constructor(
    private fb: FormBuilder,
    private mlService: MlSegmentationService
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.loadInitialData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialiser les formulaires
   */
  private initializeForms(): void {
    // Formulaire de prédiction simple
    this.predictionForm = this.fb.group({
      community_name: ['', [Validators.required, Validators.minLength(2)]],
      community_type: ['sportive', Validators.required],
      total_events: [0, [Validators.required, Validators.min(0)]],
      events_last_30_days: [0, [Validators.required, Validators.min(0)]],
      events_last_90_days: [0, [Validators.required, Validators.min(0)]],
      unique_organizers: [0, [Validators.required, Validators.min(0)]],
      community_age_days: [0, [Validators.required, Validators.min(1)]],
      last_event_days_ago: [0, [Validators.required, Validators.min(0)]]
    });

    // Formulaire de prédiction par lot
    this.batchForm = this.fb.group({
      communities: this.fb.array([])
    });
  }

  /**
   * Charger les données initiales
   */
  private loadInitialData(): void {
    this.loadClusterInfo();
    this.checkHealthStatus();
  }

  /**
   * Charger les informations sur les clusters
   */
  private loadClusterInfo(): void {
    this.mlService.getClusterDetails()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clusters) => {
          this.clusterInfo = clusters;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des clusters:', err);
          this.error = 'Impossible de charger les informations sur les clusters';
        }
      });
  }

  /**
   * Vérifier le status de health de l'API
   */
  private checkHealthStatus(): void {
    this.mlService.getHealthStatus()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (status) => {
          this.healthStatus = status;
          if (status.status !== 'healthy') {
            this.error = 'L\'API ML n\'est pas disponible actuellement';
          }
        },
        error: (err) => {
          console.error('Erreur lors de la vérification de health:', err);
          this.error = 'Impossible de contacter l\'API ML';
        }
      });
  }

  /**
   * Effectuer une prédiction simple
   */
  predictCommunity(): void {
    if (this.predictionForm.invalid) {
      this.markFormGroupTouched(this.predictionForm);
      return;
    }

    const communityData: CommunityData = this.predictionForm.value;
    
    // Validation supplémentaire
    const validation = this.mlService.validateCommunityData(communityData);
    if (!validation.isValid) {
      this.error = validation.errors.join(', ');
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    this.mlService.predictCommunityCluster(communityData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.currentPrediction = result;
          this.success = 'Prédiction effectuée avec succès !';
          this.loading = false;
        },
        error: (err) => {
          console.error('Erreur lors de la prédiction:', err);
          this.error = 'Erreur lors de la prédiction: ' + (err.message || 'Erreur inconnue');
          this.loading = false;
        }
      });
  }

  /**
   * Add une community au formulaire de lot
   */
  addCommunityToBatch(): void {
    const communityGroup = this.fb.group({
      community_name: ['', [Validators.required, Validators.minLength(2)]],
      community_type: ['sportive', Validators.required],
      total_events: [0, [Validators.required, Validators.min(0)]],
      events_last_30_days: [0, [Validators.required, Validators.min(0)]],
      events_last_90_days: [0, [Validators.required, Validators.min(0)]],
      unique_organizers: [0, [Validators.required, Validators.min(0)]],
      community_age_days: [0, [Validators.required, Validators.min(1)]],
      last_event_days_ago: [0, [Validators.required, Validators.min(0)]]
    });

    this.communities.push(communityGroup);
  }

  /**
   * Delete une community du formulaire de lot
   */
  removeCommunityFromBatch(index: number): void {
    this.communities.removeAt(index);
  }

  /**
   * Effectuer des prédictions par lot
   */
  predictBatch(): void {
    if (this.batchForm.invalid) {
      this.markFormGroupTouched(this.batchForm);
      return;
    }

    const communities: CommunityData[] = this.batchForm.value.communities;
    
    // Validate all communities
    const validationErrors: string[] = [];
    communities.forEach((community, index) => {
      const validation = this.mlService.validateCommunityData(community);
      if (!validation.isValid) {
        validationErrors.push(`Community ${index + 1}: ${validation.errors.join(', ')}`);
      }
    });

    if (validationErrors.length > 0) {
      this.error = validationErrors.join('; ');
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    this.mlService.predictBatch(communities)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (results) => {
          this.batchPredictions = results;
          this.success = `${results.length} prédictions effectuées avec succès !`;
          this.loading = false;
        },
        error: (err) => {
          console.error('Erreur lors des prédictions par lot:', err);
          this.error = 'Erreur lors des prédictions par lot: ' + (err.message || 'Erreur inconnue');
          this.loading = false;
        }
      });
  }

  /**
   * Exporter les résultats
   */
  exportResults(format: 'csv' | 'json' = 'csv'): void {
    this.mlService.exportPredictions(format)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `ml_predictions_${new Date().toISOString().split('T')[0]}.${format}`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
          this.success = `Résultats exportés en ${format.toUpperCase()}`;
        },
        error: (err) => {
          console.error('Erreur lors de l\'export:', err);
          this.error = 'Erreur lors de l\'export des résultats';
        }
      });
  }

  /**
   * Réinitialiser le formulaire de prédiction
   */
  resetPredictionForm(): void {
    this.predictionForm.reset({
      community_type: 'sportive'
    });
    this.currentPrediction = null;
    this.error = null;
    this.success = null;
  }

  /**
   * Réinitialiser le formulaire de lot
   */
  resetBatchForm(): void {
    this.batchForm.setControl('communities', this.fb.array([]));
    this.batchPredictions = [];
    this.error = null;
    this.success = null;
  }

  /**
   * Obtenir le style pour un cluster
   */
  getClusterStyle(clusterType: string): any {
    const config = this.clusterMessages[clusterType as keyof typeof this.clusterMessages];
    if (!config) return {};

    return {
      'badge': true,
      [`bg-${config.color}`]: true,
      'text-white': true
    };
  }

  /**
   * Obtenir l'icône pour un cluster
   */
  getClusterIcon(clusterType: string): string {
    const config = this.clusterMessages[clusterType as keyof typeof this.clusterMessages];
    return config?.icon || 'fas fa-question';
  }

  /**
   * Obtenir le title pour un cluster
   */
  getClusterTitle(clusterType: string): string {
    const config = this.clusterMessages[clusterType as keyof typeof this.clusterMessages];
    return config?.titre || clusterType;
  }

  /**
   * Obtenir la description pour un cluster
   */
  getClusterDescription(clusterType: string): string {
    const config = this.clusterMessages[clusterType as keyof typeof this.clusterMessages];
    return config?.description || '';
  }

  /**
   * Marquer all champs d'un formulaire comme touchés
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
    });
  }

  /**
   * Getters pour le formulaire de lot
   */
  get communities(): FormArray {
    return this.batchForm.get('communities') as FormArray;
  }

  /**
   * Obtenir le message d'erreur pour un champ
   */
  getErrorMessage(fieldName: string, form: FormGroup): string {
    const field = form.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return 'Ce champ est requis';
      }
      if (field.errors['min']) {
        return `La valeur minimale est ${field.errors['min'].min}`;
      }
      if (field.errors['minlength']) {
        return `La longueur minimale est ${field.errors['minlength'].requiredLength}`;
      }
    }
    return '';
  }
}



