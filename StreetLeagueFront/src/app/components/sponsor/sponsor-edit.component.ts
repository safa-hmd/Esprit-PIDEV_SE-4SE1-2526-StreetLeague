import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';

@Component({
  selector: 'app-sponsor-edit',
  templateUrl: './sponsor-edit.component.html',
  styleUrls: ['./sponsor-edit.component.css']
})
export class SponsorEditComponent implements OnInit {
  formData = { nom: '', type: 'Bronze', contactEmail: '', telephone: '', adresse: '' };
  typeOptions = ['Bronze', 'Argent', 'Or', 'Premium', 'Partenaire'];
  sponsorId = 0;
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sponsorService: SponsorService
  ) {}

  ngOnInit(): void {
    this.sponsorId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.sponsorId) this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.sponsorService.getById(this.sponsorId).subscribe({
      next: (data) => {
        this.formData = {
          nom: data.nom,
          type: data.type,
          contactEmail: data.contactEmail,
          telephone: data.telephone,
          adresse: data.adresse
        };
        this.loading = false;
      },
      error: (err) => { this.error = 'Chargement impossible : ' + err.message; this.loading = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.nom.trim()) { this.error = 'Le nom est obligatoire.'; return; }
    if (!this.formData.contactEmail.trim()) { this.error = 'L\'email est obligatoire.'; return; }
    this.saving = true;
    const payload: any = { id: this.sponsorId, ...this.formData };
    this.sponsorService.update(this.sponsorId, payload).subscribe({
      next: () => {
        this.successMessage = 'Sponsor mis à jour !';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/client/sponsor']), 1500);
      },
      error: (err) => { this.error = err.error?.message || err.message || 'Erreur'; this.saving = false; }
    });
  }

  cancel(): void { this.router.navigate(['/client/sponsor']); }
}
