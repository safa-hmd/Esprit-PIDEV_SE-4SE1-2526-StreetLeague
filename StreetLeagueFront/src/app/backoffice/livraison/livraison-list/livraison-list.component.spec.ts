import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { LivraisonListComponent } from './livraison-list.component';
import { LivraisonService } from '../../../services/livraison.service';
import { Livraison } from '../../../models/livraison.model';

describe('LivraisonListComponent', () => {
  let component: LivraisonListComponent;
  let fixture: ComponentFixture<LivraisonListComponent>;
  let livraisonServiceSpy: jasmine.SpyObj<LivraisonService>;

  // ── Données fictives ────────────────────────────────────
  const mockLivraisons: Livraison[] = [
    {
      id: 1,
      commandeId: 10,
      transporteurId: 2,
      adresse: '12 Rue Tunis',
      fraisLivraison: 7.5,
      statut: 'PREPAREE'
    },
    {
      id: 2,
      commandeId: 11,
      transporteurId: 3,
      adresse: '5 Avenue Sfax',
      fraisLivraison: 10,
      statut: 'EN_COURS'
    },
    {
      id: 3,
      commandeId: 12,
      transporteurId: 1,
      adresse: '3 Rue Sousse',
      fraisLivraison: 5,
      statut: 'LIVREE'
    },
    {
      id: 4,
      commandeId: 13,
      transporteurId: 2,
      adresse: '8 Boulevard Bizerte',
      fraisLivraison: 8,
      statut: 'ECHEC'
    }
  ];

  beforeEach(() => {
    livraisonServiceSpy = jasmine.createSpyObj('LivraisonService',
      ['getAllLivraisons', 'updateStatus']);

    livraisonServiceSpy.getAllLivraisons.and.returnValue(of(mockLivraisons));

    TestBed.configureTestingModule({
      declarations: [LivraisonListComponent],
      imports: [FormsModule],
      providers: [
        { provide: LivraisonService, useValue: livraisonServiceSpy }
      ]
    });

    fixture = TestBed.createComponent(LivraisonListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ════════════════════════════════════════════════════════
  // 1. Création
  // ════════════════════════════════════════════════════════
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ════════════════════════════════════════════════════════
  // 2. Chargement initial
  // ════════════════════════════════════════════════════════
  describe('loadLivraisons()', () => {

    it('should call getAllLivraisons on init', () => {
      expect(livraisonServiceSpy.getAllLivraisons).toHaveBeenCalled();
    });

    it('should populate livraisons array', () => {
      expect(component.livraisons.length).toBe(4);
    });

    it('should populate filteredLivraisons array', () => {
      expect(component.filteredLivraisons.length).toBe(4);
    });

    it('should set loading to false after load', () => {
      expect(component.loading).toBeFalse();
    });

    it('should show error message when API fails', () => {
      livraisonServiceSpy.getAllLivraisons.and.returnValue(
        throwError(() => new Error('Erreur réseau'))
      );
      component.loadLivraisons();
      expect(component.errorMsg).toContain('Erreur');
    });

    it('should set loading false even on error', () => {
      livraisonServiceSpy.getAllLivraisons.and.returnValue(
        throwError(() => new Error('Erreur'))
      );
      component.loadLivraisons();
      expect(component.loading).toBeFalse();
    });
  });

  // ════════════════════════════════════════════════════════
  // 3. Recherche search()
  // ════════════════════════════════════════════════════════
  describe('search()', () => {

    it('should filter by adresse (case insensitive)', () => {
      component.searchTerm = 'tunis';
      component.search();
      expect(component.filteredLivraisons.length).toBe(1);
      expect(component.filteredLivraisons[0].adresse).toContain('Tunis');
    });

    it('should filter by id', () => {
      component.searchTerm = '2';
      component.search();
      // id=2, commandeId=11 contient pas 2, adresse Sfax pas 2... id=2 match
      expect(component.filteredLivraisons.some(l => l.id === 2)).toBeTrue();
    });

    it('should filter by commandeId', () => {
      component.searchTerm = '12';
      component.search();
      expect(component.filteredLivraisons.some(l => l.commandeId === 12)).toBeTrue();
    });

    it('should return all when searchTerm is empty', () => {
      component.searchTerm = '';
      component.search();
      expect(component.filteredLivraisons.length).toBe(4);
    });

    it('should return empty when no match', () => {
      component.searchTerm = 'zzz-aucun-match';
      component.search();
      expect(component.filteredLivraisons.length).toBe(0);
    });
  });

  // ════════════════════════════════════════════════════════
  // 4. updateStatus()
  // ════════════════════════════════════════════════════════
  describe('updateStatus()', () => {

    it('should call updateStatus service', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...mockLivraisons[0], statut: 'EXPEDIEE' })
      );
      component.updateStatus(mockLivraisons[0], 'EXPEDIEE');
      expect(livraisonServiceSpy.updateStatus).toHaveBeenCalledWith(
        1,
        jasmine.objectContaining({ statut: 'EXPEDIEE' })
      );
    });

    it('should reload livraisons after update', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...mockLivraisons[0], statut: 'EXPEDIEE' })
      );
      component.updateStatus(mockLivraisons[0], 'EXPEDIEE');
      // getAllLivraisons appelé 1 fois au init + 1 fois après update = 2
      expect(livraisonServiceSpy.getAllLivraisons).toHaveBeenCalledTimes(2);
    });

    it('should show success message after update', fakeAsync(() => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...mockLivraisons[0], statut: 'LIVREE' })
      );
      component.updateStatus(mockLivraisons[0], 'LIVREE');
      expect(component.successMsg).toContain('succès');
      tick(3000);
      expect(component.successMsg).toBe('');
    }));

    it('should show error message when update fails', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        throwError(() => new Error('Erreur'))
      );
      component.updateStatus(mockLivraisons[0], 'LIVREE');
      expect(component.errorMsg).toContain('Erreur');
    });
  });

  // ════════════════════════════════════════════════════════
  // 5. Stats getter
  // ════════════════════════════════════════════════════════
  describe('stats getter', () => {

    it('should return total = 4', () => {
      expect(component.stats.total).toBe(4);
    });

    it('should return enCours = 1 (EN_COURS only)', () => {
      expect(component.stats.enCours).toBe(1);
    });

    it('should return livrees = 1', () => {
      expect(component.stats.livrees).toBe(1);
    });

    it('should return echec = 1', () => {
      expect(component.stats.echec).toBe(1);
    });
  });

  // ════════════════════════════════════════════════════════
  // 6. getBadgeClass()
  // ════════════════════════════════════════════════════════
  describe('getBadgeClass()', () => {

    it('should return a-badge-orange for PREPAREE', () => {
      expect(component.getBadgeClass('PREPAREE')).toBe('a-badge-orange');
    });
    it('should return a-badge-blue for EN_COURS', () => {
      expect(component.getBadgeClass('EN_COURS')).toBe('a-badge-blue');
    });
    it('should return a-badge-green for LIVREE', () => {
      expect(component.getBadgeClass('LIVREE')).toBe('a-badge-green');
    });
    it('should return a-badge-red for ECHEC', () => {
      expect(component.getBadgeClass('ECHEC')).toBe('a-badge-red');
    });
  });

});