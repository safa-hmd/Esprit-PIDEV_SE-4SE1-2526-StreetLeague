import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdherenceCommunauteService {

  /**
   * Ajouter un utilisateur à une communauté (localStorage)
   */
  rejoindre(userId: number, communauteId: number): Observable<any> {
    const adherences = this.getAdherencesFromStorage();
    
    // Vérifier si l'adhésion existe déjà
    if (!adherences.find(a => a.userId === userId && a.communauteId === communauteId)) {
      adherences.push({ userId, communauteId, date: new Date().toISOString() });
      localStorage.setItem('MesCommunautes', JSON.stringify(adherences));
    }

    return of({ success: true });
  }

  /**
   * Vérifier si l'utilisateur fait déjà partie de la communauté
   */
  estMembre(userId: number, communauteId: number): Observable<boolean> {
    const adherences = this.getAdherencesFromStorage();
    const isMember = adherences.some(a => a.userId === userId && a.communauteId === communauteId);
    return of(isMember);
  }

  /**
   * Quitter une communauté (localStorage)
   */
  quitter(userId: number, communauteId: number): Observable<void> {
    const adherences = this.getAdherencesFromStorage();
    const filtered = adherences.filter(a => !(a.userId === userId && a.communauteId === communauteId));
    localStorage.setItem('MesCommunautes', JSON.stringify(filtered));
    return of(void 0);
  }

  /**
   * Récupérer les communautés rejointes par l'utilisateur
   */
  getMesCommunautes(userId: number): Observable<number[]> {
    const adherences = this.getAdherencesFromStorage();
    const communauteIds = adherences
      .filter(a => a.userId === userId)
      .map(a => a.communauteId);
    return of(communauteIds);
  }

  /**
   * Récupérer les membres d'une communauté
   */
  getMembres(communauteId: number): Observable<number[]> {
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
