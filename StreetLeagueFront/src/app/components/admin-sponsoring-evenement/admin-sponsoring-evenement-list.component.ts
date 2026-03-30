import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';

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

  constructor(private sponsoringService: SponsoringEvenementService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

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
    if (!confirm('Supprimer définitivement ce sponsoring ?')) return;
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

  getContributionBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }
}
