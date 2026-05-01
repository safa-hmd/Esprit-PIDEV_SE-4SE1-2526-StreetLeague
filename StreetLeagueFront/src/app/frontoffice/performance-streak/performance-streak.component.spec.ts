import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceStreakComponent } from './performance-streak.component';

describe('PerformanceStreakComponent', () => {
  let component: PerformanceStreakComponent;
  let fixture: ComponentFixture<PerformanceStreakComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PerformanceStreakComponent]
    });
    fixture = TestBed.createComponent(PerformanceStreakComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
