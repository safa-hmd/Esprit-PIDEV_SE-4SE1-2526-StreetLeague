import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-admin-evenement-form',
  templateUrl: './admin-evenement-form.component.html',
  styleUrls: ['./admin-evenement-form.component.css']
})
export class AdminEvenementFormComponent implements OnInit {
  formData = {
    titre: '',
    description: '',
    date: '',
    communauteId: 0,
    organisateurId: 1
  };

  communautes: CommunauteDTO[] = [];
  loading = false;
  error = '';
  loadingCommunautes = false;

  constructor(
    private evenementService: EvenementCommunauteService,
    private communauteService: CommunauteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.formData.date = new Date().toISOString().split('T')[0];
    const uid = localStorage.getItem('UserIdConnect');
    if (uid) this.formData.organisateurId = parseInt(uid, 10) || 1;
    this.loadCommunautes();
  }

  loadCommunautes(): void {
    this.loadingCommunautes = true;
    this.communauteService.getAll().subscribe({
      next: (data) => {
        this.communautes = data;
        if (data.length > 0 && !this.formData.communauteId) {
          this.formData.communauteId = data[0].id;
        }
        this.loadingCommunautes = false;
      },
      error: () => { this.loadingCommunautes = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.titre.trim()) { this.error = 'Le titre est obligatoire.'; return; }
    if (!this.formData.description.trim()) { this.error = 'La description est obligatoire.'; return; }
    if (!this.formData.date) { this.error = 'La date est obligatoire.'; return; }
    if (!this.formData.communauteId || this.formData.communauteId <= 0) { this.error = 'Veuillez sélectionner une communauté.'; return; }

    this.loading = true;
    const payload: any = {
      id: null,
      titre: this.formData.titre.trim(),
      description: this.formData.description.trim(),
      date: new Date(this.formData.date),
      communauteId: this.formData.communauteId,
      organisateurId: this.formData.organisateurId
    };

    this.evenementService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/evenement']),
      error: (err) => {
        this.error = err.error?.message || err.message || 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/evenement']); }
}
