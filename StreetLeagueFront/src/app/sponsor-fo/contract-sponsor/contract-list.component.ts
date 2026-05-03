import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ContractSponsorService } from '../../services/contract-sponsor.service';
import { ContractSponsorDTO } from '../../models/contract-sponsor-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-contract-list',
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.css']
})
export class ContractListComponent implements OnInit {
  contracts: ContractSponsorDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalItems: number = 0;
  totalPages: number = 0;

  constructor(private contractService: ContractSponsorService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    
    // Permettre aux SPONSORS de rester sur cette page
    // Ne pas rediriger les ADMINs ici, laisser les guards manage ça
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.contractService.getAll().subscribe({
      next: (data) => { 
        this.contracts = data; 
        this.updatePagination();
        this.loading = false; 
      },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/sponsor/contract-sponsor/new']); }
  edit(id: number): void { this.router.navigate(['/sponsor/contract-sponsor', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete ce contract de sponsoring ?')) return;
    this.loading = true;
    this.contractService.delete(id).subscribe({
      next: () => {
        this.contracts = this.contracts.filter(c => c.id !== id);
        this.successMessage = 'Contract supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }

  approveContract(id: number): void {
    this.loading = true;
    this.contractService.updateStatus(id, 'APPROUVÉ').subscribe({
      next: (updated) => {
        const contract = this.contracts.find(c => c.id === id);
        if (contract && updated) {
          contract.statut = 'APPROUVÉ';
        }
        this.successMessage = 'Contract approuvé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur approbation : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  rejectContract(id: number): void {
    if (!confirm('Are you sure de vouloir reject ce contract ? Il sera supprimé automatiquement.')) return;
    this.loading = true;
    this.contractService.updateStatus(id, 'REJETÉ').subscribe({
      next: () => {
        // Si le contract a été supprimé (rejeté), le retirer de la liste
        this.contracts = this.contracts.filter(c => c.id !== id);
        this.successMessage = 'Contract rejeté et supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.error = 'Erreur rejet : ' + (err.error?.error ?? err.error?.message ?? err.message);
        this.loading = false;
      }
    });
  }

  getStatusBadgeClass(status?: string): string {
    switch (status) {
      case 'APPROUVÉ': return 'badge-approved';
      case 'REJETÉ': return 'badge-rejected';
      case 'PENDING':
      default: return 'badge-pending';
    }
  }

  canCreate(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'SPONSOR';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'SPONSOR';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'SPONSOR';
  }

  isAdmin(): boolean {
    return this.currentUserRole === 'ADMIN';
  }

  // Pagination methods
  get paginatedContracts(): ContractSponsorDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.contracts.slice(startIndex, endIndex);
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
    this.totalItems = this.contracts.length;
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


