import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HealthComponent } from './health.component';
import { HealthDashboardService } from '../../services/healthdashboard.service';
import { of, throwError } from 'rxjs';

describe('HealthComponent (Backoffice) - Contrôles de Saisie', () => {
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

  describe('Validation - Chargement des données', () => {
    it('devrait charger les rappels au démarrage', (done) => {
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

    it('devrait gérer erreur lors du chargement des rappels', (done) => {
      healthService.getAllReminders.and.returnValue(throwError(() => ({ status: 500 })));
      component.ngOnInit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.reminders.length).toBe(0);
        done();
      }, 100);
    });
  });

  describe('Validation - Filtrage par recherche', () => {
    beforeEach(() => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: 'John Doe', userEmail: 'john@example.com' },
        { id: 2, frequency: 30, quantity: 500, active: true, userName: 'Jane Smith', userEmail: 'jane@example.com' }
      ];
      component.filteredReminders = [...component.reminders];
    });

    it('devrait filtrer par nom d\'utilisateur', () => {
      const event = { target: { value: 'john' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
      expect(component.filteredReminders[0].userName).toBe('John Doe');
    });

    it('devrait filtrer par email', () => {
      const event = { target: { value: 'jane@' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
      expect(component.filteredReminders[0].userEmail).toBe('jane@example.com');
    });

    it('devrait ignorer la casse lors du filtrage', () => {
      const event = { target: { value: 'JOHN' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(1);
    });

    it('devrait retourner tous les rappels avec recherche vide', () => {
      const event = { target: { value: '' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(2);
    });

    it('devrait retourner tableau vide si pas de correspondance', () => {
      const event = { target: { value: 'xyz123' } };
      component.onSearch(event);
      expect(component.filteredReminders.length).toBe(0);
    });
  });

  describe('Validation - Calcul des statistiques', () => {
    beforeEach(() => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 300, active: true, userName: 'User 1', userEmail: 'user1@example.com' },
        { id: 2, frequency: 30, quantity: 200, active: true, userName: 'User 2', userEmail: 'user2@example.com' },
        { id: 3, frequency: 90, quantity: 250, active: false, userName: 'User 3', userEmail: 'user3@example.com' }
      ];
    });

    it('devrait calculer le nombre total d\'utilisateurs', () => {
      expect(component.totalUsers).toBe(3);
    });

    it('devrait retourner 0 si pas de rappels', () => {
      component.reminders = [];
      expect(component.totalUsers).toBe(0);
      expect(component.avgQuantity).toBe('0 ml');
      expect(component.avgFrequency).toBe('-');
    });

    it('devrait calculer les rappels actifs correctement', () => {
      expect(component.activeReminders).toBe(2);
    });

    it('devrait calculer la quantité moyenne correctement', () => {
      expect(component.avgQuantity).toBe('250 ml');
    });

    it('devrait calculer la fréquence moyenne correctement', () => {
      expect(component.avgFrequency).toBe('60 min');
    });
  });

  describe('Validation - Formatage des labels de fréquence', () => {
    it('devrait formater les minutes < 60', () => {
      expect(component.freqLabel(30)).toBe('30 min');
      expect(component.freqLabel(59)).toBe('59 min');
    });

    it('devrait formater 60 min comme 1 heure', () => {
      expect(component.freqLabel(60)).toBe('1 heure');
    });

    it('devrait formater les heures > 60 min', () => {
      expect(component.freqLabel(120)).toBe('2 heures');
      expect(component.freqLabel(90)).toBe('1.5 heures');
    });

    it('devrait gérer 0 minute', () => {
      expect(component.freqLabel(0)).toBe('0 min');
    });
  });

  describe('Validation - Valeurs de données invalides', () => {
    it('devrait gérer userName null sans erreur', () => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: null as any, userEmail: 'test@example.com' }
      ];
      component.filteredReminders = [...component.reminders];

      expect(() => {
        component.onSearch({ target: { value: 'test' } });
      }).not.toThrow();
    });

    it('devrait gérer userEmail null sans erreur', () => {
      component.reminders = [
        { id: 1, frequency: 60, quantity: 250, active: true, userName: 'User', userEmail: null as any }
      ];
      component.filteredReminders = [...component.reminders];

      expect(() => {
        component.onSearch({ target: { value: 'test' } });
      }).not.toThrow();
    });
  });

  describe('Initialisation du composant', () => {
    it('devrait être créé', () => {
      expect(component).toBeTruthy();
    });

    it('devrait initialiser avec des valeurs par défaut', () => {
      expect(component.isLoading).toBe(true);
      expect(component.reminders).toEqual([]);
      expect(component.filteredReminders).toEqual([]);
    });
  });
});
