import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FieldReservationComponent } from './field-reservation.component';
import { FieldReservationService } from '../../services/field-reservation.service';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FieldReservation, ReservationStatus, SportType } from '../../models/field-reservation.model';
import { AuthService } from 'src/app/services/auth.service';

describe('Backoffice FieldReservationComponent', () => {
  let component: FieldReservationComponent;
  let fixture: ComponentFixture<FieldReservationComponent>;
  let svcSpy: jasmine.SpyObj<FieldReservationService>;

  const mockFields = [
    { id: 1, name: 'Field A', location: 'North', sportType: 'FOOTBALL', available: true,  pricePerHour: 60, capacity: 11 },
    { id: 2, name: 'Court B', location: 'South', sportType: 'TENNIS',   available: false, pricePerHour: 30, capacity: 2  }
  ];

  const mockPending = [
    { id: 10, fieldId: 1, status: ReservationStatus.PENDING,
      startTime: '2026-05-01T10:00:00', endTime: '2026-05-01T12:00:00',
      createdAt: new Date().toISOString(), totalPrice: 100 }
  ];

  const mockAllReservations = [
    { id: 10, fieldId: 1, status: ReservationStatus.APPROVED,
      startTime: '2026-05-01T10:00:00', endTime: '2026-05-01T12:00:00',
      createdAt: new Date().toISOString(), totalPrice: 100 },
    { id: 11, fieldId: 2, status: ReservationStatus.REJECTED,
      startTime: '2025-01-01T10:00:00', endTime: '2025-01-01T11:00:00',
      createdAt: new Date(2025, 0, 1).toISOString(), totalPrice: 0 }
  ];

  beforeEach(async () => {
    svcSpy = jasmine.createSpyObj('FieldReservationService', [
      'getPendingReservations', 'getAllReservations', 'getAllFields',
      'approveReservation', 'rejectReservation', 'deleteReservation',
      'createField', 'updateField', 'toggleAvailability', 'deleteField'
    ]);

    const authSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser', 'logout']);

    svcSpy.getPendingReservations.and.returnValue(of(mockPending as any));
    svcSpy.getAllReservations.and.returnValue(of(mockAllReservations as any));
    svcSpy.getAllFields.and.returnValue(of(mockFields as any));

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
    expect(component.fields.length).toBe(2);
  });

  it('should load pending reservations on init', () => {
    expect(svcSpy.getPendingReservations).toHaveBeenCalled();
    expect(component.pendingReservations.length).toBe(1);
  });

  it('should load all reservations on init', () => {
    expect(svcSpy.getAllReservations).toHaveBeenCalled();
    expect(component.allReservations.length).toBe(2);
  });

  it('should build fieldForm on init', () => {
    expect(component.fieldForm).toBeTruthy();
  });

  it('filteredFields should equal fields after load', () => {
    expect(component.filteredFields.length).toBe(2);
  });

  // ── loadPendingReservations error ─────────────────────────

  it('should show toast when loadPendingReservations fails', () => {
    svcSpy.getPendingReservations.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.loadPendingReservations();
    expect(toastSpy).toHaveBeenCalledWith('Failed to load pending reservations', 'error');
    expect(component.isLoading).toBeFalse();
  });

  // ── loadFields error ──────────────────────────────────────

  it('should show toast when loadFields fails', () => {
    svcSpy.getAllFields.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.loadFields();
    expect(toastSpy).toHaveBeenCalledWith('Failed to load fields', 'error');
  });

  // ── Stats getters ─────────────────────────────────────────

  it('availableFieldsCount — should count available fields', () => {
    expect(component.availableFieldsCount).toBe(1);
  });

  it('pendingCount — should return pending reservations count', () => {
    expect(component.pendingCount).toBe(1);
  });

  it('monthlyReservations — should count this-month reservations only', () => {
    expect(component.monthlyReservations).toBe(1);
  });

  it('monthlyRevenue — should sum APPROVED reservations this month', () => {
    expect(component.monthlyRevenue).toBe(100);
  });

  // ── applySearch ───────────────────────────────────────────

  it('applySearchTest — should filter fields by name', () => {
    component.searchQuery = 'field';
    component.applySearch();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].name).toBe('Field A');
  });

  it('applySearchTest — should filter fields by location', () => {
    component.searchQuery = 'south';
    component.applySearch();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].location).toBe('South');
  });

  it('applySearchTest — should filter fields by sportType', () => {
    component.searchQuery = 'tennis';
    component.applySearch();
    expect(component.filteredFields.length).toBe(1);
    expect(component.filteredFields[0].sportType).toBe('TENNIS');
  });

  it('applySearchTest — should return all when query is empty', () => {
    component.searchQuery = '';
    component.applySearch();
    expect(component.filteredFields.length).toBe(2);
  });

  it('applySearchTest — should return empty when no match', () => {
    component.searchQuery = 'zzzzz';
    component.applySearch();
    expect(component.filteredFields.length).toBe(0);
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

  // ── capacityLabel ─────────────────────────────────────────

  it('capacityLabelTest — should return NvN format', () => {
    expect(component.capacityLabel(5)).toBe('5v5');
    expect(component.capacityLabel(11)).toBe('11v11');
  });

  // ── formatDate ────────────────────────────────────────────

  it('formatDateTest — should return — for undefined', () => {
    expect(component.formatDate(undefined)).toBe('—');
  });

  it('formatDateTest — should format a valid date string', () => {
    const result = component.formatDate('2026-05-01T00:00:00');
    expect(result).toContain('2026');
  });

  // ── approve ───────────────────────────────────────────────

  it('approveTest — should call approveReservation and reload on success', () => {
    svcSpy.approveReservation.and.returnValue(of({} as FieldReservation));
    const loadSpy = spyOn(component, 'loadAll');
    component.approve(10);
    expect(svcSpy.approveReservation).toHaveBeenCalledWith(10);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('approveTest — should show toast error on failure', () => {
    svcSpy.approveReservation.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.approve(10);
    expect(toastSpy).toHaveBeenCalledWith('Approval failed', 'error');
  });

  // ── openRejectModal ───────────────────────────────────────

  it('openRejectModalTest — should set state and open modal', () => {
    component.openRejectModal(10);
    expect(component.rejectTargetId).toBe(10);
    expect(component.adminNoteInput).toBe('');
    expect(component.showRejectNoteModal).toBeTrue();
  });

  // ── confirmReject ─────────────────────────────────────────

  it('confirmRejectTest — should do nothing when rejectTargetId is null', () => {
    component.rejectTargetId = null;
    component.confirmReject();
    expect(svcSpy.rejectReservation).not.toHaveBeenCalled();
  });

  it('confirmRejectTest — should reject and reload on success', () => {
    svcSpy.rejectReservation.and.returnValue(of({} as FieldReservation));
    const loadSpy = spyOn(component, 'loadAll');
    component.rejectTargetId = 10;
    component.confirmReject();
    expect(svcSpy.rejectReservation).toHaveBeenCalled();
    expect(component.showRejectNoteModal).toBeFalse();
    expect(loadSpy).toHaveBeenCalled();
  });

  it('confirmRejectTest — should show toast error on failure', () => {
    svcSpy.rejectReservation.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.rejectTargetId = 10;
    component.confirmReject();
    expect(toastSpy).toHaveBeenCalledWith('Rejection failed', 'error');
  });

  // ── viewReservationDetail ─────────────────────────────────

  it('viewReservationDetailTest — should set selectedReservation and open modal', () => {
    const r = mockPending[0] as any;
    component.viewReservationDetail(r);
    expect(component.selectedReservation).toBe(r);
    expect(component.showDetailModal).toBeTrue();
  });

  // ── deleteReservation ─────────────────────────────────────

  it('deleteReservationTest — should delete on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.deleteReservation.and.returnValue(of(void 0) as any);
    const loadSpy = spyOn(component, 'loadAll');
    component.deleteReservation(10);
    expect(svcSpy.deleteReservation).toHaveBeenCalledWith(10);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('deleteReservationTest — should not delete when confirm cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteReservation(10);
    expect(svcSpy.deleteReservation).not.toHaveBeenCalled();
  });

  it('deleteReservationTest — should show toast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.deleteReservation.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.deleteReservation(10);
    expect(toastSpy).toHaveBeenCalledWith('Deletion failed', 'error');
  });

  // ── openAddFieldModal ─────────────────────────────────────

  it('openAddFieldModalTest — should reset form and open modal', () => {
    component.openAddFieldModal();
    expect(component.showAddFieldModal).toBeTrue();
    expect(component.fieldForm.value.available).toBeTrue();
    expect(component.fieldForm.value.sportType).toBe(SportType.FOOTBALL);
  });

  // ── openEditFieldModal ────────────────────────────────────

  it('openEditFieldModalTest — should patch form with field data and open modal', () => {
    const field = mockFields[0] as any;
    component.openEditFieldModal(field);
    expect(component.selectedField).toBe(field);
    expect(component.showEditFieldModal).toBeTrue();
    expect(component.fieldForm.value.name).toBe('Field A');
  });

  // ── saveNewField ──────────────────────────────────────────

  it('saveNewFieldTest — should do nothing when form is invalid', () => {
    component.fieldForm.reset();
    component.saveNewField();
    expect(svcSpy.createField).not.toHaveBeenCalled();
  });

  it('saveNewFieldTest — should create field and reload on success', () => {
    svcSpy.createField.and.returnValue(of(mockFields[0] as any));
    const loadSpy = spyOn(component, 'loadFields');
    component.fieldForm.setValue({
      name: 'New Field', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
    });
    component.saveNewField();
    expect(svcSpy.createField).toHaveBeenCalled();
    expect(component.showAddFieldModal).toBeFalse();
    expect(loadSpy).toHaveBeenCalled();
  });

  it('saveNewFieldTest — should show validation errors on 400 with object error', () => {
    svcSpy.createField.and.returnValue(throwError(() => ({
      status: 400, error: { name: 'too short' }
    })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.fieldForm.setValue({
      name: 'New Field', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
    });
    component.saveNewField();
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('Validation errors'), 'error');
  });

<<<<<<< HEAD
  // ✅ FIX SAFE
  get availableFieldsCount(): number {
    return (this.fields ?? []).filter(f => f.available === true).length;
  }

  get pendingCount(): number {
    return this.pendingReservations.length;
  }

  get monthlyReservations(): number {
    const now = new Date();
    return this.allReservations.filter(r => {
      const d = new Date(r.createdAt || '');
      return d.getMonth() === now.getMonth() &&
             d.getFullYear() === now.getFullYear();
    }).length;
  }

  get monthlyRevenue(): number {
    const now = new Date();
    return this.allReservations
      .filter(r => {
        const d = new Date(r.createdAt || '');
        return d.getMonth() === now.getMonth() &&
               d.getFullYear() === now.getFullYear() &&
               r.statut === ReservationStatus.APPROVED;
      })
      .reduce((sum, r) => sum + (r.totalPrice ?? 0), 0);
  }

  applySearch(): void {
    const q = this.searchQuery.toLowerCase().trim();
    this.filteredFields = q
      ? this.fields.filter(f =>
          f.nom.toLowerCase().includes(q) ||
          f.lieu.toLowerCase().includes(q) ||
          f.sportType.toLowerCase().includes(q)
        )
      : [...this.fields];
  }

  private buildFieldForm(): void {
    this.fieldForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      sportType: [SportType.FOOTBALL, Validators.required],
      lieu: ['', Validators.required],
      imageUrl: [''],
      pricePerHour: [null, [Validators.required, Validators.min(0)]],
      capacity: [null, [Validators.required, Validators.min(1)]],
      available: [true]
=======
  it('saveNewFieldTest — should show message error on other errors', () => {
    svcSpy.createField.and.returnValue(throwError(() => ({
      status: 409, error: { message: 'Field already exists' }
    })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.fieldForm.setValue({
      name: 'New Field', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
    });
    component.saveNewField();
    expect(toastSpy).toHaveBeenCalledWith('Field already exists', 'error');
  });

<<<<<<< HEAD
  private toast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}

=======
  // ── saveEditedField ───────────────────────────────────────

  it('saveEditedFieldTest — should do nothing when form is invalid', () => {
    component.fieldForm.reset();
    component.saveEditedField();
    expect(svcSpy.updateField).not.toHaveBeenCalled();
  });

  it('saveEditedFieldTest — should do nothing when selectedField has no id', () => {
    component.selectedField = { ...mockFields[0], id: undefined } as any;
    component.fieldForm.setValue({
      name: 'Updated', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
    });
    component.saveEditedField();
    expect(svcSpy.updateField).not.toHaveBeenCalled();
  });

  it('saveEditedFieldTest — should update field on success', () => {
    svcSpy.updateField.and.returnValue(of(mockFields[0] as any));
    const loadSpy = spyOn(component, 'loadFields');
    component.selectedField = mockFields[0] as any;
    component.fieldForm.setValue({
      name: 'Updated', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
    });
    component.saveEditedField();
    expect(svcSpy.updateField).toHaveBeenCalledWith(1, jasmine.any(Object));
    expect(component.showEditFieldModal).toBeFalse();
    expect(loadSpy).toHaveBeenCalled();
  });

  it('saveEditedFieldTest — should show validation errors on 400', () => {
    svcSpy.updateField.and.returnValue(throwError(() => ({
      status: 400, error: { location: 'required' }
    })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.selectedField = mockFields[0] as any;
    component.fieldForm.setValue({
      name: 'Updated', description: '', sportType: 'FOOTBALL',
      location: 'East', imageUrl: '', pricePerHour: 40, capacity: 5, available: true
    });
    component.saveEditedField();
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('Validation errors'), 'error');
  });

  // ── toggleField ───────────────────────────────────────────

  it('toggleFieldTest — should do nothing when field has no id', () => {
    component.toggleField({ ...mockFields[0], id: undefined } as any);
    expect(svcSpy.toggleAvailability).not.toHaveBeenCalled();
  });

  it('toggleFieldTest — should toggle availability and update fields list', () => {
    const updated = { ...mockFields[0], available: false } as any;
    svcSpy.toggleAvailability.and.returnValue(of(updated));
    component.toggleField(mockFields[0] as any);
    expect(svcSpy.toggleAvailability).toHaveBeenCalledWith(1);
    expect(component.fields[0].available).toBeFalse();
  });

  it('toggleFieldTest — should show toast on error', () => {
    svcSpy.toggleAvailability.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.toggleField(mockFields[0] as any);
    expect(toastSpy).toHaveBeenCalledWith('Toggle failed', 'error');
  });

  // ── deleteField ───────────────────────────────────────────

  it('deleteFieldTest — should delete field on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.deleteField.and.returnValue(of({} as any));
    const loadSpy = spyOn(component, 'loadFields');
    component.deleteField(1);
    expect(svcSpy.deleteField).toHaveBeenCalledWith(1);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('deleteFieldTest — should not delete when confirm cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteField(1);
    expect(svcSpy.deleteField).not.toHaveBeenCalled();
  });

  it('deleteFieldTest — should show toast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.deleteField.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'toast');
    component.deleteField(1);
    expect(toastSpy).toHaveBeenCalledWith('Deletion failed', 'error');
  });

  // ── closeModals ───────────────────────────────────────────

  it('closeModalsTest — should reset all modal states', () => {
    component.showAddFieldModal   = true;
    component.showEditFieldModal  = true;
    component.showDetailModal     = true;
    component.showRejectNoteModal = true;
    component.selectedReservation = mockPending[0] as any;
    component.selectedField       = mockFields[0] as any;

    component.closeModals();

    expect(component.showAddFieldModal).toBeFalse();
    expect(component.showEditFieldModal).toBeFalse();
    expect(component.showDetailModal).toBeFalse();
    expect(component.showRejectNoteModal).toBeFalse();
    expect(component.selectedReservation).toBeNull();
    expect(component.selectedField).toBeNull();
  });
});
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
