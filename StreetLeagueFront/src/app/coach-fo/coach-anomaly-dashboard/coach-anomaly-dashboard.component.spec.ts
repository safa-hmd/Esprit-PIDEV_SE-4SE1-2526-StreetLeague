import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoachAnomalyDashboardComponent } from './coach-anomaly-dashboard.component';

describe('CoachAnomalyDashboardComponent', () => {
  let component: CoachAnomalyDashboardComponent;
  let fixture: ComponentFixture<CoachAnomalyDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CoachAnomalyDashboardComponent]
    });
    fixture = TestBed.createComponent(CoachAnomalyDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
