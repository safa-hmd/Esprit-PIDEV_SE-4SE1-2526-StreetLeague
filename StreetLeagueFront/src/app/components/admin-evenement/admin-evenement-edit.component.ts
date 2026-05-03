import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-admin-evenement-edit',
  templateUrl: './admin-evenement-edit.component.html',
  styleUrls: ['./admin-evenement-edit.component.css']
})
export class AdminEvenementEditComponent implements OnInit {
  formData = {
    titre: '',
    description: '',
    date: '',
    communauteId: 0,
    organisateurId: 1
  };

  communautes: CommunauteDTO[] = [];
  evenementId = 0;
  loading = false;
  saving = false;
  error = '';
  successMessage = '';
  loadingCommunautes = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evenementService: EvenementCommunauteService,
    private communauteService: CommunauteService
  ) {}

  ngOnInit(): void {
    this.evenementId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCommunautes();
    if (this.evenementId) this.loadData();
  }

  loadCommunautes(): void {
    this.loadingCommunautes = true;
    this.communauteService.getAll().subscribe({
      next: (data) => { this.communautes = data; this.loadingCommunautes = false; },
      error: () => { this.loadingCommunautes = false; }
    });
  }

  loadData(): void {
    this.loading = true;
    this.evenementService.getById(this.evenementId).subscribe({
      next: (data) => {
        this.formData = {
          titre: data.titre,
          description: data.description,
          date: data.date ? new Date(data.date).toISOString().split('T')[0] : '',
          communauteId: data.communauteId,
          organisateurId: data.organisateurId
        };
        this.loading = false;
      },
      error: (err) => { this.error = 'Chargement impossible : ' + err.message; this.loading = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.titre.trim()) { this.error = 'Le titre est obligatoire.'; return; }
    if (!this.formData.description.trim()) { this.error = 'La description est obligatoire.'; return; }
    if (!this.formData.date) { this.error = 'La date est obligatoire.'; return; }
    if (!this.formData.communauteId || this.formData.communauteId <= 0) { this.error = 'Veuillez sélectionner une communauté.'; return; }

    this.saving = true;
    const payload: any = {
      id: this.evenementId,
      titre: this.formData.titre.trim(),
      description: this.formData.description.trim(),
      date: new Date(this.formData.date),
      communauteId: this.formData.communauteId,
      organisateurId: this.formData.organisateurId
    };

    this.evenementService.update(this.evenementId, payload).subscribe({
      next: () => {
        this.successMessage = 'Événement mis à jour !';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/admin/evenement']), 1500);
      },
      error: (err) => {
        this.error = err.error?.message || err.message || 'Erreur inconnue';
        this.saving = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/evenement']); }
}
