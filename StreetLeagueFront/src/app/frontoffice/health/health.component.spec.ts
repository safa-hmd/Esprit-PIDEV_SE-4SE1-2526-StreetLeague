import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HealthComponent } from './health.component';
import { HealthService } from '../../services/health.service';
import { of } from 'rxjs';

describe('HealthComponent (Frontoffice) - Input Validation', () => {
  let component: HealthComponent;
  let fixture: ComponentFixture<HealthComponent>;
  let healthService: jasmine.SpyObj<HealthService>;

  beforeEach(async () => {
    const healthServiceSpy = jasmine.createSpyObj('HealthService', ['addReminder', 'updateReminder']);

    await TestBed.configureTestingModule({
      declarations: [HealthComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provide: HealthService, useValue: healthServiceSpy }
      ]
    }).compileComponents();

    healthService = TestBed.inject(HealthService) as jasmine.SpyObj<HealthService>;
    fixture = TestBed.createComponent(HealthComponent);
    component = fixture.componentInstance;
  });

  describe('Validation - BMI Calculation', () => {
    it('should calculate BMI correctly', () => {
      component.weight = 70;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('22.9');
    });

    it('should not calculate if weight is missing', () => {
      component.weight = 0;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('');
    });

    it('should reject negative weight values', () => {
      component.weight = -70;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('');
      expect(component.showNotification).toBe(true);
      expect(component.notificationMessage).toContain('Weight must be positive');
    });

    it('should reject negative height values', () => {
      component.weight = 70;
      component.height = -175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('');
      expect(component.showNotification).toBe(true);
      expect(component.notificationMessage).toContain('Height must be positive');
    });

    it('should categorize BMI as Underweight', () => {
      component.weight = 50;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Insuffisant');
    });

    it('should categorize BMI as Normal', () => {
      component.weight = 70;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Normal');
    });

    it('should categorize BMI as Overweight', () => {
      component.weight = 85;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Surpoids');
    });

    it('should categorize BMI as Obesity', () => {
      component.weight = 100;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Obésité');
    });
  });

  describe('Validation - Water Quantity', () => {
    beforeEach(() => {
      component.amountPerReminder = 250;
    });

    it('should increase quantity', () => {
      component.changeQuantity(50);
      expect(component.amountPerReminder).toBe(300);
    });

    it('should decrease quantity', () => {
      component.changeQuantity(-50);
      expect(component.amountPerReminder).toBe(200);
    });

    it('should not go below 100 ml', () => {
      component.changeQuantity(-200);
      expect(component.amountPerReminder).toBeGreaterThanOrEqual(100);
    });

    it('should not exceed 2000 ml', () => {
      component.changeQuantity(2000);
      expect(component.amountPerReminder).toBeLessThanOrEqual(2000);
    });
  });

  describe('Validation - Reminder Frequency', () => {
    it('should change reminder frequency', () => {
      component.setFrequency(30);
      expect(component.reminderFrequency).toBe(30);
    });
  });

  describe('Validation - Hydration History', () => {
    beforeEach(() => {
      component.glassCount = 0;
      component.glassTarget = 8;
      component.amountPerReminder = 250;
      component.drinkHistory = [];
    });

    it('should increment glass count', () => {
      component.drinkNow();
      expect(component.glassCount).toBe(1);
      expect(component.drinkHistory.length).toBe(1);
    });

    it('should calculate hydration percentage', () => {
      component.glassCount = 4;
      component.glassTarget = 8;
      component.updateProgress();

      expect(component.hydrationPercentage).toBe(50);
    });

    it('should load today history', () => {
      const today = new Date().toDateString();
      const stored = [{ time: '10:30', quantity: 250 }];
      spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(stored));

      component.loadTodayHistory();

      expect(component.drinkHistory).toEqual(stored);
    });
  });

  describe('Validation - Weight Difference', () => {
    it('should identify positive weight difference', () => {
      component.weightDifference = '5.0';
      expect(component.isWeightDifferencePositive()).toBe(true);
      expect(component.getWeightDifferenceDisplay()).toBe('+5.0');
    });

    it('should identify negative weight difference', () => {
      component.weightDifference = '-3.5';
      expect(component.isWeightDifferenceNegative()).toBe(true);
    });
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
  });
});
