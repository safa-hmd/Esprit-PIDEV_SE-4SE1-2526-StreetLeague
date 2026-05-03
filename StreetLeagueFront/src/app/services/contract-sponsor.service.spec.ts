import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ContractSponsorService } from './contract-sponsor.service';
import { ContractSponsorDTO } from '../models/contract-sponsor-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('ContractSponsorService', () => {
  let service: ContractSponsorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ContractSponsorService],
    });
    service = TestBed.inject(ContractSponsorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call getAll with GET', () => {
    const list: ContractSponsorDTO[] = [{
      id: 1,
      sponsorId: 2,
      equipeId: 3,
      montant: 1000,
      dateDebut: new Date('2026-01-01'),
      dateFin: new Date('2026-12-31'),
      statut: 'ACTIVE',
      conditions: 'OK',
    }];

    service.getAll().subscribe((res) => expect(res).toEqual(list));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/contract`);
    expect(req.request.method).toBe('GET');
    req.flush(list);
  });

  it('should call update with PUT', () => {
    const dto: ContractSponsorDTO = {
      id: 1,
      sponsorId: 2,
      equipeId: 4,
      montant: 1500,
      dateDebut: new Date('2026-01-01'),
      dateFin: new Date('2026-12-31'),
      statut: 'ACTIVE',
      conditions: 'Maj',
    };

    service.update(1, dto).subscribe((res) => expect(res).toEqual(dto));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/contract/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(dto);
    req.flush(dto);
  });
});

