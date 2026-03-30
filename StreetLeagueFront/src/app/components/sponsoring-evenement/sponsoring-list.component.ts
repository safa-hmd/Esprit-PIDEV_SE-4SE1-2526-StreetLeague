import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';

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

  constructor(private sponsoringService: SponsoringEvenementService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.sponsoringService.getAll().subscribe({
      next: (data) => { this.sponsorings = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/client/sponsoring-evenement/new']); }
  viewDetail(id: number): void { this.router.navigate(['/client/sponsoring-evenement', id]); }
  edit(id: number): void { this.router.navigate(['/client/sponsoring-evenement', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Supprimer ce sponsoring événement ?')) return;
    this.loading = true;
    this.sponsoringService.delete(id).subscribe({
      next: () => {
        this.sponsorings = this.sponsorings.filter(s => s.id !== id);
        this.successMessage = 'Sponsoring supprimé.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur : ' + err.message; this.loading = false; }
    });
  }

  getContribBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }
}
