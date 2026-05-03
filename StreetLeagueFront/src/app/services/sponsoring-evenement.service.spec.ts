import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SponsoringEvenementService } from './sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../models/sponsoring-evenement-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('SponsoringEvenementService', () => {
  let service: SponsoringEvenementService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SponsoringEvenementService],
    });
    service = TestBed.inject(SponsoringEvenementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call create with POST', () => {
    const dto: SponsoringEvenementDTO = {
      id: 0,
      sponsorId: 1,
      evenementId: 5,
      contribution: 1200,
      typeContribution: 'Cash',
      statut: 'PENDING'
    };

    service.create(dto).subscribe((res) => expect(res).toEqual(dto));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/sponsoring`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush(dto);
  });

  it('should call delete with DELETE', () => {
    service.delete(7).subscribe((res) => expect(res).toBeNull());

    const req = httpMock.expectOne(`${API_BASE_URL}/api/sponsoring/7`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

