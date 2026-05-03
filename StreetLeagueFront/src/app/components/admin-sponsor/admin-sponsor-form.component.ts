import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';

@Component({
  selector: 'app-admin-sponsor-form',
  templateUrl: './admin-sponsor-form.component.html',
  styleUrls: ['./admin-sponsor-form.component.css']
})
export class AdminSponsorFormComponent {
  formData = { nom: '', type: 'Bronze', contactEmail: '', telephone: '', adresse: '' };
  typeOptions = ['Bronze', 'Argent', 'Or', 'Premium', 'Partenaire'];
  loading = false;
  error = '';

  constructor(private sponsorService: SponsorService, private router: Router) {}

  save(): void {
    this.error = '';
    if (!this.formData.nom.trim()) { this.error = 'Le nom est obligatoire.'; return; }
    if (!this.formData.contactEmail.trim()) { this.error = 'L\'email de contact est obligatoire.'; return; }
    this.loading = true;
    const payload: any = { id: null, ...this.formData, nom: this.formData.nom.trim() };
    this.sponsorService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/sponsor']),
      error: (err) => { this.error = err.error?.message || err.message || 'Erreur inconnue'; this.loading = false; }
    });
  }

  cancel(): void { this.router.navigate(['/admin/sponsor']); }
}
