import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { UserService } from './user.service';
import { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../models/user.model';

describe('UserService', () => {
  let service:  UserService;
  let httpMock: HttpTestingController;

  const base = 'http://localhost:8086/StreetLeague/user';

  const mockProfile: UserProfile = {
      fullName: 'Alice Martin',
      email: 'alice@example.com',
      role: 'COACH',
      idUser: 0,
      teamCount: 0,
      matchCount: 0,
      trainingCount: 0
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:   [HttpClientTestingModule],
      providers: [UserService]
    });

    service  = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ── Creation ───────────────────────────────────────────────────────────────

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── getProfile ─────────────────────────────────────────────────────────────

  describe('getProfileTest', () => {
    it('should call GET /user/profile and return a UserProfile', () => {
      service.getProfile().subscribe(res => {
        expect(res).toEqual(mockProfile);
        expect(res.fullName).toBe('Alice Martin');
        expect(res.email).toBe('alice@example.com');
        expect(res.role).toBe('COACH');
      });

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProfile);
    });

    it('should propagate HTTP errors from getProfile', () => {
      let errorResponse: any;

      service.getProfile().subscribe({
        next:  ()    => fail('expected an error'),
        error: (err) => errorResponse = err
      });

      const req = httpMock.expectOne(`${base}/profile`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(errorResponse.status).toBe(401);
    });
  });

  // ── updateProfile ──────────────────────────────────────────────────────────

  describe('updateProfileTest', () => {
    it('should call PUT /user/profile with the request body and return updated UserProfile', () => {
      const requestData: UpdateProfileRequest = { fullName: 'Bob Smith' };
      const updatedProfile: UserProfile = { ...mockProfile, fullName: 'Bob Smith' };

      service.updateProfile(requestData).subscribe(res => {
        expect(res).toEqual(updatedProfile);
        expect(res.fullName).toBe('Bob Smith');
      });

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(requestData);
      req.flush(updatedProfile);
    });

    it('should send the correct fullName in the request body', () => {
      const requestData: UpdateProfileRequest = { fullName: 'Charlie Doe' };

      service.updateProfile(requestData).subscribe();

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.body.fullName).toBe('Charlie Doe');
      req.flush({ ...mockProfile, fullName: 'Charlie Doe' });
    });

    it('should propagate HTTP errors from updateProfile', () => {
      let errorResponse: any;

      service.updateProfile({ fullName: 'Test' }).subscribe({
        next:  ()    => fail('expected an error'),
        error: (err) => errorResponse = err
      });

      const req = httpMock.expectOne(`${base}/profile`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

      expect(errorResponse.status).toBe(403);
    });
  });

  // ── changePassword ─────────────────────────────────────────────────────────

  describe('changePasswordTest', () => {
    it('should call PUT /user/change-password with the request body', () => {
      const requestData: ChangePasswordRequest = {
        currentPassword: 'oldPass1',
        newPassword:     'newPass1'
      };

      service.changePassword(requestData).subscribe(res => {
        expect(res).toBe('Password changed successfully');
      });

      const req = httpMock.expectOne(`${base}/change-password`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(requestData);
      req.flush('Password changed successfully');
    });

    it('should use responseType text for changePassword', () => {
      const requestData: ChangePasswordRequest = {
        currentPassword: 'oldPass1',
        newPassword:     'newPass1'
      };

      service.changePassword(requestData).subscribe(res => {
        expect(typeof res).toBe('string');
      });

      const req = httpMock.expectOne(`${base}/change-password`);
      expect(req.request.responseType).toBe('text');
      req.flush('ok');
    });

    it('should send correct currentPassword and newPassword in body', () => {
      const requestData: ChangePasswordRequest = {
        currentPassword: 'secret',
        newPassword:     'newSecret'
      };

      service.changePassword(requestData).subscribe();

      const req = httpMock.expectOne(`${base}/change-password`);
      expect(req.request.body.currentPassword).toBe('secret');
      expect(req.request.body.newPassword).toBe('newSecret');
      req.flush('ok');
    });

    it('should propagate HTTP errors from changePassword', () => {
      let errorResponse: any;

      service.changePassword({ currentPassword: 'wrong', newPassword: 'new' }).subscribe({
        next:  ()    => fail('expected an error'),
        error: (err) => errorResponse = err
      });

      const req = httpMock.expectOne(`${base}/change-password`);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });

      expect(errorResponse.status).toBe(400);
    });
  });

  // ── deleteAccount ──────────────────────────────────────────────────────────

  describe('deleteAccountTest', () => {
    it('should call DELETE /user/profile and return a text response', () => {
      service.deleteAccount().subscribe(res => {
        expect(res).toBe('Account deleted successfully');
      });

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.method).toBe('DELETE');
      req.flush('Account deleted successfully');
    });

    it('should use responseType text for deleteAccount', () => {
      service.deleteAccount().subscribe(res => {
        expect(typeof res).toBe('string');
      });

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.responseType).toBe('text');
      req.flush('deleted');
    });

    it('should send no request body on deleteAccount', () => {
      service.deleteAccount().subscribe();

      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.body).toBeNull();
      req.flush('deleted');
    });

    it('should propagate HTTP errors from deleteAccount', () => {
      let errorResponse: any;

      service.deleteAccount().subscribe({
        next:  ()    => fail('expected an error'),
        error: (err) => errorResponse = err
      });

      const req = httpMock.expectOne(`${base}/profile`);
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });

      expect(errorResponse.status).toBe(404);
    });
  });

  // ── URL correctness ────────────────────────────────────────────────────────

  describe('URL correctness', () => {
    it('getProfile should hit exactly /user/profile', () => {
      service.getProfile().subscribe();
      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.url).toBe(`${base}/profile`);
      req.flush(mockProfile);
    });

    it('updateProfile should hit exactly /user/profile', () => {
      service.updateProfile({ fullName: 'X' }).subscribe();
      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.url).toBe(`${base}/profile`);
      req.flush(mockProfile);
    });

    it('changePassword should hit exactly /user/change-password', () => {
      service.changePassword({ currentPassword: 'a', newPassword: 'b' }).subscribe();
      const req = httpMock.expectOne(`${base}/change-password`);
      expect(req.request.url).toBe(`${base}/change-password`);
      req.flush('ok');
    });

    it('deleteAccount should hit exactly /user/profile', () => {
      service.deleteAccount().subscribe();
      const req = httpMock.expectOne(`${base}/profile`);
      expect(req.request.url).toBe(`${base}/profile`);
      req.flush('deleted');
    });
  });
});