import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ContratSponsorService } from '../../services/contrat-sponsor.service';

@Component({
  selector: 'app-admin-contrat-sponsor-edit',
  templateUrl: './admin-contrat-sponsor-edit.component.html',
  styleUrls: ['./admin-contrat-sponsor-edit.component.css']
})
export class AdminContratSponsorEditComponent implements OnInit {
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
  contratId = 0;
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contratService: ContratSponsorService
  ) {}

  ngOnInit(): void {
    this.contratId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.contratId) this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.contratService.getById(this.contratId).subscribe({
      next: (data) => {
        this.formData = {
          sponsorId: data.sponsorId,
          equipeId: data.equipeId,
          montant: data.montant,
          dateDebut: data.dateDebut ? new Date(data.dateDebut).toISOString().split('T')[0] : '',
          dateFin: data.dateFin ? new Date(data.dateFin).toISOString().split('T')[0] : '',
          statut: data.statut,
          conditions: data.conditions
        };
        this.loading = false;
      },
      error: (err) => { this.error = 'Chargement impossible : ' + err.message; this.loading = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.sponsorId || this.formData.sponsorId <= 0) { this.error = 'L\'ID sponsor est obligatoire.'; return; }
    if (!this.formData.equipeId || this.formData.equipeId <= 0) { this.error = 'L\'ID équipe est obligatoire.'; return; }
    if (this.formData.montant < 0) { this.error = 'Le montant ne peut pas être négatif.'; return; }
    if (!this.formData.dateDebut) { this.error = 'La date de début est obligatoire.'; return; }
    if (!this.formData.dateFin) { this.error = 'La date de fin est obligatoire.'; return; }
    this.saving = true;
    const payload: any = {
      id: this.contratId,
      sponsorId: this.formData.sponsorId,
      equipeId: this.formData.equipeId,
      montant: this.formData.montant,
      dateDebut: new Date(this.formData.dateDebut),
      dateFin: new Date(this.formData.dateFin),
      statut: this.formData.statut,
      conditions: this.formData.conditions
    };
    this.contratService.update(this.contratId, payload).subscribe({
      next: () => {
        this.successMessage = 'Contrat mis à jour !';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/admin/contrat-sponsor']), 1500);
      },
      error: (err) => {
        this.error = err.error?.message || err.message || 'Erreur inconnue';
        this.saving = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/contrat-sponsor']); }
}
