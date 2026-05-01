// src/app/services/schedule-refresh.service.ts
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ScheduleRefreshService {
  private _refresh$ = new Subject<void>();
  refresh$ = this._refresh$.asObservable();

  trigger(): void {
    this._refresh$.next();
  }
}