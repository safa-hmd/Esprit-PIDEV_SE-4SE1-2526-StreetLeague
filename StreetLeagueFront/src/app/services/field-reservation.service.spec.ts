import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FieldReservationService } from './field-reservation.service';

describe('FieldReservationService', () => {
  let service: FieldReservationService;
  let httpMock: HttpTestingController;

  const reservationApi = 'http://localhost:8086/StreetLeague/api/reservations';
  const fieldApi = 'http://localhost:8086/StreetLeague/api/fields';

  const mockReservation: any = {
    id: 1,
    fieldId: 1,
    fieldName: 'Field 1',
    fieldLocation: 'Tunis',
    playerId: 1,
    playerUsername: 'testuser',
    startTime: '2026-03-29T10:00:00',
    endTime: '2026-03-29T12:00:00',
    notes: 'Test notes',
    status: 'PENDING',
    totalPrice: 100,
    adminNote: '',
    createdAt: '2026-03-29T09:00:00'
  };

  const mockField: any = {
    id: 1,
    name: 'Field 1',
    description: 'Test field',
    sportType: 'FOOTBALL',
    location: 'Tunis',
    imageUrl: 'http://example.com/image.jpg',
    pricePerHour: 50,
    capacity: 10,
    available: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(FieldReservationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ─── RESERVATIONS ─────────────────────────────────────────────────

  it('should get all reservations', () => {
    service.getAllReservations().subscribe((res: any) => {
      expect(res.length).toBe(1);
      expect(res[0]).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(reservationApi);
    expect(req.request.method).toBe('GET');
    req.flush([mockReservation]);
  });

  it('should get pending reservations', () => {
    service.getPendingReservations().subscribe((res: any) => {
      expect(res).toEqual([mockReservation]);
    });
    const req = httpMock.expectOne(`${reservationApi}/pending`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReservation]);
  });

  it('should get reservation by id', () => {
    service.getReservationById(1).subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockReservation);
  });

  it('should get reservations by field', () => {
    service.getReservationsByField(1).subscribe((res: any) => {
      expect(res).toEqual([mockReservation]);
    });
    const req = httpMock.expectOne(`${reservationApi}/field/1`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReservation]);
  });

  it('should get reservations by player', () => {
    service.getReservationsByPlayer(1).subscribe((res: any) => {
      expect(res).toEqual([mockReservation]);
    });
    const req = httpMock.expectOne(`${reservationApi}/player/1`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReservation]);
  });

  it('should create a reservation', () => {
    service.createReservation(mockReservation).subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(reservationApi);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockReservation);
    req.flush(mockReservation);
  });

  it('should approve a reservation with adminNote', () => {
    service.approveReservation(1, 'Approved').subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1/approve`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ adminNote: 'Approved' });
    req.flush(mockReservation);
  });

  it('should approve a reservation without adminNote', () => {
    service.approveReservation(1).subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1/approve`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(mockReservation);
  });

  it('should reject a reservation with adminNote', () => {
    service.rejectReservation(1, 'Rejected').subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1/reject`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ adminNote: 'Rejected' });
    req.flush(mockReservation);
  });

  it('should reject a reservation without adminNote', () => {
    service.rejectReservation(1).subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1/reject`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(mockReservation);
  });

  it('should cancel a reservation', () => {
    service.cancelReservation(1, 42).subscribe((res: any) => {
      expect(res).toEqual(mockReservation);
    });
    const req = httpMock.expectOne(`${reservationApi}/1/cancel?playerId=42`);
    expect(req.request.method).toBe('PATCH');
    req.flush(mockReservation);
  });

  it('should delete a reservation', () => {
    service.deleteReservation(1).subscribe((res: any) => {
      expect(res).toBeNull();
    });
    const req = httpMock.expectOne(`${reservationApi}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  // ─── FIELDS ───────────────────────────────────────────────────────

  it('should get all fields', () => {
    service.getAllFields().subscribe((res: any) => {
      expect(res).toEqual([mockField]);
    });
    const req = httpMock.expectOne(fieldApi);
    expect(req.request.method).toBe('GET');
    req.flush([mockField]);
  });

  it('should get available fields', () => {
    service.getAvailableFields().subscribe((res: any) => {
      expect(res).toEqual([mockField]);
    });
    const req = httpMock.expectOne(`${fieldApi}/available`);
    expect(req.request.method).toBe('GET');
    req.flush([mockField]);
  });

  it('should get field by id', () => {
    service.getFieldById(1).subscribe((res: any) => {
      expect(res).toEqual(mockField);
    });
    const req = httpMock.expectOne(`${fieldApi}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockField);
  });

  it('should create a field', () => {
    service.createField(mockField).subscribe((res: any) => {
      expect(res).toEqual(mockField);
    });
    const req = httpMock.expectOne(fieldApi);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockField);
    req.flush(mockField);
  });

  it('should update a field', () => {
    service.updateField(1, mockField).subscribe((res: any) => {
      expect(res).toEqual(mockField);
    });
    const req = httpMock.expectOne(`${fieldApi}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockField);
    req.flush(mockField);
  });

  it('should toggle field availability', () => {
    service.toggleAvailability(1).subscribe((res: any) => {
      expect(res).toEqual(mockField);
    });
    const req = httpMock.expectOne(`${fieldApi}/1/toggle`);
    expect(req.request.method).toBe('PATCH');
    req.flush(mockField);
  });

  it('should delete a field', () => {
    service.deleteField(1).subscribe((res: any) => {
      expect(res).toBeNull();
    });
    const req = httpMock.expectOne(`${fieldApi}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});