import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { LivraisonService } from 'src/app/services/livraison.service';
import { AuthService } from 'src/app/services/auth.service';
import { Livraison, LivraisonStatus } from 'src/app/models/livraison.model';
import * as polyline from '@mapbox/polyline';
declare const L: any;

interface StopDTO {
  ordre: number;
  livraisonId: number;
  commandeId: number;
  adresse: string;
  latitude: number;
  longitude: number;
  statut: string;
  distanceDepuisPrecedentKm: number;
  etaMinutesDepuisDepart: number;
  fraisLivraison: number;
}

interface TourneeDTO {
  livreurId: number;
  livreurNom: string;
  latDepart: number | null;
  lonDepart: number | null;
  stops: StopDTO[];
  distanceTotaleKm: number;
  etaTotalMinutes: number;
  polylines: string[];
}

@Component({
  selector: 'app-mes-livraisons',
  templateUrl: './mes-livraisons.component.html',
  styleUrls: ['./mes-livraisons.component.css']
})
export class MesLivraisonsComponent implements OnInit, AfterViewChecked {
  livraisons: Livraison[] = [];
  filteredLivraisons: Livraison[] = [];
  selectedLivraison?: Livraison;
  loading = false;
  successMsg = '';
  errorMsg = '';
  userId!: number; // ✅ propriété ajoutée
  statuts: LivraisonStatus[] = ['PREPAREE', 'ASSIGNEE', 'EXPEDIEE', 'OUT_FOR_DELIVERY', 'LIVREE', 'ECHEC'];
  activeFilter: LivraisonStatus | 'TOUTES' = 'TOUTES';
  gpsActive = true;
  stats = { total: 0, preparees: 0, assignees: 0, enCours: 0, livrees: 0, echecs: 0 };
  tournee?: TourneeDTO;
  showTourneeMap = false;
  tourneeLoading = false;
  private tourneeMap: any = null;
  private leafletLoaded = false;

  constructor(
    private livraisonService: LivraisonService,
    private authService: AuthService
  ) {}

  // ✅ Un seul ngOnInit, correctement fermé
  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (id) {
      this.userId = Number(id);
    }
    this.loadLivraisons();
    this.loadLeaflet();
  }

  // ✅ Un seul ngAfterViewChecked
  ngAfterViewChecked(): void {
    if (this.showTourneeMap && this.tournee && this.leafletLoaded && !this.tourneeMap) {
      this.initMap();
    }
  }

  private loadLeaflet(): void {
    if ((window as any).L) {
      this.leafletLoaded = true;
      return;
    }
    const script = document.createElement('script');
    script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
    script.onload = () => {
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
      document.head.appendChild(link);
      this.leafletLoaded = true;
    };
    document.head.appendChild(script);
  }

  loadLivraisons(): void {
    this.loading = true;
    const livreurId = this.authService.getUserId();
    if (!livreurId) {
      this.errorMsg = 'Utilisateur non identifié';
      this.loading = false;
      return;
    }
    // ✅ Number() pour convertir string → number
    this.livraisonService.getLivraisonsByLivreur(Number(livreurId)).subscribe({
      next: (data: Livraison[]) => {
        this.livraisons = data;
        this.applyFilter();
        this.computeStats();
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Erreur chargement livraisons';
        this.loading = false;
      }
    });
  }

  computeStats(): void {
    this.stats.total = this.livraisons.length;
    this.stats.preparees = this.livraisons.filter(l => l.statut === 'PREPAREE').length;
    this.stats.assignees = this.livraisons.filter(l => l.statut === 'ASSIGNEE').length;
    this.stats.enCours = this.livraisons.filter(l => l.statut === 'EXPEDIEE' || l.statut === 'OUT_FOR_DELIVERY').length;
    this.stats.livrees = this.livraisons.filter(l => l.statut === 'LIVREE').length;
    this.stats.echecs = this.livraisons.filter(l => l.statut === 'ECHEC').length;
  }

  setFilter(filter: LivraisonStatus | 'TOUTES'): void {
    this.activeFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'TOUTES') {
      this.filteredLivraisons = [...this.livraisons];
    } else {
      this.filteredLivraisons = this.livraisons.filter(l => l.statut === this.activeFilter);
    }
  }

  getCountForStatut(statut: LivraisonStatus): number {
    return this.livraisons.filter(l => l.statut === statut).length;
  }

  getBadgeClass(statut: string): string {
    const map: Record<string, string> = {
      PREPAREE: 'badge-warning',
      ASSIGNEE: 'badge-info',
      EXPEDIEE: 'badge-primary',
      OUT_FOR_DELIVERY: 'badge-purple',
      LIVREE: 'badge-success',
      ECHEC: 'badge-danger'
    };
    return map[statut] || '';
  }

  selectLivraison(l: Livraison): void {
    this.selectedLivraison = this.selectedLivraison?.id === l.id ? undefined : l;
  }

  updateStatus(l: Livraison, newStatut: LivraisonStatus): void {
    if (!l.id) {
      this.errorMsg = 'ID de livraison invalide';
      return;
    }
    this.livraisonService.updateStatus(l.id, { statut: newStatut }).subscribe({
      next: () => {
        l.statut = newStatut;
        this.computeStats();
        this.successMsg = `Statut mis à jour : ${newStatut}`;
        setTimeout(() => this.successMsg = '', 2000);
        if (this.showTourneeMap) this.chargerTournee();
      },
      error: () => this.errorMsg = 'Erreur mise à jour statut'
    });
  }

  formatEta(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = Math.round(minutes % 60);
    return h > 0 ? `${h}h ${m}min` : `${m} min`;
  }

  chargerTournee(): void {
    this.tourneeLoading = true;
    const livreurId = this.authService.getUserId();
    if (!livreurId) {
      this.errorMsg = 'Utilisateur non identifié';
      this.tourneeLoading = false;
      return;
    }
    // ✅ Number() pour convertir string → number
    this.livraisonService.getTournee(Number(livreurId)).subscribe({
      next: (t: TourneeDTO) => {
        this.tournee = t;
        this.showTourneeMap = true;
        this.tourneeLoading = false;
      },
      error: () => {
        this.errorMsg = 'Impossible de calculer la tournée';
        this.tourneeLoading = false;
      }
    });
  }

  fermerTournee(): void {
    this.showTourneeMap = false;
    this.tournee = undefined;
    if (this.tourneeMap) {
      this.tourneeMap.remove();
      this.tourneeMap = null;
    }
  }

  private initMap(): void {
    if (!this.tournee || this.tourneeMap) return;
    const centerLat = this.tournee.latDepart ?? (this.tournee.stops[0]?.latitude ?? 36.8189);
    const centerLng = this.tournee.lonDepart ?? (this.tournee.stops[0]?.longitude ?? 10.1658);

    this.tourneeMap = L.map('tournee-map').setView([centerLat, centerLng], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.tourneeMap);

    if (this.tournee.latDepart !== null && this.tournee.latDepart !== undefined &&
        this.tournee.lonDepart !== null && this.tournee.lonDepart !== undefined) {
      L.marker([this.tournee.latDepart, this.tournee.lonDepart])
        .bindPopup('🚀 Départ')
        .addTo(this.tourneeMap);
    }

    this.tournee.stops.forEach(stop => {
      L.marker([stop.latitude, stop.longitude])
        .bindPopup(`<b>Stop #${stop.ordre}</b><br>${stop.adresse}<br>${stop.statut}`)
        .addTo(this.tourneeMap);
    });

    let anyPolyline = false;
    if (this.tournee.polylines && this.tournee.polylines.length) {
      for (const enc of this.tournee.polylines) {
        if (enc && enc.length) {
          try {
            const decoded = polyline.decode(enc, 5);
            const latlngs = decoded.map((p: number[]) => [p[0], p[1]] as [number, number]);
            L.polyline(latlngs, { color: '#e61920', weight: 4, opacity: 0.9 }).addTo(this.tourneeMap);
            anyPolyline = true;
          } catch (e) {
            console.error('Erreur décodage polyline', e);
          }
        }
      }
    }

    if (!anyPolyline) {
      const points: [number, number][] = [];
      if (this.tournee.latDepart !== null && this.tournee.latDepart !== undefined &&
          this.tournee.lonDepart !== null && this.tournee.lonDepart !== undefined) {
        points.push([this.tournee.latDepart, this.tournee.lonDepart]);
      }
      this.tournee.stops.forEach(s => points.push([s.latitude, s.longitude]));
      if (points.length > 1) {
        L.polyline(points, { color: 'cyan', weight: 3, dashArray: '5,5' }).addTo(this.tourneeMap);
      }
    }

    const allPoints: [number, number][] = [];
    if (this.tournee.latDepart !== null && this.tournee.latDepart !== undefined &&
        this.tournee.lonDepart !== null && this.tournee.lonDepart !== undefined) {
      allPoints.push([this.tournee.latDepart, this.tournee.lonDepart]);
    }
    this.tournee.stops.forEach(s => allPoints.push([s.latitude, s.longitude]));
    if (allPoints.length) this.tourneeMap.fitBounds(allPoints, { padding: [40, 40] });

    setTimeout(() => this.tourneeMap.invalidateSize(), 200);
  }
}