import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LivraisonFormComponent } from './livraison-form.component';
import { LivraisonService } from '../../../services/livraison.service';
import { Transporteur } from '../../../models/livraison.model';

describe('LivraisonFormComponent', () => {
  let component: LivraisonFormComponent;
  let fixture: ComponentFixture<LivraisonFormComponent>;
  let livraisonServiceSpy: jasmine.SpyObj<LivraisonService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // ── Données fictives ────────────────────────────────────
  const fakeTransporteurs: Transporteur[] = [
    { id: 1, nomSociete: 'TransTunis', telephone: '20000000', email: 'trans@tunis.com' },
    { id: 2, nomSociete: 'RapidoExpress', telephone: '22000000', email: 'rapido@ex.com' }
  ];

  const fakeLivreurs = [
    { id: 42, fullName: 'Ahmed Ben Ali', email: 'ahmed@test.com', role: 'DELIVERY' },
    { id: 43, fullName: 'Sara Trabelsi', email: 'sara@test.com',  role: 'DELIVERY' }
  ];

  const fakeLivraison = {
    id: 1,
    commandeId: 10,
    transporteurId: 1,
    livreurId: 42,
    adresse: '12 Rue de la Paix, Tunis',
    fraisLivraison: 7.5,
    statut: 'PREPAREE' as const
  };

  beforeEach(() => {
    livraisonServiceSpy = jasmine.createSpyObj('LivraisonService',
      ['getAllTransporteurs', 'getAllDeliveryUsers', 'createLivraison']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    livraisonServiceSpy.getAllTransporteurs.and.returnValue(of(fakeTransporteurs));
    livraisonServiceSpy.getAllDeliveryUsers.and.returnValue(of(fakeLivreurs));
    livraisonServiceSpy.createLivraison.and.returnValue(of(fakeLivraison));

    TestBed.configureTestingModule({
      declarations: [LivraisonFormComponent],
      imports: [FormsModule],
      providers: [
        { provide: LivraisonService, useValue: livraisonServiceSpy },
        { provide: Router,           useValue: routerSpy }
      ]
    });

    fixture = TestBed.createComponent(LivraisonFormComponent);
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
  // 2. ngOnInit — chargement des données
  // ════════════════════════════════════════════════════════
  describe('ngOnInit()', () => {

    it('should call getAllTransporteurs on init', () => {
      expect(livraisonServiceSpy.getAllTransporteurs).toHaveBeenCalled();
    });

    it('should populate transporteurs list', () => {
      expect(component.transporteurs.length).toBe(2);
      expect(component.transporteurs[0].nomSociete).toBe('TransTunis');
    });

    it('should call getAllDeliveryUsers on init', () => {
      expect(livraisonServiceSpy.getAllDeliveryUsers).toHaveBeenCalled();
    });

    it('should populate livreurs list', () => {
      expect(component.livreurs.length).toBe(2);
      expect(component.livreurs[0].fullName).toBe('Ahmed Ben Ali');
    });

    it('should show error if getAllTransporteurs fails', () => {
      livraisonServiceSpy.getAllTransporteurs.and.returnValue(
        throwError(() => new Error('Erreur'))
      );
      component.ngOnInit();
      expect(component.errorMsg).toContain('transporteurs');
    });

    it('should show error if getAllDeliveryUsers fails', () => {
      livraisonServiceSpy.getAllDeliveryUsers.and.returnValue(
        throwError(() => new Error('Erreur'))
      );
      component.ngOnInit();
      expect(component.errorMsg).toContain('livreurs');
    });
  });

  // ════════════════════════════════════════════════════════
  // 3. Formulaire initial
  // ════════════════════════════════════════════════════════
  describe('form initial state', () => {

    it('should have commandeId initialized to 0', () => {
      expect(component.form.commandeId).toBe(0);
    });

    it('should have transporteurId initialized to 0', () => {
      expect(component.form.transporteurId).toBe(0);
    });

    it('should have statut initialized to PREPAREE', () => {
      expect(component.form.statut).toBe('PREPAREE');
    });

    it('should have adresse initialized to empty string', () => {
      expect(component.form.adresse).toBe('');
    });

    it('should have fraisLivraison initialized to 0', () => {
      expect(component.form.fraisLivraison).toBe(0);
    });
  });

  // ════════════════════════════════════════════════════════
  // 4. Validation submit()
  // ════════════════════════════════════════════════════════
  describe('submit() — validation', () => {

    it('should show error if commandeId is 0', () => {
      component.form.commandeId = 0;
      component.form.transporteurId = 1;
      component.form.adresse = '12 Rue Tunis';
      component.submit();
      expect(component.errorMsg).toContain('obligatoires');
      expect(livraisonServiceSpy.createLivraison).not.toHaveBeenCalled();
    });

    it('should show error if transporteurId is 0', () => {
      component.form.commandeId = 10;
      component.form.transporteurId = 0;
      component.form.adresse = '12 Rue Tunis';
      component.submit();
      expect(component.errorMsg).toContain('obligatoires');
      expect(livraisonServiceSpy.createLivraison).not.toHaveBeenCalled();
    });

    it('should show error if adresse is empty', () => {
      component.form.commandeId = 10;
      component.form.transporteurId = 1;
      component.form.adresse = '';
      component.submit();
      expect(component.errorMsg).toContain('obligatoires');
      expect(livraisonServiceSpy.createLivraison).not.toHaveBeenCalled();
    });
  });

  // ════════════════════════════════════════════════════════
  // 5. submit() — succès
  // ════════════════════════════════════════════════════════
  describe('submit() — succès', () => {

    beforeEach(() => {
      // Remplir le formulaire correctement
      component.form.commandeId = 10;
      component.form.transporteurId = 1;
      component.form.livreurId = 42;
      component.form.adresse = '12 Rue de la Paix, Tunis';
      component.form.fraisLivraison = 7.5;
      component.form.statut = 'PREPAREE';
    });

    it('should call createLivraison with form data', () => {
      component.submit();
      expect(livraisonServiceSpy.createLivraison).toHaveBeenCalledWith(
        jasmine.objectContaining({
          commandeId: 10,
          transporteurId: 1,
          livreurId: 42,
          adresse: '12 Rue de la Paix, Tunis'
        })
      );
    });

    it('should show success message after creation', () => {
      component.submit();
      expect(component.successMsg).toContain('succès');
    });

    it('should navigate to /admin/livraisons after 1500ms', fakeAsync(() => {
      component.submit();
      tick(1500);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/admin/livraisons']);
    }));

    it('should set loading to false after success', () => {
      component.submit();
      expect(component.loading).toBeFalse();
    });
  });

  // ════════════════════════════════════════════════════════
  // 6. submit() — erreur API
  // ════════════════════════════════════════════════════════
  describe('submit() — erreur', () => {

    beforeEach(() => {
      component.form.commandeId = 10;
      component.form.transporteurId = 1;
      component.form.adresse = '12 Rue Tunis';
      livraisonServiceSpy.createLivraison.and.returnValue(
        throwError(() => new Error('Erreur serveur'))
      );
    });

    it('should show error message when API fails', () => {
      component.submit();
      expect(component.errorMsg).toContain('Erreur');
    });

    it('should set loading to false after error', () => {
      component.submit();
      expect(component.loading).toBeFalse();
    });

    it('should not navigate on error', () => {
      component.submit();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });
  });

  // ════════════════════════════════════════════════════════
  // 7. cancel()
  // ════════════════════════════════════════════════════════
  describe('cancel()', () => {

    it('should navigate to /admin/livraisons', () => {
      component.cancel();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/admin/livraisons']);
    });
  });

  // ════════════════════════════════════════════════════════
  // 8. Liste des statuts
  // ════════════════════════════════════════════════════════
  describe('statuts list', () => {

    it('should contain all 5 statuts', () => {
      expect(component.statuts.length).toBe(5);
      expect(component.statuts).toContain('PREPAREE');
      expect(component.statuts).toContain('EXPEDIEE');
      expect(component.statuts).toContain('EN_COURS');
      expect(component.statuts).toContain('LIVREE');
      expect(component.statuts).toContain('ECHEC');
    });
  });

});