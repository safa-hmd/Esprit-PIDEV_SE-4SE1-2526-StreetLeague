import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ContratSponsorService } from '../../services/contrat-sponsor.service';
import { ContratSponsorDTO } from '../../models/contrat-sponsor-dto';

@Component({
  selector: 'app-contrat-list',
  templateUrl: './contrat-list.component.html',
  styleUrls: ['./contrat-list.component.css']
})
export class ContratListComponent implements OnInit {
  contrats: ContratSponsorDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(private contratService: ContratSponsorService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.contratService.getAll().subscribe({
      next: (data) => { this.contrats = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/client/contrat-sponsor/new']); }
  edit(id: number): void { this.router.navigate(['/client/contrat-sponsor', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Supprimer ce contrat de sponsoring ?')) return;
    this.loading = true;
    this.contratService.delete(id).subscribe({
      next: () => {
        this.contrats = this.contrats.filter(c => c.id !== id);
        this.successMessage = 'Contrat supprimé avec succès.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }

  getStatutBadgeClass(statut: string): string {
    const s = statut?.toLowerCase() || '';
    if (s.includes('actif') || s.includes('valid')) return 'badge-actif';
    if (s.includes('expiré') || s.includes('expire')) return 'badge-expire';
    if (s.includes('en attente') || s.includes('attente')) return 'badge-attente';
    if (s.includes('résilié') || s.includes('resilie')) return 'badge-resilie';
    return 'badge-autre';
  }
}
