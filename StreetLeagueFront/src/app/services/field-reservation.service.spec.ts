import { TestBed } from '@angular/core/testing';

import { FieldReservationService } from './field-reservation.service';

describe('FieldReservationService', () => {
  let service: FieldReservationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FieldReservationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
