import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';

@Component({
  selector: 'app-sponsor-form',
  templateUrl: './sponsor-form.component.html',
  styleUrls: ['./sponsor-form.component.css']
})
export class SponsorFormComponent {
  formData = { nom: '', type: 'Bronze', contactEmail: '', telephone: '', adresse: '' };
  typeOptions = ['Bronze', 'Argent', 'Or', 'Premium', 'Partenaire'];
  loading = false;
  error = '';

  constructor(private sponsorService: SponsorService, private router: Router) {}

  save(): void {
    this.error = '';
    if (!this.formData.nom.trim()) { this.error = 'Le nom est obligatoire.'; return; }
    if (!this.formData.contactEmail.trim()) { this.error = 'L\'email est obligatoire.'; return; }
    if (this.formData.telephone && !/^\d{8}$/.test(this.formData.telephone)) {
      this.error = 'Le téléphone doit contenir exactement 8 chiffres.'; return;
    }
    this.loading = true;
    const payload: any = { id: null, ...this.formData, nom: this.formData.nom.trim() };
    this.sponsorService.create(payload).subscribe({
      next: () => this.router.navigate(['/client/sponsor']),
      error: (err) => { this.error = err.error?.message || err.message || 'Erreur inconnue'; this.loading = false; }
    });
  }

  cancel(): void { this.router.navigate(['/client/sponsor']); }
}
