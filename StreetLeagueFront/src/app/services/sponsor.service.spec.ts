import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SponsorService } from './sponsor.service';
import { SponsorDTO } from '../models/sponsor-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('SponsorService', () => {
  let service: SponsorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SponsorService],
    });
    service = TestBed.inject(SponsorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call getById with GET', () => {
    const sponsor: SponsorDTO = {
      id: 3, nom: 'Nike', type: 'Gold', contactEmail: 'nike@test.com', telephone: '12345678', adresse: 'Tunis',
    };
    service.getById(3).subscribe((res) => expect(res).toEqual(sponsor));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/sponsor/3`);
    expect(req.request.method).toBe('GET');
    req.flush(sponsor);
  });

  it('should call delete with DELETE', () => {
    service.delete(9).subscribe((res) => expect(res).toBeNull());

    const req = httpMock.expectOne(`${API_BASE_URL}/api/sponsor/9`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
