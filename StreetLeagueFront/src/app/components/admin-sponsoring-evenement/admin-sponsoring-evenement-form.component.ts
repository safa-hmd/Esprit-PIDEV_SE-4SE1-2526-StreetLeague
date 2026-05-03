import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';

@Component({
  selector: 'app-admin-sponsoring-evenement-form',
  templateUrl: './admin-sponsoring-evenement-form.component.html',
  styleUrls: ['./admin-sponsoring-evenement-form.component.css']
})
export class AdminSponsoringEvenementFormComponent {
  formData = {
    sponsorId: 0,
    evenementId: 0,
    contribution: 0,
    typeContribution: 'Financier'
  };
  typeOptions = ['Financier', 'Matériel', 'Service', 'Médiatique', 'Autre'];
  loading = false;
  error = '';

  constructor(private sponsoringService: SponsoringEvenementService, private router: Router) {}

  save(): void {
    this.error = '';
    if (!this.formData.sponsorId || this.formData.sponsorId <= 0) { this.error = 'L\'ID sponsor est obligatoire.'; return; }
    if (!this.formData.evenementId || this.formData.evenementId <= 0) { this.error = 'L\'ID événement est obligatoire.'; return; }
    if (this.formData.contribution < 0) { this.error = 'La contribution ne peut pas être négative.'; return; }
    this.loading = true;
    const payload: any = {
      id: null,
      sponsorId: this.formData.sponsorId,
      evenementId: this.formData.evenementId,
      contribution: this.formData.contribution,
      typeContribution: this.formData.typeContribution
    };
    this.sponsoringService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/sponsoring-evenement']),
      error: (err: any) => {
        this.error = err.error?.error ?? err.error?.message ?? err.message ?? 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/sponsoring-evenement']); }
}
