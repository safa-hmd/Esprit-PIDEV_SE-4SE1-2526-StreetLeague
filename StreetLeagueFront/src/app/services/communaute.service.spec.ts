import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommunauteService } from './communaute.service';
import { CommunauteDTO } from '../models/communaute-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('CommunauteService', () => {
  let service: CommunauteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CommunauteService],
    });
    service = TestBed.inject(CommunauteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call getAll with GET', () => {
    const data: CommunauteDTO[] = [
      { id: 1, nom: 'Street', description: 'Desc', type: 'Sport', dateCreation: new Date(), createurId: 10 },
    ];
    service.getAll().subscribe((res) => expect(res).toEqual(data));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/communaute`);
    expect(req.request.method).toBe('GET');
    req.flush(data);
  });

  it('should call create with POST', () => {
    const payload: CommunauteDTO = {
      id: 0, nom: 'New', description: 'D', type: 'Sport', dateCreation: new Date(), createurId: 11,
    };
    service.create(payload).subscribe((res) => expect(res).toEqual(payload));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/communaute`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(payload);
  });
});
