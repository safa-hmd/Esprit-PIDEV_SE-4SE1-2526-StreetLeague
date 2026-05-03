import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ContractSponsorService } from '../../services/contract-sponsor.service';
import { ContractSponsorDTO } from '../../models/contract-sponsor-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-contract-sponsor-list',
  templateUrl: './admin-contract-sponsor-list.component.html',
  styleUrls: ['./admin-contract-sponsor-list.component.css']
})
export class AdminContractSponsorListComponent implements OnInit {
  contracts: ContractSponsorDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(private contractService: ContractSponsorService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void { 
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadAll(); 
  }

  loadAll(): void {
    this.loading = true;
    this.contractService.getAll().subscribe({
      next: (data) => { this.contracts = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/admin/contract-sponsor/new']); }
  edit(id: number): void { this.router.navigate(['/admin/contract-sponsor', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete ce contract de sponsoring ?')) return;
    this.loading = true;
    this.contractService.delete(id).subscribe({
      next: () => {
        this.contracts = this.contracts.filter(c => c.id !== id);
        this.successMessage = 'Contract supprimé.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }

  // Méthodes pour approbation/rejet
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
}

