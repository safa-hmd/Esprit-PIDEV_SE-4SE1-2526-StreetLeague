import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';

@Component({
  selector: 'app-sponsoring-form',
  templateUrl: './sponsoring-form.component.html',
  styleUrls: ['./sponsoring-form.component.css']
})
export class SponsoringFormComponent {
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
    if (this.formData.contribution <= 0) { this.error = 'La contribution doit être strictement positive (> 0).'; return; }
    this.loading = true;
    const payload: any = { id: null, ...this.formData };
    this.sponsoringService.create(payload).subscribe({
      next: () => this.router.navigate(['/client/sponsoring-evenement']),
      error: (err: any) => {
        this.error = err.error?.error ?? err.error?.message ?? err.message ?? 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/client/sponsoring-evenement']); }
}
