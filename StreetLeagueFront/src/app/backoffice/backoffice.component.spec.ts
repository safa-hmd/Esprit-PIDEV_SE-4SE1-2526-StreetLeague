import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';


import { BackofficeComponent } from './backoffice.component';

describe('BackofficeComponent', () => {
  let component: BackofficeComponent;
  let fixture: ComponentFixture<BackofficeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({

      imports: [RouterTestingModule],      // ← pour router-outlet
      declarations: [BackofficeComponent],
      schemas: [NO_ERRORS_SCHEMA]          // ← ignore app-header, app-sidebar inconnus

    });
    fixture = TestBed.createComponent(BackofficeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});