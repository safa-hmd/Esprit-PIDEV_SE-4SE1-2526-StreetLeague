import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ContratSponsorService } from '../../services/contrat-sponsor.service';

@Component({
  selector: 'app-admin-contrat-sponsor-form',
  templateUrl: './admin-contrat-sponsor-form.component.html',
  styleUrls: ['./admin-contrat-sponsor-form.component.css']
})
export class AdminContratSponsorFormComponent implements OnInit {
  formData = {
    sponsorId: 0,
    equipeId: 0,
    montant: 0,
    dateDebut: '',
    dateFin: '',
    statut: 'En attente',
    conditions: ''
  };
  statutOptions = ['En attente', 'Actif', 'Expiré', 'Résilié', 'Suspendu'];
  loading = false;
  error = '';

  constructor(private contratService: ContratSponsorService, private router: Router) {}

  ngOnInit(): void {
    const today = new Date().toISOString().split('T')[0];
    this.formData.dateDebut = today;
    const nextYear = new Date();
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    this.formData.dateFin = nextYear.toISOString().split('T')[0];
  }

  save(): void {
    this.error = '';
    if (!this.formData.sponsorId || this.formData.sponsorId <= 0) { this.error = 'L\'ID sponsor est obligatoire.'; return; }
    if (!this.formData.equipeId || this.formData.equipeId <= 0) { this.error = 'L\'ID équipe est obligatoire.'; return; }
    if (this.formData.montant < 0) { this.error = 'Le montant ne peut pas être négatif.'; return; }
    if (!this.formData.dateDebut) { this.error = 'La date de début est obligatoire.'; return; }
    if (!this.formData.dateFin) { this.error = 'La date de fin est obligatoire.'; return; }
    this.loading = true;
    const payload: any = {
      id: null,
      sponsorId: this.formData.sponsorId,
      equipeId: this.formData.equipeId,
      montant: this.formData.montant,
      dateDebut: new Date(this.formData.dateDebut),
      dateFin: new Date(this.formData.dateFin),
      statut: this.formData.statut,
      conditions: this.formData.conditions
    };
    this.contratService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/contrat-sponsor']),
      error: (err: any) => {
        this.error = err.error?.error ?? err.error?.message ?? err.message ?? 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/contrat-sponsor']); }
}
