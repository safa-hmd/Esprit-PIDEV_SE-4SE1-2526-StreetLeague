import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldReservationComponent } from './field-reservation.component';

describe('FieldReservationComponent', () => {
  let component: FieldReservationComponent;
  let fixture: ComponentFixture<FieldReservationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FieldReservationComponent]
    });
    fixture = TestBed.createComponent(FieldReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
