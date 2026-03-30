import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ContratSponsorService } from './contrat-sponsor.service';
import { ContratSponsorDTO } from '../models/contrat-sponsor-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('ContratSponsorService', () => {
  let service: ContratSponsorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ContratSponsorService],
    });
    service = TestBed.inject(ContratSponsorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call getAll with GET', () => {
    const list: ContratSponsorDTO[] = [{
      id: 1,
      sponsorId: 2,
      equipeId: 3,
      montant: 1000,
      dateDebut: new Date('2026-01-01'),
      dateFin: new Date('2026-12-31'),
      statut: 'ACTIF',
      conditions: 'OK',
    }];

    service.getAll().subscribe((res) => expect(res).toEqual(list));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/contrat`);
    expect(req.request.method).toBe('GET');
    req.flush(list);
  });

  it('should call update with PUT', () => {
    const dto: ContratSponsorDTO = {
      id: 1,
      sponsorId: 2,
      equipeId: 4,
      montant: 1500,
      dateDebut: new Date('2026-01-01'),
      dateFin: new Date('2026-12-31'),
      statut: 'ACTIF',
      conditions: 'Maj',
    };

    service.update(1, dto).subscribe((res) => expect(res).toEqual(dto));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/contrat/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(dto);
    req.flush(dto);
  });
});
