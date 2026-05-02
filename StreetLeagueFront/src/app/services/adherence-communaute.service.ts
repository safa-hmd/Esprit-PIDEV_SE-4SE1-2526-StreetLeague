import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdherenceCommunauteService {

  /**
   * Add un utilisateur à une community (localStorage)
   */
  join(userId: number, communauteId: number): Observable<any> {
    const adherences = this.getAdherencesFromStorage();
    
    // Vérifier si l'adhésion existe déjà
    if (!adherences.find(a => a.userId === userId && a.communauteId === communauteId)) {
      adherences.push({ userId, communauteId, date: new Date().toISOString() });
      localStorage.setItem('MesCommunautes', JSON.stringify(adherences));
    }

    return of({ success: true });
  }

  /**
   * Vérifier si l'utilisateur fait déjà partie de la community
   */
  estMembre(userId: number, communauteId: number): Observable<boolean> {
    const adherences = this.getAdherencesFromStorage();
    const isMember = adherences.some(a => a.userId === userId && a.communauteId === communauteId);
    return of(isMember);
  }

  /**
   * Leave une community (localStorage)
   */
  leave(userId: number, communauteId: number): Observable<void> {
    const adherences = this.getAdherencesFromStorage();
    const filtered = adherences.filter(a => !(a.userId === userId && a.communauteId === communauteId));
    localStorage.setItem('MesCommunautes', JSON.stringify(filtered));
    return of(void 0);
  }

  /**
   * Récupérer les communities rejointes par l'utilisateur
   */
  getMesCommunautes(userId: number): Observable<number[]> {
    const adherences = this.getAdherencesFromStorage();
    const communauteIds = adherences
      .filter(a => a.userId === userId)
      .map(a => a.communauteId);
    return of(communauteIds);
  }

  /**
   * Récupérer les members d'une community
   */
  getMembers(communauteId: number): Observable<number[]> {
    const adherences = this.getAdherencesFromStorage();
    const userIds = adherences
      .filter(a => a.communauteId === communauteId)
      .map(a => a.userId);
    return of([...new Set(userIds)]); // Devuplicate
  }

  /**
   * Récupérer ou initialiser les adhésions depuis localStorage
   */
  private getAdherencesFromStorage(): any[] {
    const adherencesStr = localStorage.getItem('MesCommunautes');
    return adherencesStr ? JSON.parse(adherencesStr) : [];
  }
}
