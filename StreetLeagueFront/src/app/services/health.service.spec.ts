import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HealthService, WaterReminderDTO } from './health.service';

describe('HealthService', () => {
  let service: HealthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HealthService]
    });
    service = TestBed.inject(HealthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('addReminder', () => {
    it('should add a new water reminder', (done) => {
      const reminderData: WaterReminderDTO = {
        frequency: 60,
        quantity: 250,
        active: true,
        userId: 1
      };
      const mockResponse = { id: 1, ...reminderData };

      service.addReminder(reminderData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.id).toBe(1);
        done();
      });

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/add');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(reminderData);
      req.flush(mockResponse);
    });

    it('should send correct frequency and quantity', (done) => {
      const reminderData: WaterReminderDTO = {
        frequency: 30,
        quantity: 500,
        active: true
      };

      service.addReminder(reminderData).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/add');
      expect(req.request.body.frequency).toBe(30);
      expect(req.request.body.quantity).toBe(500);
      expect(req.request.body.active).toBe(true);
      req.flush({ id: 1 });
    });

    it('should handle error when adding reminder', (done) => {
      const reminderData: WaterReminderDTO = {
        frequency: 60,
        quantity: 250,
        active: true
      };

      service.addReminder(reminderData).subscribe(
        () => fail('should have failed with 400 error'),
        (error) => {
          expect(error.status).toBe(400);
          done();
        }
      );

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/add');
      req.flush('Invalid data', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('updateReminder', () => {
    it('should update an existing reminder', (done) => {
      const reminderId = 1;
      const updatedData: WaterReminderDTO = {
        frequency: 120,
        quantity: 300,
        active: false,
        userId: 1
      };
      const mockResponse = { id: reminderId, ...updatedData };

      service.updateReminder(reminderId, updatedData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`http://localhost:8080/waterReminder/update/${reminderId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedData);
      req.flush(mockResponse);
    });

    it('should handle error when updating non-existent reminder', (done) => {
      const reminderData: WaterReminderDTO = {
        frequency: 60,
        quantity: 250,
        active: true
      };

      service.updateReminder(999, reminderData).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/update/999');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getReminder', () => {
    it('should fetch a reminder by id', (done) => {
      const reminderId = 1;
      const mockReminder: WaterReminderDTO = {
        id: reminderId,
        frequency: 60,
        quantity: 250,
        active: true,
        userId: 1
      };

      service.getReminder(reminderId).subscribe((reminder) => {
        expect(reminder).toEqual(mockReminder);
        expect(reminder.id).toBe(reminderId);
        done();
      });

      const req = httpMock.expectOne(`http://localhost:8080/waterReminder/getById/${reminderId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReminder);
    });

    it('should handle error when reminder not found', (done) => {
      service.getReminder(999).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/getById/999');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should return correct reminder properties', (done) => {
      const reminderId = 5;
      const mockReminder: WaterReminderDTO = {
        id: reminderId,
        frequency: 45,
        quantity: 200,
        active: true,
        userId: 2
      };

      service.getReminder(reminderId).subscribe((reminder) => {
        expect(reminder.frequency).toBe(45);
        expect(reminder.quantity).toBe(200);
        expect(reminder.active).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`http://localhost:8080/waterReminder/getById/${reminderId}`);
      req.flush(mockReminder);
    });
  });

  describe('deleteReminder', () => {
    it('should delete a reminder by id', (done) => {
      const reminderId = 1;

      service.deleteReminder(reminderId).subscribe(() => {
        expect(true).toBeTruthy();
        done();
      });

      const req = httpMock.expectOne(`http://localhost:8080/waterReminder/delete/${reminderId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle error when deleting non-existent reminder', (done) => {
      service.deleteReminder(999).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne('http://localhost:8080/waterReminder/delete/999');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should send delete request to correct endpoint', (done) => {
      const reminderId = 5;

      service.deleteReminder(reminderId).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`http://localhost:8080/waterReminder/delete/${reminderId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });
});
