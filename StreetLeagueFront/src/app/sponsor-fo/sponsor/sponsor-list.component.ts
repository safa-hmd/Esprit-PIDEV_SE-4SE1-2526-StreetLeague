import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sponsor-list',
  templateUrl: './sponsor-list.component.html',
  styleUrls: ['./sponsor-list.component.css']
})
export class SponsorListComponent implements OnInit {
  sponsors: SponsorDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalItems: number = 0;
  totalPages: number = 0;

  constructor(private sponsorService: SponsorService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.sponsorService.getAll().subscribe({
      next: (data) => { 
        this.sponsors = data; 
        this.updatePagination();
        this.loading = false; 
      },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/sponsor/sponsor/new']); }
  viewDetail(id: number): void { this.router.navigate(['/sponsor/sponsor', id]); }
  edit(id: number): void { this.router.navigate(['/sponsor/sponsor', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete ce sponsor ?')) return;
    this.loading = true;
    this.sponsorService.delete(id).subscribe({
      next: () => {
        this.sponsors = this.sponsors.filter(s => s.id !== id);
        this.successMessage = 'Sponsor supprimé.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur : ' + err.message; this.loading = false; }
    });
  }

  // Méthodes pour vérifier les permissions SPONSOR
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN' || this.currentUserRole === 'ROLE_SPONSOR';
  }

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }

  // Pagination methods
  get paginatedSponsors(): SponsorDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.sponsors.slice(startIndex, endIndex);
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
    this.totalItems = this.sponsors.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    if (this.currentPage > this.totalPages) {
      this.currentPage = Math.max(1, this.totalPages);
    }
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

  // Méthode pour obtenir la classe CSS du status
  getStatusClass(status?: string): string {
    switch (status) {
      case 'APPROUVÉ':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning';
      case 'REJETÉ':
        return 'bg-danger';
      default:
        return 'bg-primary';
    }
  }
}
