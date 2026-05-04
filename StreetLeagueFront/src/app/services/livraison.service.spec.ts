import { environment } from 'src/environments/environment';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LivraisonService } from './livraison.service';
import { Livraison, Transporteur } from '../models/livraison.model';

describe('LivraisonService', () => {
  let service: LivraisonService;
  let httpMock: HttpTestingController;

  const BASE = `${environment.baseUrl}/api`;

  // ── Données fictives ────────────────────────────────────
  const fakeLivraison: Livraison = {
    id: 1,
    commandeId: 10,
    transporteurId: 2,
    livreurId: 42,
    adresse: '12 Rue de la Paix, Tunis',
    fraisLivraison: 7.5,
    statut: 'PREPAREE'
  };

  const fakeLivraison2: Livraison = {
    id: 2,
    commandeId: 11,
    transporteurId: 3,
    livreurId: 5,
    adresse: '5 Avenue Habib Bourguiba, Sfax',
    fraisLivraison: 10,
    statut: 'EN_COURS'
  };

  const fakeTransporteur: Transporteur = {
    id: 1,
    nomSociete: 'TransTunis',
    telephone: '20000000',
    email: 'trans@tunis.com'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LivraisonService]
    });
    service = TestBed.inject(LivraisonService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ════════════════════════════════════════════════════════
  // 1. Création du service
  // ════════════════════════════════════════════════════════
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ════════════════════════════════════════════════════════
  // 2. getAllLivraisons()
  // ════════════════════════════════════════════════════════
  describe('getAllLivraisons()', () => {

    it('should call GET /api/livraisons', () => {
      service.getAllLivraisons().subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should return list of livraisons', () => {
      let result: Livraison[] = [];
      service.getAllLivraisons().subscribe(data => result = data);

      httpMock.expectOne(`${BASE}/livraisons`).flush([fakeLivraison, fakeLivraison2]);
      expect(result.length).toBe(2);
      expect(result[0].id).toBe(1);
      expect(result[1].statut).toBe('EN_COURS');
    });

    it('should return empty array when no livraisons', () => {
      let result: Livraison[] = [];
      service.getAllLivraisons().subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/livraisons`).flush([]);
      expect(result).toEqual([]);
    });
  });

  // ════════════════════════════════════════════════════════
  // 3. getLivraisonById()
  // ════════════════════════════════════════════════════════
  describe('getLivraisonById()', () => {

    it('should call GET /api/livraisons/:id', () => {
      service.getLivraisonById(1).subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons/1`);
      expect(req.request.method).toBe('GET');
      req.flush(fakeLivraison);
    });

    it('should return the correct livraison', () => {
      let result: Livraison | undefined;
      service.getLivraisonById(1).subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/livraisons/1`).flush(fakeLivraison);
      expect(result?.id).toBe(1);
      expect(result?.adresse).toBe('12 Rue de la Paix, Tunis');
    });
  });

  // ════════════════════════════════════════════════════════
  // 4. createLivraison()
  // ════════════════════════════════════════════════════════
  describe('createLivraison()', () => {

    it('should call POST /api/livraisons', () => {
      service.createLivraison(fakeLivraison).subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons`);
      expect(req.request.method).toBe('POST');
      req.flush(fakeLivraison);
    });

    it('should send the correct body', () => {
      service.createLivraison(fakeLivraison).subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons`);
      expect(req.request.body.commandeId).toBe(10);
      expect(req.request.body.transporteurId).toBe(2);
      expect(req.request.body.adresse).toBe('12 Rue de la Paix, Tunis');
      expect(req.request.body.statut).toBe('PREPAREE');
      req.flush(fakeLivraison);
    });

    it('should return the created livraison', () => {
      let result: Livraison | undefined;
      service.createLivraison(fakeLivraison).subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/livraisons`).flush(fakeLivraison);
      expect(result?.id).toBe(1);
    });
  });

  // ════════════════════════════════════════════════════════
  // 5. updateStatus()
  // ════════════════════════════════════════════════════════
  describe('updateStatus()', () => {

    it('should call PUT /api/livraisons/:id/status', () => {
      service.updateStatus(1, { statut: 'LIVREE' }).subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons/1/status`);
      expect(req.request.method).toBe('PUT');
      req.flush({ ...fakeLivraison, statut: 'LIVREE' });
    });

    it('should send new statut in body', () => {
      service.updateStatus(1, { statut: 'LIVREE' }).subscribe();
      const req = httpMock.expectOne(`${BASE}/livraisons/1/status`);
      expect(req.request.body.statut).toBe('LIVREE');
      req.flush({ ...fakeLivraison, statut: 'LIVREE' });
    });

    it('should return updated livraison', () => {
      let result: Livraison | undefined;
      service.updateStatus(1, { statut: 'LIVREE' })
        .subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/livraisons/1/status`)
        .flush({ ...fakeLivraison, statut: 'LIVREE' });
      expect(result?.statut).toBe('LIVREE');
    });
  });

  // ════════════════════════════════════════════════════════
  // 6. getAllTransporteurs()
  // ════════════════════════════════════════════════════════
  describe('getAllTransporteurs()', () => {

    it('should call GET /api/transporteurs', () => {
      service.getAllTransporteurs().subscribe();
      const req = httpMock.expectOne(`${BASE}/transporteurs`);
      expect(req.request.method).toBe('GET');
      req.flush([fakeTransporteur]);
    });

    it('should return list of transporteurs', () => {
      let result: Transporteur[] = [];
      service.getAllTransporteurs().subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/transporteurs`).flush([fakeTransporteur]);
      expect(result.length).toBe(1);
      expect(result[0].nomSociete).toBe('TransTunis');
    });
  });

  // ════════════════════════════════════════════════════════
  // 7. getAllDeliveryUsers()
  // ════════════════════════════════════════════════════════
  describe('getAllDeliveryUsers()', () => {

    const allUsers = [
      { id: 1, fullName: 'Admin User',   email: 'admin@test.com',   role: 'ADMIN' },
      { id: 2, fullName: 'Ahmed Livreur', email: 'ahmed@test.com',  role: 'DELIVERY' },
      { id: 3, fullName: 'Sara Livreur',  email: 'sara@test.com',   role: 'DELIVERY' },
      { id: 4, fullName: 'Player One',    email: 'player@test.com', role: 'PLAYER' },
    ];

    it('should call GET /api/users', () => {
      service.getAllDeliveryUsers().subscribe();
      const req = httpMock.expectOne(`${BASE}/users`);
      expect(req.request.method).toBe('GET');
      req.flush(allUsers);
    });

    it('should return only users with role DELIVERY', () => {
      let result: any[] = [];
      service.getAllDeliveryUsers().subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/users`).flush(allUsers);
      expect(result.length).toBe(2);
      result.forEach(u => expect(u.role).toBe('DELIVERY'));
    });

    it('should return empty array when no DELIVERY users', () => {
      let result: any[] = [];
      service.getAllDeliveryUsers().subscribe(data => result = data);
      httpMock.expectOne(`${BASE}/users`).flush([
        { id: 1, fullName: 'Admin', email: 'admin@test.com', role: 'ADMIN' }
      ]);
      expect(result).toEqual([]);
    });
  });

  // ════════════════════════════════════════════════════════
  // 8. deleteTransporteur()
  // ════════════════════════════════════════════════════════
  describe('deleteTransporteur()', () => {

    it('should call DELETE /api/transporteurs/:id', () => {
      service.deleteTransporteur(1).subscribe();
      const req = httpMock.expectOne(`${BASE}/transporteurs/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush('Transporteur supprimé avec succès');
    });
  });

});
