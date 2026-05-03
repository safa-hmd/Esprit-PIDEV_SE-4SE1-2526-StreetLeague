import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { MesLivraisonsComponent } from './mes-livraisons.component';
import { LivraisonService } from '../../services/livraison.service';
import { AuthService } from '../../services/auth.service';
import { FilterByStatutPipe } from '../filter-by-statut.pipe';
import { Livraison } from '../../models/livraison.model';

describe('MesLivraisonsComponent', () => {
  let component: MesLivraisonsComponent;
  let fixture: ComponentFixture<MesLivraisonsComponent>;
  let livraisonServiceSpy: jasmine.SpyObj<LivraisonService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const CURRENT_USER_ID = '42';

  const mockApiResponse: Livraison[] = [
    {
      id: 1,
      commandeId: 10,
      transporteurId: 2,
      livreurId: CURRENT_USER_ID,
      livreur: { id: CURRENT_USER_ID, fullName: 'Ahmed', email: 'ahmed@test.com', role: 'DELIVERY' },
      adresse: '12 Rue Tunis',
      fraisLivraison: 7.5,
      statut: 'PREPAREE'
    },
    {
      id: 2,
      commandeId: 11,
      transporteurId: 3,
      livreurId: CURRENT_USER_ID,
      livreur: { id: CURRENT_USER_ID, fullName: 'Ahmed', email: 'ahmed@test.com', role: 'DELIVERY' },
      adresse: '5 Avenue Sfax',
      fraisLivraison: 10,
      statut: 'LIVREE'
    },
    {
      id: 3,
      commandeId: 12,
      transporteurId: 1,
      livreurId: 99,
      livreur: { id: 99, fullName: 'Autre', email: 'autre@test.com', role: 'DELIVERY' },
      adresse: '3 Rue Sousse',
      fraisLivraison: 5,
      statut: 'EN_COURS'
    }
  ];

  beforeEach(() => {
    livraisonServiceSpy = jasmine.createSpyObj('LivraisonService',
      ['getAllLivraisons', 'updateStatus']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getUserId']);

    livraisonServiceSpy.getAllLivraisons.and.returnValue(of(mockApiResponse));
    livraisonServiceSpy.updateStatus.and.returnValue(of({} as Livraison));
    authServiceSpy.getUserId.and.returnValue(CURRENT_USER_ID);

    TestBed.configureTestingModule({
      declarations: [MesLivraisonsComponent, FilterByStatutPipe],
      providers: [
        { provide: LivraisonService, useValue: livraisonServiceSpy },
        { provide: AuthService,      useValue: authServiceSpy }
      ]
    });

    fixture = TestBed.createComponent(MesLivraisonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ════════════════════════════════════════════════════════
  // 0. TEST DE DIAGNOSTIC — à lire en premier si échec
  // ════════════════════════════════════════════════════════
  it('[DIAG] should have userId=42 and 2 livraisons after init', () => {
    console.log('userId:', component.userId);
    console.log('livraisons.length:', component.livraisons.length);
    console.log('livraisons:', component.livraisons.map(l => `id=${l.id} statut=${l.statut}`));
    expect(component.userId).toBe(CURRENT_USER_ID);
    expect(component.livraisons.length).toBe(2);
  });

  // ════════════════════════════════════════════════════════
  // 1. Création
  // ════════════════════════════════════════════════════════
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ════════════════════════════════════════════════════════
  // 2. ngOnInit
  // ════════════════════════════════════════════════════════
  describe('ngOnInit()', () => {

    it('should set userId from AuthService', () => {
      expect(component.userId).toBe(CURRENT_USER_ID);
    });

    it('should call getAllLivraisons on init', () => {
      expect(livraisonServiceSpy.getAllLivraisons).toHaveBeenCalled();
    });

    it('should keep only livraisons assigned to current user', () => {
      expect(component.livraisons.length).toBe(2);
    });

    it('should exclude id=3 (livreur.id=99)', () => {
      expect(component.livraisons.map(l => l.id)).not.toContain(3);
    });

    it('should set loading to false after load', () => {
      expect(component.loading).toBeFalse();
    });
  });

  // ════════════════════════════════════════════════════════
  // 3. Filtre — fonctionne sur component.livraisons qui est set manuellement si besoin
  // ════════════════════════════════════════════════════════
  describe('setFilter()', () => {

    // Setup robuste : forcer les données directement si ngOnInit n'a pas filtré
    beforeEach(() => {
      // Forcer livraisons manuellement pour que les tests de filtre soient indépendants
      component.livraisons = [
        { ...mockApiResponse[0] }, // PREPAREE
        { ...mockApiResponse[1] }  // LIVREE
      ];
      component.filteredLivraisons = [...component.livraisons];
    });

    it('should show all with filter TOUTES', () => {
      component.setFilter('TOUTES');
      expect(component.filteredLivraisons.length).toBe(2);
    });

    it('should filter by PREPAREE', () => {
      component.setFilter('PREPAREE');
      expect(component.filteredLivraisons.length).toBe(1);
      expect(component.filteredLivraisons[0].statut).toBe('PREPAREE');
    });

    it('should filter by LIVREE', () => {
      component.setFilter('LIVREE');
      expect(component.filteredLivraisons.length).toBe(1);
      expect(component.filteredLivraisons[0].statut).toBe('LIVREE');
    });

    it('should return empty for ECHEC (no match)', () => {
      component.setFilter('ECHEC');
      expect(component.filteredLivraisons.length).toBe(0);
    });

    it('should update activeFilter', () => {
      component.setFilter('EXPEDIEE');
      expect(component.activeFilter).toBe('EXPEDIEE');
    });
  });

  // ════════════════════════════════════════════════════════
  // 4. Stats — forcées directement pour robustesse
  // ════════════════════════════════════════════════════════
  describe('stats getter', () => {

    beforeEach(() => {
      component.livraisons = [
        { ...mockApiResponse[0] }, // PREPAREE
        { ...mockApiResponse[1] }  // LIVREE
      ];
    });

    it('should return total = 2', () => {
      expect(component.stats.total).toBe(2);
    });

    it('should return livrees = 1', () => {
      expect(component.stats.livrees).toBe(1);
    });

    it('should return preparees = 1', () => {
      expect(component.stats.preparees).toBe(1);
    });

    it('should return echecs = 0', () => {
      expect(component.stats.echecs).toBe(0);
    });

    it('should return enCours = 0', () => {
      expect(component.stats.enCours).toBe(0);
    });
  });

  // ════════════════════════════════════════════════════════
  // 5. updateStatus()
  // ════════════════════════════════════════════════════════
  describe('updateStatus()', () => {

    // Livraison locale pour les tests — indépendante de ngOnInit
    let livraisonPreparee: Livraison;
    let livraisonLivree: Livraison;

    beforeEach(() => {
      livraisonPreparee = { ...mockApiResponse[0] }; // id=1, PREPAREE
      livraisonLivree   = { ...mockApiResponse[1] }; // id=2, LIVREE
      component.livraisons = [livraisonPreparee, livraisonLivree];
      component.filteredLivraisons = [...component.livraisons];
      livraisonServiceSpy.updateStatus.calls.reset();
    });

    it('should NOT call service when same statut passed', () => {
      component.updateStatus(livraisonPreparee, 'PREPAREE'); // même statut
      expect(livraisonServiceSpy.updateStatus).not.toHaveBeenCalled();
    });

    it('should call updateStatus with correct id and statut', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...livraisonPreparee, statut: 'EXPEDIEE' })
      );
      component.updateStatus(livraisonPreparee, 'EXPEDIEE');
      expect(livraisonServiceSpy.updateStatus).toHaveBeenCalledWith(
        1,
        jasmine.objectContaining({ statut: 'EXPEDIEE' })
      );
    });

    it('should update livraison statut locally after success', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...livraisonPreparee, statut: 'EXPEDIEE' })
      );
      component.updateStatus(livraisonPreparee, 'EXPEDIEE');
      expect(livraisonPreparee.statut).toBe('EXPEDIEE');
    });

    it('should show success message after update', fakeAsync(() => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...livraisonPreparee, statut: 'EXPEDIEE' })
      );
      component.updateStatus(livraisonPreparee, 'EXPEDIEE');
      expect(component.successMsg).toContain('EXPEDIEE');
      tick(3500);
      expect(component.successMsg).toBe('');
    }));

    it('should show error message on service failure', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        throwError(() => new Error('Erreur'))
      );
      component.updateStatus(livraisonPreparee, 'LIVREE');
      expect(component.errorMsg).toContain('Erreur');
    });

    it('should update selectedLivraison statut if it matches', () => {
      livraisonServiceSpy.updateStatus.and.returnValue(
        of({ ...livraisonPreparee, statut: 'EXPEDIEE' })
      );
      component.selectedLivraison = livraisonPreparee;
      component.updateStatus(livraisonPreparee, 'EXPEDIEE');
      expect(component.selectedLivraison!.statut).toBe('EXPEDIEE');
    });
  });

  // ════════════════════════════════════════════════════════
  // 6. selectLivraison()
  // ════════════════════════════════════════════════════════
  describe('selectLivraison()', () => {

    let l1: Livraison;
    let l2: Livraison;

    beforeEach(() => {
      l1 = { ...mockApiResponse[0] };
      l2 = { ...mockApiResponse[1] };
      component.livraisons = [l1, l2];
    });

    it('should set selectedLivraison on first click', () => {
      component.selectLivraison(l1);
      expect(component.selectedLivraison).toEqual(l1);
    });

    it('should deselect (null) when same clicked again', () => {
      component.selectLivraison(l1);
      component.selectLivraison(l1);
      expect(component.selectedLivraison).toBeNull();
    });

    it('should switch to other livraison', () => {
      component.selectLivraison(l1);
      component.selectLivraison(l2);
      expect(component.selectedLivraison?.id).toBe(l2.id);
    });
  });

  // ════════════════════════════════════════════════════════
  // 7. Erreur au chargement
  // ════════════════════════════════════════════════════════
  describe('loadLivraisons() — erreur', () => {

    it('should show error message when API fails', () => {
      livraisonServiceSpy.getAllLivraisons.and.returnValue(
        throwError(() => new Error('Network error'))
      );
      component.loadLivraisons();
      expect(component.errorMsg).toContain('Erreur');
    });

    it('should set loading to false on error', () => {
      livraisonServiceSpy.getAllLivraisons.and.returnValue(
        throwError(() => new Error('Network error'))
      );
      component.loadLivraisons();
      expect(component.loading).toBeFalse();
    });
  });

  // ════════════════════════════════════════════════════════
  // 8. getBadgeClass()
  // ════════════════════════════════════════════════════════
  describe('getBadgeClass()', () => {

    it('should return dfo-badge-orange for PREPAREE', () => {
      expect(component.getBadgeClass('PREPAREE')).toBe('dfo-badge-orange');
    });
    it('should return dfo-badge-blue for EXPEDIEE', () => {
      expect(component.getBadgeClass('EXPEDIEE')).toBe('dfo-badge-blue');
    });
    it('should return dfo-badge-blue for EN_COURS', () => {
      expect(component.getBadgeClass('EN_COURS')).toBe('dfo-badge-blue');
    });
    it('should return dfo-badge-green for LIVREE', () => {
      expect(component.getBadgeClass('LIVREE')).toBe('dfo-badge-green');
    });
    it('should return dfo-badge-red for ECHEC', () => {
      expect(component.getBadgeClass('ECHEC')).toBe('dfo-badge-red');
    });
  });

});