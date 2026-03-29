import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserProfile } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';
import { TeamService } from 'src/app/services/team.service';
import { TrainingService } from 'src/app/services/training.service';
import { Team } from 'src/app/models/team.model';
import { TrainingResponse } from 'src/app/models/training.model';

@Component({
  selector: 'app-player-profile',
  templateUrl: './player-profile.component.html',
  styleUrls: ['./player-profile.component.css']
})
export class PlayerProfileComponent implements OnInit {

  profile: UserProfile | null = null;
  profileForm!:  FormGroup;
  passwordForm!: FormGroup;

  myTeams:     Team[]             = [];
  myTrainings: TrainingResponse[] = [];

  profileSuccess  = '';
  profileError    = '';
  passwordSuccess = '';
  passwordError   = '';
  activeTab: 'info' | 'password' | 'stats' = 'info';

  showDeleteModal = false;
  deleteError     = '';

  constructor(
    private fb:              FormBuilder,
    private userService:     UserService,
    private teamService:     TeamService,
    private trainingService: TrainingService,
    private router:          Router
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword:     ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });

    this.userService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm.patchValue({ fullName: data.fullName });
      }
    });

    this.loadMyTeams();
    this.loadMyTrainings();
  }

  // ── Load ──────────────────────────────────────────────────
  loadMyTeams(): void {
    this.teamService.getTeamsByCoach().subscribe({
      next: (data) => this.myTeams = data,
      error: ()     => {}
    });
  }

  loadMyTrainings(): void {
    this.trainingService.getTrainingsByCoach().subscribe({
      next: (data) => this.myTrainings = data,
      error: ()     => {}
    });
  }

  // ── Delete Team ───────────────────────────────────────────
  deleteTeam(idTeam: number): void {
    if (!confirm('Delete this team permanently?')) return;
    const email = localStorage.getItem('EmailUserConnect')!;
    this.teamService.deleteTeam(idTeam, email).subscribe({
      next: () => {
        this.myTeams = this.myTeams.filter(t => t.idTeam !== idTeam);
      },
      error: () => alert('Failed to delete team.')
    });
  }

  // ── Delete Training ───────────────────────────────────────
  deleteTraining(idTraining: number): void {
    if (!confirm('Delete this training?')) return;
    this.trainingService.deleteTraining(idTraining).subscribe({
      next: () => {
        this.myTrainings = this.myTrainings.filter(t => t.idTraining !== idTraining);
      },
      error: () => alert('Failed to delete training.')
    });
  }

  // ── Profile ───────────────────────────────────────────────
  onUpdateProfile(): void {
    if (this.profileForm.invalid) return;
    this.profileSuccess = '';
    this.profileError   = '';
    this.userService.updateProfile(this.profileForm.value).subscribe({
      next: (data) => {
        this.profile = data;
        localStorage.setItem('userName', data.fullName);
        this.profileSuccess = 'Profile updated successfully!';
        setTimeout(() => this.profileSuccess = '', 3000);
      },
      error: () => { this.profileError = 'Failed to update profile.'; }
    });
  }

  // ── Password ──────────────────────────────────────────────
  passwordMatchValidator(group: AbstractControl) {
    const np = group.get('newPassword')?.value;
    const cp = group.get('confirmPassword')?.value;
    return np === cp ? null : { mismatch: true };
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;
    this.passwordSuccess = '';
    this.passwordError   = '';
    const { currentPassword, newPassword } = this.passwordForm.value;
    this.userService.changePassword({ currentPassword, newPassword }).subscribe({
      next: () => {
        this.passwordSuccess = 'Password changed successfully!';
        this.passwordForm.reset();
        setTimeout(() => this.passwordSuccess = '', 3000);
      },
      error: (err) => { this.passwordError = err.error || 'Current password is incorrect.'; }
    });
  }

  // ── Delete Account ────────────────────────────────────────
  openDeleteModal():  void { this.showDeleteModal = true; }
  closeDeleteModal(): void { this.showDeleteModal = false; this.deleteError = ''; }

  confirmDelete(): void {
    this.userService.deleteAccount().subscribe({
      next: () => { localStorage.clear(); this.router.navigate(['/login']); },
      error: ()  => { this.deleteError = 'Failed to delete account.'; }
    });
  }

  // ── Helpers ───────────────────────────────────────────────
  getRoleBadgeClass(): string {
    const classes: Record<string, string> = {
      COACH: 'bg-success', ADMIN: 'bg-danger', PLAYER: 'bg-primary'
    };
    return classes[this.profile?.role ?? ''] ?? 'bg-dark';
  }

  getTabIndicatorLeft(): string {
    const idx = ['info', 'password', 'stats'].indexOf(this.activeTab);
    return `${idx * 33.33}%`;
  }

  clampStat(val: number, max: number): number {
    if (!val || val <= 0) return 4;
    return Math.min(Math.max((val / max) * 100, 4), 100);
  }

  getPasswordStrength(): number {
    const pw: string = this.passwordForm.get('newPassword')?.value ?? '';
    if (!pw) return 0;
    let score = 0;
    if (pw.length >= 6)           score++;
    if (pw.length >= 10)          score++;
    if (/[A-Z]/.test(pw))        score++;
    if (/[0-9]/.test(pw))        score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;
    return score;
  }

  getStrengthLabel(): string {
    const s = this.getPasswordStrength();
    if (s <= 1) return 'Weak';
    if (s <= 2) return 'Fair';
    if (s <= 3) return 'Good';
    return 'Strong';
  }

  getStrengthClass(): string {
    const s = this.getPasswordStrength();
    if (s <= 1) return 'weak';
    if (s <= 2) return 'fair';
    if (s <= 3) return 'good';
    return 'strong';
  }

  getStrengthWidth(): string {
    return `${(this.getPasswordStrength() / 5) * 100}%`;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PLANNED':   return 'badge-blue';
      case 'COMPLETED': return 'badge-green';
      case 'CANCELLED': return 'badge-red';
      default:          return 'badge-gray';
    }
  }
}