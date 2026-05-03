import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ContractSponsorService } from '../../services/contract-sponsor.service';

@Component({
  selector: 'app-admin-contract-sponsor-form',
  templateUrl: './admin-contract-sponsor-form.component.html',
  styleUrls: ['./admin-contract-sponsor-form.component.css']
})
export class AdminContractSponsorFormComponent implements OnInit {
  formData = {
    sponsorId: 0,
    equipeId: 0,
    montantTotal: 0,
    dateDebut: '',
    dateFin: '',
    statut: 'PENDING',
    conditions: ''
  };
  statusOptions = ['PENDING', 'APPROUVÉ', 'REJETÉ'];
  loading = false;
  error = '';

  constructor(private contractService: ContractSponsorService, private router: Router) {}

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
    if (this.formData.montantTotal < 0) { this.error = 'Le amount ne peut pas être négatif.'; return; }
    if (!this.formData.dateDebut) { this.error = 'La start date est obligatoire.'; return; }
    if (!this.formData.dateFin) { this.error = 'La end date est obligatoire.'; return; }
    this.loading = true;
    const payload: any = {
      id: null,
      sponsorId: this.formData.sponsorId,
      equipeId: this.formData.equipeId,
      montantTotal: this.formData.montantTotal,
      dateDebut: this.formData.dateDebut,
      dateFin: this.formData.dateFin,
      statut: this.formData.statut,
      conditions: this.formData.conditions
    };
    this.contractService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/contract-sponsor']),
      error: (err: any) => {
        this.error = err.error?.error ?? err.error?.message ?? err.message ?? 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/contract-sponsor']); }
}


