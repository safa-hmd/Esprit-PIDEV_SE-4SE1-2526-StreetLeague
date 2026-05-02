import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EvenementCommunauteService } from './evenement-communaute.service';
import { EvenementCommunauteDTO } from '../models/evenement-communaute-dto';
import { API_BASE_URL } from 'src/environments/api-url';

describe('EvenementCommunauteService', () => {
  let service: EvenementCommunauteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EvenementCommunauteService],
    });
    service = TestBed.inject(EvenementCommunauteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call getById with GET', () => {
    const dto: EvenementCommunauteDTO = {
      id: 5,
      titre: 'Tournoi',
      description: 'Evenement test',
      date: new Date('2026-06-01'),
      communauteId: 2,
      organisateurId: 3,
    };

    service.getById(5).subscribe((res) => expect(res).toEqual(dto));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/evenement/5`);
    expect(req.request.method).toBe('GET');
    req.flush(dto);
  });

  it('should call update with PUT', () => {
    const dto: EvenementCommunauteDTO = {
      id: 5,
      titre: 'Tournoi MAJ',
      description: 'Evenement test',
      date: new Date('2026-06-01'),
      communauteId: 2,
      organisateurId: 3,
    };

    service.update(5, dto).subscribe((res) => expect(res).toEqual(dto));

    const req = httpMock.expectOne(`${API_BASE_URL}/api/evenement/5`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(dto);
    req.flush(dto);
  });
});

