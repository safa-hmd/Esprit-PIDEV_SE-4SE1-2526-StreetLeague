import { Component, OnInit } from '@angular/core';
import { RecommendationService, FieldRecommendation, SlotDto }
  from '../../services/recommendation.service';

@Component({
  selector: 'app-field-recommender',
  templateUrl: './field-recommender.component.html',
  styleUrls:   ['./field-recommender.component.css']
})
export class FieldRecommenderComponent implements OnInit {

  fields:        FieldRecommendation[] = [];
  selectedField: FieldRecommendation | null = null;
  slots:         SlotDto[] = [];

  isLoading      = false;
  isLoadingSlots = false;
  errorMsg       = '';
  geoError       = '';

  userLat = 0;
  userLng = 0;

  constructor(private recService: RecommendationService) {}

  ngOnInit(): void {
    this.loadRecommendations();
  }

  // ── Load field recommendations ───────────────────────────────────
  loadRecommendations(): void {
    this.isLoading = true;
    this.errorMsg  = '';
    this.geoError  = '';
    this.fields    = [];
    this.selectedField = null;
    this.slots = [];

    this.recService.getFieldRecommendations().subscribe({
      next: data => {
        this.fields    = data || [];
        this.isLoading = false;
        if (this.fields.length === 0) {
          this.geoError = 'Activez la localisation pour des recommandations personnalisées.';
        }
      },
      error: err => {
        this.isLoading = false;
        this.errorMsg = 'Impossible de charger les recommandations. Vérifiez que le serveur IA est actif.';
        console.error('[FieldRecommender] error:', err);
      }
    });
  }

  // ── Select a field and load its best slots ───────────────────────
  selectField(field: FieldRecommendation): void {
    if (this.selectedField?.fieldId === field.fieldId) {
      // Toggle off
      this.selectedField = null;
      this.slots = [];
      return;
    }

    this.selectedField  = field;
    this.slots          = [];
    this.isLoadingSlots = true;

    const doFetch = () => this.fetchSlots(field.fieldId);

    if (this.userLat === 0) {
      navigator.geolocation?.getCurrentPosition(
        pos => {
          this.userLat = pos.coords.latitude;
          this.userLng = pos.coords.longitude;
          doFetch();
        },
        () => doFetch()  // no GPS — still fetch slots
      );
    } else {
      doFetch();
    }
  }

  private fetchSlots(fieldId: number): void {
    this.recService.getSlotRecommendations(fieldId, this.userLat, this.userLng).subscribe({
      next:  slots => { this.slots = slots || []; this.isLoadingSlots = false; },
      error: ()    => { this.isLoadingSlots = false; }
    });
  }

  // ── Helpers ──────────────────────────────────────────────────────
  getScorePercent(score: number): number {
    return Math.round((score || 0) * 100);
  }

  getRecClass(rec: string | undefined): string {
    switch (rec) {
      case 'EXCELLENT':   return 'rec-excellent';
      case 'ACCEPTABLE':  return 'rec-acceptable';
      case 'DECONSEILLE': return 'rec-bad';
      default:            return '';
    }
  }

  getWeatherIcon(weather: string): string {
    if (!weather) return '🌤️';
    const w = weather.toLowerCase();
    if (w.includes('clear'))  return '☀️';
    if (w.includes('cloud'))  return '⛅';
    if (w.includes('rain'))   return '🌧️';
    if (w.includes('storm'))  return '⛈️';
    if (w.includes('wind'))   return '💨';
    return '🌤️';
  }

  getMapsLink(field: FieldRecommendation): string {
    if (field.fieldLocation) {
      return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(field.fieldLocation)}`;
    }
    return '#';
  }
}