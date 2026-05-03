import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sponsor-communaute-list',
  templateUrl: './communaute-list.component.html',
  styleUrls: ['./communaute-list.component.css']
})
export class SponsorCommunauteListComponent implements OnInit {
  communautes: CommunauteDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalItems: number = 0;
  totalPages: number = 0;

  constructor(
    private communauteService: CommunauteService, 
    private router: Router, 
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.communauteService.getAll().subscribe({
      next: (data) => { 
        this.communautes = data; 
        this.updatePagination();
        this.loading = false; 
      },
      error: (err) => { 
        this.error = 'Erreur chargement : ' + err.message; 
        this.loading = false; 
      }
    });
  }

  viewDetail(id: number): void {
    this.router.navigate(['/sponsor/communaute', id]);
  }

  canCreateSponsoring(): boolean {
    return this.currentUserRole === 'SPONSOR';
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Pagination methods
  get paginatedCommunautes(): CommunauteDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.communautes.slice(startIndex, endIndex);
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
    this.totalItems = this.communautes.length;
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

