import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';

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

  constructor(private sponsorService: SponsorService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.sponsorService.getAll().subscribe({
      next: (data) => { this.sponsors = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/client/sponsor/new']); }
  viewDetail(id: number): void { this.router.navigate(['/client/sponsor', id]); }
  edit(id: number): void { this.router.navigate(['/client/sponsor', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Supprimer ce sponsor ?')) return;
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

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }
}
