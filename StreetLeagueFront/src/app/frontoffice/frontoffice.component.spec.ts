import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

import { FrontofficeComponent } from './frontoffice.component';

describe('FrontofficeComponent', () => {
  let component: FrontofficeComponent;
  let fixture: ComponentFixture<FrontofficeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FrontofficeComponent],
      imports: [RouterTestingModule], // pour <router-outlet>
      schemas: [NO_ERRORS_SCHEMA]     // ignore navbar/footer
    }).compileComponents();

    fixture = TestBed.createComponent(FrontofficeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});