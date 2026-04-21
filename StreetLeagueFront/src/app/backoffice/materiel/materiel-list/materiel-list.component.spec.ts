import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { MaterielListComponent } from './materiel-list.component';

describe('MaterielListComponent', () => {
  let component: MaterielListComponent;
  let fixture: ComponentFixture<MaterielListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,  // ← pour MaterielService
        FormsModule,              // ← pour [(ngModel)]
      ],
      declarations: [MaterielListComponent],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(MaterielListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});