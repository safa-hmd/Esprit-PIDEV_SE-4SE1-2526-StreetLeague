import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HealthComponent } from './health.component';
import { HealthDashboardService } from '../../services/healthdashboard.service';
import { of, throwError } from 'rxjs';

describe('HealthComponent (Backoffice) - Input Validation', () => {
  let component: HealthComponent;
  let fixture: ComponentFixture<HealthComponent>;
  let healthService: jasmine.SpyObj<HealthDashboardService>;

  beforeEach(async () => {
    const healthServiceSpy = jasmine.createSpyObj('HealthDashboardService', ['getAllReminders']);

    await TestBed.configureTestingModule({
      declarations: [HealthComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provide: HealthDashboardService, useValue: healthServiceSpy }
      ]
    }).compileComponents();

    healthService = TestBed.inject(HealthDashboardService) as jasmine.SpyObj<HealthDashboardService>;
    fixture = TestBed.createComponent(HealthComponent);
    component = fixture.componentInstance;
  });

  describe('Validation - Data Loading', () => {
    it('should load reminders on initialization', (done) => {
      const mockReminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: 'John', userEmail: 'john@example.com' },
        { id: 2, frequency: 30, quantity: 500, active: true, userName: 'Jane', userEmail: 'jane@example.com' }
      ];

      healthService.getAllReminders.and.returnValue(of(mockReminders));
      component.ngOnInit();

      setTimeout(() => {
        expect(component.reminders.length).toBe(2);
        expect(component.filteredReminders.length).toBe(2);
        expect(component.isLoading).toBe(false);
        done();
      }, 150);
    });

    it('should handle error loading reminders', (done) => {
      healthService.getAllReminders.and.returnValue(throwError(() => ({ status: 500 })));
      component.ngOnInit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.reminders.length).toBe(0);
        done();
      }, 100);
    });
  });

  describe('Validation - Search Filtering', () => {
    beforeEach(() => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: 'John Doe', userEmail: 'john@example.com' },
        { id: 2, frequency: 30, quantity: 500, active: true, userName: 'Jane Smith', userEmail: 'jane@example.com' }
      ];
      component.filteredReminders = [...component.reminders];
    });

    it('should filter by username', () => {
      const event = { target: { value: 'john' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
      expect(component.filteredReminders[0].userName).toBe('John Doe');
    });

    it('should filter by email', () => {
      const event = { target: { value: 'jane@' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
      expect(component.filteredReminders[0].userEmail).toBe('jane@example.com');
    });

    it('should be case insensitive', () => {
      const event = { target: { value: 'JOHN' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
    });

    it('should return all reminders with empty search', () => {
      const event = { target: { value: '' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(2);
    });

    it('should return empty array if no match', () => {
      const event = { target: { value: 'xyz123' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(0);
    });
  });

  describe('Validation - Statistics Calculation', () => {
    beforeEach(() => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 300, active: true, userName: 'User 1', userEmail: 'user1@example.com' },
        { id: 2, frequency: 30, quantity: 200, active: true, userName: 'User 2', userEmail: 'user2@example.com' },
        { id: 3, frequency: 90, quantity: 250, active: false, userName: 'User 3', userEmail: 'user3@example.com' }
      ];
    });

    it('should calculate total users', () => {
      expect(component.totalUsers).toBe(3);
    });

    it('should return 0 if no reminders', () => {
      component.reminders = [];
      expect(component.totalUsers).toBe(0);
      expect(component.avgQuantity).toBe('0 ml');
      expect(component.avgFrequency).toBe('-');
    });

    it('should calculate active reminders correctly', () => {
      expect(component.activeReminders).toBe(2);
    });

    it('should calculate average quantity correctly', () => {
      expect(component.avgQuantity).toBe('250 ml');
    });

    it('should calculate average frequency correctly', () => {
      expect(component.avgFrequency).toBe('60 min');
    });
  });

  describe('Validation - Frequency Label Formatting', () => {
    it('should format minutes < 60', () => {
      expect(component.freqLabel(30)).toBe('30 min');
      expect(component.freqLabel(59)).toBe('59 min');
    });

    it('should format 60 min as 1 hour', () => {
      expect(component.freqLabel(60)).toBe('1 heure');
    });

    it('should format hours > 60 min', () => {
      expect(component.freqLabel(120)).toBe('2 heures');
      expect(component.freqLabel(90)).toBe('1.5 heures');
    });

    it('should handle 0 minute', () => {
      expect(component.freqLabel(0)).toBe('0 min');
    });
  });

  describe('Validation - Invalid Data Values', () => {
    it('should handle null userName without error', () => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: null as any, userEmail: 'test@example.com' }
      ];
      component.filteredReminders = [...component.reminders];

      expect(() => {
        component.onSearch({ target: { value: 'test' } });
      }).not.toThrow();
    });

    it('should handle null userEmail without error', () => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: 'User', userEmail: null as any }
      ];
      component.filteredReminders = [...component.reminders];

      expect(() => {
        component.onSearch({ target: { value: 'test' } });
      }).not.toThrow();
    });
  });

  describe('Component Initialization', () => {
    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
      expect(component.isLoading).toBe(true);
      expect(component.reminders).toEqual([]);
      expect(component.filteredReminders).toEqual([]);
    });
  });
});
