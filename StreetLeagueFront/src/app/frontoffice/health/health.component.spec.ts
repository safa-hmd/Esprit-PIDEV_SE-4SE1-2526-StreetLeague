import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HealthComponent } from './health.component';
import { HealthService } from '../../services/health.service';
import { of } from 'rxjs';

describe('HealthComponent (Frontoffice) - Contrôles de Saisie', () => {
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

  describe('Validation - Calcul du BMI', () => {
    it('devrait calculer le BMI correctement', () => {
      component.weight = 70;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('22.9');
    });

    it('ne devrait pas calculer si poids absent', () => {
      component.weight = 0;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiResult).toBe('');
    });

    it('devrait catégoriser BMI insuffisant', () => {
      component.weight = 50;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Insuffisant');
    });

    it('devrait catégoriser BMI normal', () => {
      component.weight = 70;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Normal');
    });

    it('devrait catégoriser BMI surpoids', () => {
      component.weight = 85;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Surpoids');
    });

    it('devrait catégoriser BMI obésité', () => {
      component.weight = 100;
      component.height = 175;
      component.calculateBMI();

      expect(component.bmiLabel).toBe('Obésité');
    });
  });

  describe('Validation - Quantité d\'eau', () => {
    beforeEach(() => {
      component.amountPerReminder = 250;
    });

    it('devrait augmenter la quantité', () => {
      component.changeQuantity(50);
      expect(component.amountPerReminder).toBe(300);
    });

    it('devrait diminuer la quantité', () => {
      component.changeQuantity(-50);
      expect(component.amountPerReminder).toBe(200);
    });

    it('ne devrait pas descendre en dessous de 100 ml', () => {
      component.changeQuantity(-200);
      expect(component.amountPerReminder).toBeGreaterThanOrEqual(100);
    });

    it('ne devrait pas dépasser 2000 ml', () => {
      component.changeQuantity(2000);
      expect(component.amountPerReminder).toBeLessThanOrEqual(2000);
    });
  });

  describe('Validation - Fréquence', () => {
    it('devrait changer la fréquence', () => {
      component.setFrequency(30);
      expect(component.reminderFrequency).toBe(30);
    });
  });

  describe('Validation - Historique d\'hydratation', () => {
    beforeEach(() => {
      component.glassCount = 0;
      component.glassTarget = 8;
      component.amountPerReminder = 250;
      component.drinkHistory = [];
    });

    it('devrait incrémenter les verres', () => {
      component.drinkNow();
      expect(component.glassCount).toBe(1);
      expect(component.drinkHistory.length).toBe(1);
    });

    it('devrait calculer le pourcentage d\'hydration', () => {
      component.glassCount = 4;
      component.glassTarget = 8;
      component.updateProgress();

      expect(component.hydrationPercentage).toBe(50);
    });

    it('devrait charger l\'historique du jour', () => {
      const today = new Date().toDateString();
      const stored = [{ time: '10:30', quantity: 250 }];
      spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(stored));

      component.loadTodayHistory();

      expect(component.drinkHistory).toEqual(stored);
    });
  });

  describe('Validation - Différence de poids', () => {
    it('devrait identifier différence positive', () => {
      component.weightDifference = '5.0';
      expect(component.isWeightDifferencePositive()).toBe(true);
      expect(component.getWeightDifferenceDisplay()).toBe('+5.0');
    });

    it('devrait identifier différence négative', () => {
      component.weightDifference = '-3.5';
      expect(component.isWeightDifferenceNegative()).toBe(true);
    });
  });

  it('devrait être créé', () => {
    expect(component).toBeTruthy();
  });
});
