import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-sponsor-list',
  templateUrl: './admin-sponsor-list.component.html',
  styleUrls: ['./admin-sponsor-list.component.css']
})
export class AdminSponsorListComponent implements OnInit {
  sponsors: SponsorDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(private sponsorService: SponsorService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.sponsorService.getAll().subscribe({
      next: (data) => { this.sponsors = data; this.loading = false; },
      error: (err: any) => {
        this.error = 'Erreur chargement : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  viewDetail(id: number): void { this.router.navigate(['/admin/sponsor', id]); }

  delete(id: number): void {
    if (!confirm('Delete définitivement ce sponsor ?')) return;
    this.loading = true;
    this.sponsorService.delete(id).subscribe({
      next: () => {
        this.sponsors = this.sponsors.filter(s => s.id !== id);
        this.successMessage = 'Sponsor supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur suppression : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  // Méthodes pour l'approbation/rejet des sponsors
  approveSponsor(id: number): void {
    this.loading = true;
    this.sponsorService.updateStatus(id, 'APPROUVÉ').subscribe({
      next: () => {
        const sponsor = this.sponsors.find(s => s.id === id);
        if (sponsor) {
          sponsor.status = 'APPROUVÉ';
        }
        this.successMessage = 'Sponsor approuvé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur approbation : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  rejectSponsor(id: number): void {
    if (!confirm('Are you sure de vouloir reject ce sponsor ? Il sera supprimé automatiquement.')) return;
    this.loading = true;
    this.sponsorService.updateStatus(id, 'REJETÉ').subscribe({
      next: () => {
        // Si le sponsor a été supprimé (rejeté), le retirer de la liste
        this.sponsors = this.sponsors.filter(s => s.id !== id);
        this.successMessage = 'Sponsor rejeté et supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur rejet : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  // Méthode pour obtenir le style du badge de status
  getStatusBadgeClass(status?: string): string {
    switch (status) {
      case 'APPROUVÉ': return 'badge-approved';
      case 'REJETÉ': return 'badge-rejected';
      case 'PENDING':
      default: return 'badge-pending';
    }
  }

  // Méthodes pour vérifier les permissions
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  createNew(): void { this.router.navigate(['/admin/sponsor/new']); }
  edit(id: number): void { this.router.navigate(['/admin/sponsor', id, 'edit']); }

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }

  isAdmin(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN';
  }
}

