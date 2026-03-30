import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = 'http://localhost:8086/StreetLeague/user';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.api}/profile`);
  }

  updateProfile(data: UpdateProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.api}/profile`, data);
  }

  changePassword(data: ChangePasswordRequest): Observable<string> {
    return this.http.put(`${this.api}/change-password`, data, { responseType: 'text' });
  }

  deleteAccount(): Observable<string> {
  return this.http.delete(`${this.api}/profile`, { responseType: 'text' });
}
}