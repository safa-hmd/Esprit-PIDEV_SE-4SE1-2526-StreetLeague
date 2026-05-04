import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { Router } from '@angular/router';
import { ShopService } from '../../../services/shop.service';
import { AuthService } from '../../../services/auth.service';
import { LivraisonService } from '../../../services/livraison.service';
import { PanierResponse } from '../../../models/panier.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http'; // ← AJOUT
import { catchError, of } from 'rxjs'; // ← AJOUT

declare const L: any;

// Interface pour les offres promo
export interface PromoOffer {
  code: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  minCartAmount: number;
  expirationDate: string;
  used: boolean;
  expired: boolean;
}

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit, AfterViewChecked {
  panier: PanierResponse | null = null;
  userId = 0;
  loading = false;
  step: 'cart' | 'delivery' | 'confirm' | 'done' = 'cart';

  adresse = '';
  fraisLivraison = 7;

  // GPS client
  latitudeClient: number | null = null;
  longitudeClient: number | null = null;
  gpsLoading = false;

  // Pin de livraison
  latitudeDelivery: number | null = null;
  longitudeDelivery: number | null = null;
  adresseSelectionnee: string = '';

  commandeId: number | null = null;
  successMsg = '';
  errorMsg = '';

  // 🎁 PROMO STATE (NOUVEAU)
  personalOffer: PromoOffer | null = null;
  offerLoading = false;
  offerDismissed = false;
  promoInput = '';
  appliedPromo: any = null;
  promoLoading = false;
  promoError = '';
  finalTotal = 0;

  // Maps Leaflet
  private gpsMapInstance: any = null;
  private addressMapInstance: any = null;
  private recapMapInstance: any = null;
  private addressPin: any = null;
  private gpsMapInitialized = false;
  private addressMapInitialized = false;
  private recapMapInitialized = false;
  private leafletLoaded = false;

  constructor(
    private shopService: ShopService,
    private livraisonService: LivraisonService,
    private router: Router,
    private authService: AuthService,
    private http: HttpClient // ← INJECTION HttpClient
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (!id) { this.router.navigate(['/']); return; }
    this.userId = id;
    this.loadCart();
    this.loadPersonalOffer();
    this.loadLeaflet().then(() => this.detectGpsPosition());
  }

  private loadLeaflet(): Promise<void> {
    return new Promise((resolve) => {
      if ((window as any).L) { this.leafletLoaded = true; resolve(); return; }
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
      document.head.appendChild(link);
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => { this.leafletLoaded = true; resolve(); };
      script.onerror = () => resolve();
      document.head.appendChild(script);
    });
  }

  detectGpsPosition(): void {
    if (!navigator.geolocation) return;
    this.gpsLoading = true;
    navigator.geolocation.getCurrentPosition(
      (pos) => { this.latitudeClient = pos.coords.latitude; this.longitudeClient = pos.coords.longitude; this.gpsLoading = false; },
      () => { this.gpsLoading = false; }
    );
  }

  ngAfterViewChecked(): void {
    if (!this.leafletLoaded) return;
    if (this.step === 'delivery' && !this.gpsMapInitialized && this.latitudeClient !== null && document.getElementById('gps-map')) {
      this.gpsMapInitialized = true;
      this.gpsMapInstance = this.initReadOnlyMap('gps-map', this.latitudeClient!, this.longitudeClient!, 14, '📍 Your position');
    }
    if (this.step === 'delivery' && !this.addressMapInitialized && document.getElementById('address-map')) {
      this.addressMapInitialized = true;
      this.initAddressMap();
    }
    if (this.step === 'confirm' && !this.recapMapInitialized && this.latitudeDelivery !== null && document.getElementById('recap-map')) {
      this.recapMapInitialized = true;
      this.recapMapInstance = this.initReadOnlyMap('recap-map', this.latitudeDelivery!, this.longitudeDelivery!, 14, '📦 Delivery address');
    }
  }

  private initReadOnlyMap(containerId: string, lat: number, lng: number, zoom: number, popupText: string): any {
    try {
      const map = L.map(containerId, { zoomControl: true, scrollWheelZoom: false }).setView([lat, lng], zoom);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '© OpenStreetMap', maxZoom: 19 }).addTo(map);
      const icon = L.divIcon({
        html: `<div style="width:28px;height:28px;background:#e61920;border:3px solid white;border-radius:50% 50% 50% 0;transform:rotate(-45deg);box-shadow:0 2px 6px rgba(230,25,32,0.4);"></div>`,
        iconSize: [28, 28], iconAnchor: [14, 28], className: ''
      });
      L.marker([lat, lng], { icon }).addTo(map).bindPopup(`<strong>${popupText}</strong>`).openPopup();
      setTimeout(() => map.invalidateSize(), 100);
      return map;
    } catch (e) { console.warn('Map init error:', e); return null; }
  }

  private initAddressMap(): void {
    try {
      const defaultLat = this.latitudeClient ?? 36.8189;
      const defaultLng = this.longitudeClient ?? 10.1658;
      this.addressMapInstance = L.map('address-map', { zoomControl: true, scrollWheelZoom: true }).setView([defaultLat, defaultLng], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '© OpenStreetMap', maxZoom: 19 }).addTo(this.addressMapInstance);
      const deliveryIcon = L.divIcon({
        html: `<div style="width:32px;height:32px;background:#e61920;border:3px solid white;border-radius:50% 50% 50% 0;transform:rotate(-45deg);box-shadow:0 3px 8px rgba(230,25,32,0.5);"></div>`,
        iconSize: [32, 32], iconAnchor: [16, 32], className: ''
      });
      this.addressMapInstance.on('click', (e: any) => {
        const { lat, lng } = e.latlng;
        if (this.addressPin) this.addressPin.setLatLng([lat, lng]);
        else this.addressPin = L.marker([lat, lng], { icon: deliveryIcon }).addTo(this.addressMapInstance).bindPopup('<strong>📦 Delivery here</strong>');
        this.addressPin.openPopup();
        this.latitudeDelivery = lat; this.longitudeDelivery = lng;
        const hint = document.getElementById('address-map-hint');
        if (hint) hint.classList.add('hidden');
        this.reverseGeocode(lat, lng);
      });
      setTimeout(() => this.addressMapInstance.invalidateSize(), 150);
    } catch (e) { console.warn('Address map init error:', e); }
  }

  private reverseGeocode(lat: number, lng: number): void {
    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&accept-language=fr`;
    fetch(url, { headers: { 'User-Agent': 'StreetLeague-App/1.0' } })
      .then(r => r.json())
      .then(data => {
        if (data && data.display_name) {
          const parts = data.display_name.split(',');
          const short = parts.slice(0, 3).join(',').trim();
          this.adresseSelectionnee = short; this.adresse = short;
        }
      })
      .catch(() => { this.adresseSelectionnee = `${lat.toFixed(4)}, ${lng.toFixed(4)}`; this.adresse = this.adresseSelectionnee; });
  }

  onAdresseManuelle(value: string): void {
    this.adresse = value; this.adresseSelectionnee = '';
    if (!this.adresseSelectionnee) { this.latitudeDelivery = null; this.longitudeDelivery = null; }
  }

  goToDelivery(): void {
    if (!this.panier || this.panier.lignes.length === 0) { this.showError('Your cart is empty.'); return; }
    this.gpsMapInitialized = false; this.addressMapInitialized = false; this.step = 'delivery'; this.errorMsg = '';
  }
  goToConfirm(): void {
    if (!this.adresse.trim()) { this.showError('Please enter or select a delivery address.'); return; }
    this.recapMapInitialized = false; this.step = 'confirm'; this.errorMsg = '';
  }
  backToCart(): void { this.gpsMapInitialized = false; this.addressMapInitialized = false; this.step = 'cart'; this.errorMsg = ''; }
  backToDelivery(): void { this.gpsMapInitialized = false; this.addressMapInitialized = false; this.recapMapInitialized = false; this.step = 'delivery'; this.errorMsg = ''; }

  loadCart(): void {
    this.loading = true;
    this.shopService.getCart(this.userId).subscribe({
      next: data => { this.panier = data; this.finalTotal = data.total; this.loading = false; },
      error: () => { this.panier = null; this.loading = false; }
    });
  }

  removeItem(ligneId: number): void {
    this.shopService.removeItem(ligneId).subscribe({ next: () => this.loadCart(), error: () => this.showError('Erreur suppression.') });
  }

  clearCart(): void {
    if (!confirm('Clear entire cart?')) return;
    this.shopService.clearCart(this.userId).subscribe({ next: () => this.loadCart(), error: () => this.showError('Erreur suppression.') });
  }

  // 🎁 PROMO LOGIC
  loadPersonalOffer(): void {
    this.offerLoading = true;
    this.http.get<PromoOffer[]>('http://localhost:8086/StreetLeague/api/promos/my-offers', { 
      params: { userId: this.userId }
    }).pipe(
      catchError((err: HttpErrorResponse) => {
        console.warn('Promo offer load error:', err);
        this.offerLoading = false; this.personalOffer = null; return of([]);
      })
    ).subscribe(offers => {
      this.personalOffer = offers.length > 0 ? offers[0] : null;
      this.offerLoading = false;
    });
  }

  applyPersonalOffer(): void {
    if (!this.personalOffer) return;
    this.promoInput = this.personalOffer.code;
    this.applyPromoCode();
    this.offerDismissed = true; this.personalOffer = null;
  }

  dismissOffer(): void { this.offerDismissed = true; }

  applyPromoCode(): void {
    if (!this.promoInput.trim() || !this.panier) return;
    this.promoLoading = true; this.promoError = '';
    const categoryIds = this.panier.lignes.map((l: any) => l.categorieId).filter((id: any) => id != null);
    
    this.http.post<any>('http://localhost:8086/StreetLeague/api/promos/validate', { 
      code: this.promoInput.trim(), userId: this.userId, cartTotal: this.panier.total, categoryIds 
    }).pipe(
      catchError((err: HttpErrorResponse) => { 
        this.promoLoading = false; this.promoError = err.error?.message || 'Code invalide'; 
        this.appliedPromo = null; this.finalTotal = this.panier?.total || 0; return of(null); 
      })
    ).subscribe(res => {
      this.promoLoading = false;
      if (res) { this.appliedPromo = res; this.finalTotal = res.newTotal; this.showSuccess(`✅ Code ${res.code} appliqué !`); }
    });
  }

  removePromo(): void {
    this.appliedPromo = null; this.promoInput = ''; this.finalTotal = this.panier?.total || 0; this.promoError = '';
  }

  confirmOrder(): void {
    this.loading = true;
    this.shopService.checkout(this.userId).subscribe({
      next: (msg: string) => {
        const match = msg.match(/ID\s*=\s*(\d+)/);
        this.commandeId = match ? Number(match[1]) : null;
        this.createLivraison(this.commandeId);
      },
      error: () => { this.loading = false; this.showError('Erreur commande.'); }
    });
  }

  private createLivraison(cmdId: number | null): void {
    const dto = {
      commandeId: cmdId, adresse: this.adresse.trim(), fraisLivraison: this.fraisLivraison,
      latitudeClient: this.latitudeDelivery ?? this.latitudeClient,
      longitudeClient: this.longitudeDelivery ?? this.longitudeClient,
      statut: 'PREPAREE', priorite: 'NORMAL'
    };
    this.livraisonService.createLivraison(dto).subscribe({
      next: () => { this.loading = false; this.step = 'done'; },
      error: () => { this.loading = false; this.step = 'done'; }
    });
  }

  private showSuccess(msg: string): void { this.successMsg = msg; this.errorMsg = ''; setTimeout(() => this.successMsg = '', 3000); }
  private showError(msg: string): void { this.errorMsg = msg; this.successMsg = ''; setTimeout(() => this.errorMsg = '', 4000); }
}