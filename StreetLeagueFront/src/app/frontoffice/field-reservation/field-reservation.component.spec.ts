import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FieldReservationComponent } from './field-reservation.component';
import { FieldReservationService } from '../../services/field-reservation.service';
import { AuthService } from 'src/app/services/auth.service';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FieldReservation, ReservationStatus } from '../../models/field-reservation.model';

describe('Frontoffice FieldReservationComponent', () => {
  let component: FieldReservationComponent;
  let fixture: ComponentFixture<FieldReservationComponent>;
  let svcSpy:  jasmine.SpyObj<FieldReservationService>;
  let authSpy: jasmine.SpyObj<AuthService>;

  const mockFields = [
    { id: 1, name: 'Field A', location: 'North', sportType: 'FOOTBALL', available: true,  pricePerHour: 60 },
    { id: 2, name: 'Court B', location: 'South', sportType: 'TENNIS',   available: false, pricePerHour: 40 }
  ];

  const mockReservations = [
    { id: 10, fieldId: 1, status: ReservationStatus.APPROVED,
      startTime: '2026-05-01T10:00:00', endTime: '2026-05-01T12:00:00',
      createdAt: new Date().toISOString() },
    { id: 11, fieldId: 2, status: ReservationStatus.PENDING,
      startTime: '2025-01-01T09:00:00', endTime: '2025-01-01T10:00:00',
      createdAt: new Date(2025, 0, 1).toISOString() }
  ];

  beforeEach(async () => {
    svcSpy  = jasmine.createSpyObj('FieldReservationService', [
      'getAllFields', 'getAllReservations', 'getReservationsByPlayer',
      'createReservation', 'cancelReservation'
    ]);
    authSpy = jasmine.createSpyObj('AuthService', ['getUserId']);

    svcSpy.getAllFields.and.returnValue(of(mockFields as any));
    svcSpy.getAllReservations.and.returnValue(of(mockReservations as any));
    svcSpy.getReservationsByPlayer.and.returnValue(of([mockReservations[0]] as any));
    authSpy.getUserId.and.returnValue(42);

    await TestBed.configureTestingModule({
      declarations: [FieldReservationComponent],
      imports: [ReactiveFormsModule, FormsModule],
      providers: [
        { provide: FieldReservationService, useValue: svcSpy  },
        { provide: AuthService,             useValue: authSpy },
        FormBuilder
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    TestBed.overrideComponent(FieldReservationComponent, {
      set: { template: '<div></div>' }
    });

    fixture   = TestBed.createComponent(FieldReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load fields on init', () => {
    expect(svcSpy.getAllFields).toHaveBeenCalled();
    expect(component.allFields.length).toBe(2);
  });

  it('should load all reservations on init', () => {
    expect(svcSpy.getAllReservations).toHaveBeenCalled();
    expect(component.allReservations.length).toBe(2);
  });

  it('should load my reservations on init', () => {
    expect(svcSpy.getReservationsByPlayer).toHaveBeenCalledWith(42);
    expect(component.myReservations.length).toBe(1);
  });

  it('should build bookingForm on init', () => {
    expect(component.bookingForm).toBeTruthy();
  });

  it('filteredFields should equal allFields after init', () => {
    expect(component.filteredFields.length).toBe(2);
  });

  // ── loadFields error ──────────────────────────────────────

  it('loadFieldsTest — should show toast on error', () => {
    svcSpy.getAllFields.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.loadFields();
    expect(toastSpy).toHaveBeenCalledWith('Failed to load fields', 'error');
    expect(component.isLoading).toBeFalse();
  });

  // ── loadMyReservations ────────────────────────────────────

  it('loadMyReservationsTest — should not call service when no player id', () => {
    authSpy.getUserId.and.returnValue(null);
    svcSpy.getReservationsByPlayer.calls.reset();
    component.loadMyReservations();
    expect(svcSpy.getReservationsByPlayer).not.toHaveBeenCalled();
  });

  // ── Stats getters ─────────────────────────────────────────

  it('totalFields — should return total number of fields', () => {
    expect(component.totalFields).toBe(2);
  });

  it('availableNow — should count only available fields', () => {
    expect(component.availableNow).toBe(1);
  });

  it('monthlyReservations — should count only this-month reservations', () => {
    expect(component.monthlyReservations).toBe(1);
  });

  it('averagePrice — should return rounded average pricePerHour', () => {
    expect(component.averagePrice).toBe(50);
  });

  it('averagePrice — should return 0 when no fields', () => {
    component.allFields = [];
    expect(component.averagePrice).toBe(0);
  });

  // ── applyFilters ──────────────────────────────────────────

  it('applyFiltersTest — should filter fields by name', () => {
    component.searchQuery = 'field';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].name).toBe('Field A');
  });

  it('applyFiltersTest — should filter fields by location', () => {
    component.searchQuery = 'south';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].location).toBe('South');
  });

  it('applyFiltersTest — should filter by sport type', () => {
    component.selectedSport = 'TENNIS';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].sportType).toBe('TENNIS');
  });

  it('applyFiltersTest — should filter available fields only', () => {
    component.selectedAvail = 'available';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].available).toBeTrue();
  });

  it('applyFiltersTest — should filter unavailable fields only', () => {
    component.selectedAvail = 'unavailable';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].available).toBeFalse();
  });

  it('applyFiltersTest — should return all when no filter is applied', () => {
    component.applyFilters();
    expect(component.filteredFields.length).toBe(2);
  });

  it('applyFiltersTest — should return empty when no match', () => {
    component.searchQuery = 'zzzzz';
    component.applyFilters();
    expect(component.filteredFields.length).toBe(0);
  });

  // ── clearFilters ──────────────────────────────────────────

  it('clearFiltersTest — should reset all filters and show all fields', () => {
    component.searchQuery   = 'field';
    component.selectedSport = 'TENNIS';
    component.selectedAvail = 'available';
    component.clearFilters();
    expect(component.searchQuery).toBe('');
    expect(component.selectedSport).toBe('');
    expect(component.selectedAvail).toBe('');
    expect(component.filteredFields.length).toBe(2);
  });

  // ── openBookingModal ──────────────────────────────────────

  it('openBookingModalTest — should set selectedField and open modal', () => {
    component.openBookingModal(mockFields[0] as any);
    expect(component.selectedField).toBe(mockFields[0] as any);
    expect(component.showBookingModal).toBeTrue();
  });

  it('openBookingModalTest — should reset form when opening modal', () => {
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    component.openBookingModal(mockFields[0] as any);
    expect(component.bookingForm.value.date).toBeNull();
  });

  // ── submitBooking ─────────────────────────────────────────

  it('submitBookingTest — should do nothing when form is invalid', () => {
    component.bookingForm.reset();
    component.submitBooking();
    expect(svcSpy.createReservation).not.toHaveBeenCalled();
  });

  it('submitBookingTest — should show toast when no player id', () => {
    authSpy.getUserId.and.returnValue(null);
    const toastSpy = spyOn<any>(component, 'toast');
    component.selectedField = mockFields[0] as any;
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    component.submitBooking();
    expect(toastSpy).toHaveBeenCalledWith('Please log in to make a reservation', 'error');
    expect(svcSpy.createReservation).not.toHaveBeenCalled();
  });

  it('submitBookingTest — should create reservation and close modal on success', () => {
    svcSpy.createReservation.and.returnValue(of({} as any));
    component.selectedField = mockFields[0] as any;
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    component.submitBooking();
    expect(svcSpy.createReservation).toHaveBeenCalled();
    expect(component.showBookingModal).toBeFalse();
  });

  it('submitBookingTest — should show error toast on failure', () => {
    svcSpy.createReservation.and.returnValue(throwError(() => ({
      error: { message: 'Slot already taken' }
    })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.selectedField = mockFields[0] as any;
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    component.submitBooking();
    expect(toastSpy).toHaveBeenCalledWith('Slot already taken', 'error');
  });

  it('submitBookingTest — should show fallback message when no error message', () => {
    svcSpy.createReservation.and.returnValue(throwError(() => ({ error: {} })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.selectedField = mockFields[0] as any;
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    component.submitBooking();
    expect(toastSpy).toHaveBeenCalledWith('Booking failed. Slot may be unavailable.', 'error');
  });

  // ── cancelMyReservation ───────────────────────────────────

  it('cancelMyReservationTest — should do nothing when no player id', () => {
    authSpy.getUserId.and.returnValue(null);
    component.cancelMyReservation(10);
    expect(svcSpy.cancelReservation).not.toHaveBeenCalled();
  });

  it('cancelMyReservationTest — should not cancel when confirm declined', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.cancelMyReservation(10);
    expect(svcSpy.cancelReservation).not.toHaveBeenCalled();
  });

  it('cancelMyReservationTest — should cancel and reload my reservations on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancelReservation.and.returnValue(of({} as FieldReservation));
    component.cancelMyReservation(10);
    expect(svcSpy.cancelReservation).toHaveBeenCalledWith(10, 42);
    expect(svcSpy.getReservationsByPlayer).toHaveBeenCalled();
  });

  it('cancelMyReservationTest — should show toast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancelReservation.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.cancelMyReservation(10);
    expect(toastSpy).toHaveBeenCalledWith('Cancellation failed', 'error');
  });

  // ── statusClass ───────────────────────────────────────────

  it('statusClassTest — APPROVED returns badge-approved', () => {
    expect(component.statusClass(ReservationStatus.APPROVED)).toBe('badge-approved');
  });

  it('statusClassTest — REJECTED returns badge-rejected', () => {
    expect(component.statusClass(ReservationStatus.REJECTED)).toBe('badge-rejected');
  });

  it('statusClassTest — CANCELLED returns badge-cancelled', () => {
    expect(component.statusClass(ReservationStatus.CANCELLED)).toBe('badge-cancelled');
  });

  it('statusClassTest — PENDING returns badge-pending', () => {
    expect(component.statusClass(ReservationStatus.PENDING)).toBe('badge-pending');
  });

  it('statusClassTest — undefined returns badge-pending', () => {
    expect(component.statusClass(undefined)).toBe('badge-pending');
  });

  // ── canCancel ─────────────────────────────────────────────

  it('canCancelTest — PENDING returns true', () => {
    expect(component.canCancel(ReservationStatus.PENDING)).toBeTrue();
  });

  it('canCancelTest — APPROVED returns true', () => {
    expect(component.canCancel(ReservationStatus.APPROVED)).toBeTrue();
  });

  it('canCancelTest — REJECTED returns false', () => {
    expect(component.canCancel(ReservationStatus.REJECTED)).toBeFalse();
  });

  it('canCancelTest — CANCELLED returns false', () => {
    expect(component.canCancel(ReservationStatus.CANCELLED)).toBeFalse();
  });

  // ── getEmoji ──────────────────────────────────────────────

  it('getEmojiTest — FOOTBALL returns ⚽', () => {
    expect(component.getEmoji('FOOTBALL')).toBe('⚽');
  });

  it('getEmojiTest — BASKETBALL returns 🏀', () => {
    expect(component.getEmoji('BASKETBALL')).toBe('🏀');
  });

  it('getEmojiTest — unknown sport returns default 🏟️', () => {
    expect(component.getEmoji('UNKNOWN')).toBe('🏟️');
  });

  // ── formatDate ────────────────────────────────────────────

  it('formatDateTest — should return — for undefined', () => {
    expect(component.formatDate(undefined)).toBe('—');
  });

  it('formatDateTest — should format a valid date string', () => {
    const result = component.formatDate('2026-05-01T00:00:00');
    expect(result).toContain('2026');
  });

  // ── estimatedPrice ────────────────────────────────────────

  it('estimatedPriceTest — should return 0 when no field selected', () => {
    component.selectedField = null;
    expect(component.estimatedPrice()).toBe(0);
  });

  it('estimatedPriceTest — should return 0 when end time is before start', () => {
    component.selectedField = mockFields[0] as any;
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '12', endHour: '10' });
    expect(component.estimatedPrice()).toBe(0);
  });

  it('estimatedPriceTest — should calculate price correctly for 2 hours', () => {
    component.selectedField = mockFields[0] as any; // pricePerHour: 60
    component.bookingForm.setValue({ date: '2026-06-01', startHour: '10', endHour: '12' });
    expect(component.estimatedPrice()).toBe(120); // 2h × 60
  });

  // ── todayDate ─────────────────────────────────────────────

  it('todayDateTest — should return today in yyyy-mm-dd format', () => {
    const today = new Date().toISOString().split('T')[0];
    expect(component.todayDate()).toBe(today);
  });

  // ── closeModals ───────────────────────────────────────────

  it('closeModalsTest — should reset all modal states', () => {
    component.showBookingModal = true;
    component.showMyBookings   = true;
    component.selectedField    = mockFields[0] as any;
    component.closeModals();
    expect(component.showBookingModal).toBeFalse();
    expect(component.showMyBookings).toBeFalse();
    expect(component.selectedField).toBeNull();
  });
});