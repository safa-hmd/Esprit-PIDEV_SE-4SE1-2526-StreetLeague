import { environment } from 'src/environments/environment';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HealthDashboardService, WaterReminderResponse } from './healthdashboard.service';

describe('HealthDashboardService', () => {
  let service: HealthDashboardService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HealthDashboardService]
    });
    service = TestBed.inject(HealthDashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllReminders', () => {
    it('should fetch all water reminders from API', (done) => {
      const mockReminders: WaterReminderResponse[] = [
        {
          id: 1,
          frequency: 60,
          quantity: 250,
          active: true,
          userName: 'John Doe',
          userEmail: 'john@example.com'
        },
        {
          id: 2,
          frequency: 30,
          quantity: 500,
          active: true,
          userName: 'Jane Smith',
          userEmail: 'jane@example.com'
        },
        {
          id: 3,
          frequency: 90,
          quantity: 200,
          active: false,
          userName: 'Mike Davis',
          userEmail: 'mike@example.com'
        }
      ];

      service.getAllReminders().subscribe((reminders) => {
        expect(reminders.length).toBe(3);
        expect(reminders).toEqual(mockReminders);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReminders);
    });

    it('should return correct reminder properties', (done) => {
      const mockReminders: WaterReminderResponse[] = [
        {
          id: 1,
          frequency: 45,
          quantity: 300,
          active: true,
          userName: 'Alice Johnson',
          userEmail: 'alice@example.com'
        }
      ];

      service.getAllReminders().subscribe((reminders) => {
        expect(reminders[0].frequency).toBe(45);
        expect(reminders[0].quantity).toBe(300);
        expect(reminders[0].active).toBe(true);
        expect(reminders[0].userName).toBe('Alice Johnson');
        expect(reminders[0].userEmail).toBe('alice@example.com');
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush(mockReminders);
    });

    it('should return empty array when no reminders exist', (done) => {
      service.getAllReminders().subscribe((reminders) => {
        expect(reminders.length).toBe(0);
        expect(reminders).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush([]);
    });

    it('should handle error when fetching reminders', (done) => {
      service.getAllReminders().subscribe(
        () => fail('should have failed with 500 error'),
        (error) => {
          expect(error.status).toBe(500);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });

    it('should handle error with 403 when not authorized', (done) => {
      service.getAllReminders().subscribe(
        () => fail('should have failed with 403 error'),
        (error) => {
          expect(error.status).toBe(403);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });
    });

    it('should include active reminders mixed with inactive', (done) => {
      const mockReminders: WaterReminderResponse[] = [
        {
          id: 1,
          frequency: 60,
          quantity: 250,
          active: true,
          userName: 'User 1',
          userEmail: 'user1@example.com'
        },
        {
          id: 2,
          frequency: 60,
          quantity: 250,
          active: false,
          userName: 'User 2',
          userEmail: 'user2@example.com'
        }
      ];

      service.getAllReminders().subscribe((reminders) => {
        const activeCount = reminders.filter(r => r.active).length;
        const inactiveCount = reminders.filter(r => !r.active).length;
        expect(activeCount).toBe(1);
        expect(inactiveCount).toBe(1);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush(mockReminders);
    });

    it('should construct correct API URL', (done) => {
      service.getAllReminders().subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/waterReminder/getAll`);
      expect(req.request.url).toBe(`${environment.baseUrl}/waterReminder/getAll`);
      req.flush([]);
    });
  });
});

