import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';

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

  constructor(private sponsorService: SponsorService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

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
    if (!confirm('Supprimer définitivement ce sponsor ?')) return;
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

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }
}
