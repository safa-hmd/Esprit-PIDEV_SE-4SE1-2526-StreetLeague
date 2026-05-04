import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PromoOffer {
  code: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  minCartAmount: number;
  expirationDate: string;
  used: boolean;
  expired: boolean;
  source: string;
}

@Injectable({ providedIn: 'root' })
export class PromoService {

  private baseUrl = 'http://localhost:8086/StreetLeague/api/promos';

  constructor(private http: HttpClient) {}

  getPersonalOffers(userId: number): Observable<PromoOffer[]> {
    return this.http.get<PromoOffer[]>(
      `${this.baseUrl}/my-offers`,
      { params: { userId } }
    );
  }

  formatOfferDisplay(offer: PromoOffer): string {
    const discount = offer.type === 'PERCENTAGE' 
      ? `-${offer.value}%` 
      : `-${offer.value} TND`;

    const minCart = offer.minCartAmount > 0 
      ? ` • Min. ${offer.minCartAmount} TND` 
      : '';

    return `${discount}${minCart}`;
  }
}