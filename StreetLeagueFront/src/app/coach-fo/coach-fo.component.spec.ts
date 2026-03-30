import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { CoachFOComponent } from './coach-fo.component';

describe('CoachFOComponent', () => {
  let component: CoachFOComponent;
  let fixture: ComponentFixture<CoachFOComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CoachFOComponent],
      schemas: [NO_ERRORS_SCHEMA],
    });
    fixture = TestBed.createComponent(CoachFOComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
