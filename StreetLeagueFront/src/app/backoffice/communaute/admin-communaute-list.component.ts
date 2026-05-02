import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-communaute-list',
  templateUrl: './admin-communaute-list.component.html',
  styleUrls: ['./admin-communaute-list.component.css']
})
export class AdminCommunauteListComponent implements OnInit {
  communautes: CommunauteDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(private communauteService: CommunauteService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.communauteService.getAll().subscribe({
      next: (data) => { this.communautes = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/admin/communaute/new']); }
  edit(id: number): void { this.router.navigate(['/admin/communaute', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete cette community ?')) return;
    this.loading = true;
    this.communauteService.delete(id).subscribe({
      next: () => {
        this.communautes = this.communautes.filter(c => c.id !== id);
        this.successMessage = 'Community supprimée.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }

  // Méthodes pour vérifier les permissions
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_COMMUNITY_MANAGER' || this.currentUserRole === 'ROLE_PLAYER';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_COMMUNITY_MANAGER' || this.currentUserRole === 'ROLE_PLAYER';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_COMMUNITY_MANAGER' || this.currentUserRole === 'ROLE_PLAYER';
  }

  // Méthodes pour join/leave (PLAYER uniquement)
  joinCommunaute(id: number): void {
    if (this.currentUserRole !== 'ROLE_PLAYER') return;
    // Implémentation à add
    console.log('Join community', id);
  }

  leaveCommunaute(id: number): void {
    if (this.currentUserRole !== 'ROLE_PLAYER') return;
    // Implémentation à add
    console.log('Leave community', id);
  }
}
