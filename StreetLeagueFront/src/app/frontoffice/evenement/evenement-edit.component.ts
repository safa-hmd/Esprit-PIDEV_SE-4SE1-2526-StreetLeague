import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { CommunauteService } from '../../services/communaute.service';
import { EvenementCommunauteDTO } from '../../models/evenement-communaute-dto';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-evenement-edit',
  templateUrl: './evenement-edit.component.html',
  styleUrls: ['./evenement-edit.component.css']
})
export class EvenementEditComponent implements OnInit {
  formData = {
    titre: '',
    description: '',
    date: '',
    communauteId: 0,
    organisateurId: 1
  };

  evenementId: number = 0;
  communautes: CommunauteDTO[] = [];
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evenementService: EvenementCommunauteService,
    private communauteService: CommunauteService
  ) {}

  ngOnInit(): void {
    this.evenementId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCommunautes();
    if (this.evenementId) {
      this.loadEvenement();
    }
  }

  loadEvenement(): void {
    this.loading = true;
    this.error = '';
    this.evenementService.getById(this.evenementId).subscribe({
      next: (data: EvenementCommunauteDTO) => {
        const dateStr = data.date
          ? new Date(data.date).toISOString().split('T')[0]
          : '';
        this.formData = {
          titre: data.titre,
          description: data.description,
          date: dateStr,
          communauteId: data.communauteId,
          organisateurId: data.organisateurId
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Impossible de charger l\'event : ' + err.message;
        this.loading = false;
      }
    });
  }

  loadCommunautes(): void {
    this.communauteService.getAll().subscribe({
      next: (data) => { this.communautes = data; },
      error: () => {}
    });
  }

  save(): void {
    this.error = '';
    this.successMessage = '';

    if (!this.formData.titre || this.formData.titre.trim() === '') {
      this.error = 'Le title est obligatoire.';
      return;
    }
    if (!this.formData.description || this.formData.description.trim() === '') {
      this.error = 'La description est obligatoire.';
      return;
    }
    if (!this.formData.date) {
      this.error = 'La date est obligatoire.';
      return;
    }
    if (!this.formData.communauteId || this.formData.communauteId === 0) {
      this.error = 'Please select une community.';
      return;
    }

    this.saving = true;

    const payload: EvenementCommunauteDTO = {
      id: this.evenementId,
      titre: this.formData.titre.trim(),
      description: this.formData.description.trim(),
      date: new Date(this.formData.date),
      communauteId: this.formData.communauteId,
      organisateurId: this.formData.organisateurId
    };

    this.evenementService.update(this.evenementId, payload).subscribe({
      next: () => {
        this.successMessage = 'Event mis à jour avec succès !';
        this.saving = false;
        setTimeout(() => {
          this.router.navigate(['/client/evenement', this.evenementId]);
        }, 1500);
      },
      error: (err) => {
        const backendMsg =
          err.error?.message ||
          err.error?.error ||
          (typeof err.error === 'string' ? err.error : null) ||
          err.message ||
          'Erreur inconnue';
        this.error = 'Error during update : ' + backendMsg;
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/client/evenement', this.evenementId]);
  }
}

