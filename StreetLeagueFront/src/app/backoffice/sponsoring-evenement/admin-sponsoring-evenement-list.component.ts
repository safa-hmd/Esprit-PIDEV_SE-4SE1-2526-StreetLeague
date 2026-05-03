import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-sponsoring-evenement-list',
  templateUrl: './admin-sponsoring-evenement-list.component.html',
  styleUrls: ['./admin-sponsoring-evenement-list.component.css']
})
export class AdminSponsoringEvenementListComponent implements OnInit {
  sponsorings: SponsoringEvenementDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(private sponsoringService: SponsoringEvenementService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.sponsoringService.getAll().subscribe({
      next: (data) => { this.sponsorings = data; this.loading = false; },
      error: (err: any) => {
        this.error = 'Erreur chargement : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  viewDetail(id: number): void { this.router.navigate(['/admin/sponsoring-evenement', id]); }

  delete(id: number): void {
    if (!confirm('Delete définitivement ce sponsoring ?')) return;
    this.loading = true;
    this.sponsoringService.delete(id).subscribe({
      next: () => {
        this.sponsorings = this.sponsorings.filter(s => s.id !== id);
        this.successMessage = 'Sponsoring supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur suppression : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  // Méthodes pour approbation/rejet
  approveSponsoring(id: number): void {
    this.loading = true;
    this.sponsoringService.updateStatus(id, 'APPROUVÉ').subscribe({
      next: (updated) => {
        const sponsoring = this.sponsorings.find(s => s.id === id);
        if (sponsoring && updated) {
          sponsoring.statut = 'APPROUVÉ';
        }
        this.successMessage = 'Sponsoring approuvé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur approbation : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  rejectSponsoring(id: number): void {
    if (!confirm('Are you sure de vouloir reject ce sponsoring ? Il sera supprimé automatiquement.')) return;
    this.loading = true;
    this.sponsoringService.updateStatus(id, 'REJETÉ').subscribe({
      next: () => {
        // Si le sponsoring a été supprimé (rejeté), le retirer de la liste
        this.sponsorings = this.sponsorings.filter(s => s.id !== id);
        this.successMessage = 'Sponsoring rejeté et supprimé avec succès.';
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
  isAdmin(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN';
  }

  getContributionBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }
}

