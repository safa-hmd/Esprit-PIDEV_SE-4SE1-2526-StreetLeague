import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { TransporteurListComponent } from './transporteur-list.component';

describe('TransporteurListComponent', () => {
  let component: TransporteurListComponent;
  let fixture: ComponentFixture<TransporteurListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,  // ← pour LivraisonService
        FormsModule,              // ← pour [(ngModel)]
      ],
      declarations: [TransporteurListComponent],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(TransporteurListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});