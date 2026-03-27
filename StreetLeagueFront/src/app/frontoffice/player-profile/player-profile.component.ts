import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { UserProfile } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';
import { TeamService } from 'src/app/services/team.service';
import { MatchService } from 'src/app/services/match.service';
import { TrainingService } from 'src/app/services/training.service';

@Component({
  selector: 'app-player-profile',
  templateUrl: './player-profile.component.html',
  styleUrls: ['./player-profile.component.css']
})
export class PlayerProfileComponent implements OnInit {
  profile: UserProfile | null = null;
  profileForm!: FormGroup;
  passwordForm!: FormGroup;

  profileSuccess = '';
  profileError = '';
  passwordSuccess = '';
  passwordError = '';
  activeTab: 'info' | 'password' | 'stats' = 'info';
  showDeleteModal = false;
  deleteError = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private teamService: TeamService,
    private matchService: MatchService,
    private trainingService: TrainingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });

    this.loadData();
  }

  private loadData(): void {
    const email = localStorage.getItem('EmailUserConnect') ?? '';

    // Charger tout en parallèle
    forkJoin({
      profile:      this.userService.getProfile(),
      allTeams:     this.teamService.getAllTeams(),
      allMatches:   this.matchService.getAllMatchs(),
      allTrainings: this.trainingService.getAllTrainings()
    }).subscribe({
      next: ({ profile, allTeams, allMatches, allTrainings }) => {

        this.profileForm.patchValue({ fullName: profile.fullName });

        // Debug — enlève après test
        console.log('Email connecté:', email);
        console.log('Team exemple:', allTeams[0]);
        console.log('Match exemple:', allMatches[0]);
        console.log('Training exemple:', allTrainings[0]);

        // ── TEAMS : je suis capitaine OU membre ──
        const myTeams = allTeams.filter(t =>
          t.captainEmail === email ||
          t.captainFullName === profile.fullName
        );

        // ── MATCHES : je suis capitaine d'une équipe impliquée ──
        const myTeamNames = new Set(myTeams.map(t => t.name));
        const myMatches = allMatches.filter(m =>
          myTeamNames.has(m.teamAName) ||
          myTeamNames.has(m.teamBName) ||
          m.captainAEmail === email    ||
          m.captainBEmail === email
        );

        // ── TRAININGS : liés à mes équipes ──
        const myTrainings = allTrainings.filter(t =>
          myTeamNames.has(t.teamName)
        );

        console.log('myTeams:', myTeams.length, myTeams);
        console.log('myMatches:', myMatches.length);
        console.log('myTrainings:', myTrainings.length);

        this.profile = {
          ...profile,
          teamCount:     myTeams.length,
          matchCount:    myMatches.length,
          trainingCount: myTrainings.length
        };
      },
      error: (err) => {
        console.error('Load failed:', err);
        // Fallback : charger juste le profil
        this.userService.getProfile().subscribe(p => {
          this.profile = p;
          this.profileForm.patchValue({ fullName: p.fullName });
        });
      }
    });
  }

  passwordMatchValidator(group: AbstractControl) {
    const np = group.get('newPassword')?.value;
    const cp = group.get('confirmPassword')?.value;
    return np === cp ? null : { mismatch: true };
  }

  onUpdateProfile(): void {
    if (this.profileForm.invalid || !this.profile) return;
    this.profileSuccess = '';
    this.profileError = '';
    const savedCounts = {
      teamCount:     this.profile.teamCount,
      matchCount:    this.profile.matchCount,
      trainingCount: this.profile.trainingCount
    };
    this.userService.updateProfile(this.profileForm.value).subscribe({
      next: (data) => {
        this.profile = { ...data, ...savedCounts };
        localStorage.setItem('userName', data.fullName);
        this.profileSuccess = 'Profile updated successfully!';
        setTimeout(() => this.profileSuccess = '', 3000);
      },
      error: () => { this.profileError = 'Failed to update profile.'; }
    });
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;
    this.passwordSuccess = '';
    this.passwordError = '';
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

  openDeleteModal():  void { this.showDeleteModal = true; }
  closeDeleteModal(): void { this.showDeleteModal = false; this.deleteError = ''; }

  confirmDelete(): void {
    this.userService.deleteAccount().subscribe({
      next: () => { localStorage.clear(); this.router.navigate(['/login']); },
      error: () => { this.deleteError = 'Failed to delete account.'; }
    });
  }

  getRoleBadgeClass(): string {
    const classes: Record<string, string> = {
      PLAYER: 'bg-primary', COACH: 'bg-success',
      ADMIN: 'bg-danger', SPONSOR: 'bg-warning', DELIVERY: 'bg-secondary'
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
    if (pw.length >= 6)  score++;
    if (pw.length >= 10) score++;
    if (/[A-Z]/.test(pw)) score++;
    if (/[0-9]/.test(pw)) score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;
    return score;
  }

  getStrengthLabel(): string {
    const s = this.getPasswordStrength();
    if (s <= 1) return 'Weak'; if (s <= 2) return 'Fair';
    if (s <= 3) return 'Good'; return 'Strong';
  }

  getStrengthClass(): string {
    const s = this.getPasswordStrength();
    if (s <= 1) return 'weak'; if (s <= 2) return 'fair';
    if (s <= 3) return 'good'; return 'strong';
  }

  getStrengthWidth(): string {
    return `${(this.getPasswordStrength() / 5) * 100}%`;
  }
}