import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { DeliveryFoComponent } from './delivery-fo.component';

describe('DeliveryFoComponent', () => {
  let component: DeliveryFoComponent;
  let fixture: ComponentFixture<DeliveryFoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,  // ← pour AuthService qui injecte HttpClient
        RouterTestingModule,      // ← pour Router injecté dans DeliveryFoComponent
      ],
      declarations: [DeliveryFoComponent],
      schemas: [NO_ERRORS_SCHEMA]  // ← ignore router-outlet et autres éléments inconnus
    });
    fixture = TestBed.createComponent(DeliveryFoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});