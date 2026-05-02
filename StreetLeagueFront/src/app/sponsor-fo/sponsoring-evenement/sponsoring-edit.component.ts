import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';

@Component({
  selector: 'app-sponsoring-edit',
  templateUrl: './sponsoring-edit.component.html',
  styleUrls: ['./sponsoring-edit.component.css']
})
export class SponsoringEditComponent implements OnInit {
  formData = { sponsorId: 0, evenementId: 0, contribution: 0, typeContribution: 'Financier' };
  typeOptions = ['Financier', 'Matériel', 'Service', 'Médiatique', 'Autre'];
  sponsoringId = 0;
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sponsoringService: SponsoringEvenementService
  ) {}

  ngOnInit(): void {
    this.sponsoringId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.sponsoringId) this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.sponsoringService.getById(this.sponsoringId).subscribe({
      next: (data) => {
        this.formData = {
          sponsorId: data.sponsorId,
          evenementId: data.evenementId,
          contribution: data.contribution,
          typeContribution: data.typeContribution
        };
        this.loading = false;
      },
      error: (err) => { this.error = 'Chargement impossible : ' + err.message; this.loading = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.sponsorId || this.formData.sponsorId <= 0) { this.error = 'L\'ID sponsor est obligatoire.'; return; }
    if (!this.formData.evenementId || this.formData.evenementId <= 0) { this.error = 'L\'ID event est obligatoire.'; return; }
    this.saving = true;
    const payload: any = { id: this.sponsoringId, ...this.formData };
    this.sponsoringService.update(this.sponsoringId, payload).subscribe({
      next: () => {
        this.successMessage = 'Sponsoring mis à jour !';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/sponsor/sponsoring-evenement']), 1500);
      },
      error: (err) => { this.error = err.error?.message || err.message || 'Erreur'; this.saving = false; }
    });
  }

  cancel(): void { this.router.navigate(['/sponsor/sponsoring-evenement']); }
}
