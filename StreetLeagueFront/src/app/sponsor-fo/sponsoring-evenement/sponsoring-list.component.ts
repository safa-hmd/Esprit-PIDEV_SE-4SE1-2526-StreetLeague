import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sponsoring-list',
  templateUrl: './sponsoring-list.component.html',
  styleUrls: ['./sponsoring-list.component.css']
})
export class SponsoringListComponent implements OnInit {
  sponsorings: SponsoringEvenementDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalItems: number = 0;
  totalPages: number = 0;

  constructor(private sponsoringService: SponsoringEvenementService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    
    // Permettre aux SPONSORS de rester sur cette page
    // Ne pas rediriger les ADMINs ici, laisser les guards manage ça
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.sponsoringService.getAll().subscribe({
      next: (data) => { 
        this.sponsorings = data; 
        this.updatePagination();
        this.loading = false; 
      },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/sponsor/sponsoring-evenement/new']); }
  viewDetail(id: number): void { this.router.navigate(['/sponsor/sponsoring-evenement', id]); }
  edit(id: number): void { this.router.navigate(['/sponsor/sponsoring-evenement', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete ce sponsoring event ?')) return;
    this.loading = true;
    this.sponsoringService.delete(id).subscribe({
      next: () => {
        this.sponsorings = this.sponsorings.filter(s => s.id !== id);
        this.successMessage = 'Sponsoring supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }

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
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  isAdmin(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN';
  }

  getContribBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }

  // Pagination methods
  get paginatedSponsorings(): SponsoringEvenementDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.sponsorings.slice(startIndex, endIndex);
  }

  onPageChange(page: number): void {
    this.currentPage = page;
  }

  getPages(): number[] {
    const pages = [];
    const startPage = Math.max(1, this.currentPage - 2);
    const endPage = Math.min(this.totalPages, this.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  updatePagination(): void {
    this.totalItems = this.sponsorings.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    if (this.currentPage > this.totalPages) {
      this.currentPage = Math.max(1, this.totalPages);
    }
  }

  onItemsPerPageChange(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }
}

