import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-evenement-form',
  templateUrl: './evenement-form.component.html',
  styleUrls: ['./evenement-form.component.css']
})
export class EvenementFormComponent implements OnInit {
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
  ) { }

  ngOnInit(): void {
    this.resolveUserId();
    // Set minimum date to today
    const today = new Date();
    this.formData.date = today.toISOString().split('T')[0];
    this.loadCommunautes();
  }

  private resolveUserId(): void {
    const userIdStr = localStorage.getItem('UserIdConnect');
    if (userIdStr) {
      const parsed = parseInt(userIdStr, 10);
      if (!isNaN(parsed) && parsed > 0) {
        this.formData.organisateurId = parsed;
        return;
      }
    }
    const email = localStorage.getItem('EmailUserConnect') || '';
    if (email) {
      let hash = 0;
      for (let i = 0; i < email.length; i++) {
        hash = ((hash << 5) - hash) + email.charCodeAt(i);
        hash |= 0;
      }
      const userId = Math.abs(hash) || 1;
      this.formData.organisateurId = userId;
      localStorage.setItem('UserIdConnect', String(userId));
    }
  }

  loadCommunautes(): void {
    this.loadingCommunautes = true;
    this.communauteService.getAll().subscribe({
      next: (data) => {
        this.communautes = data;
        if (data.length > 0) {
          this.formData.communauteId = data[0].id;
        }
        this.loadingCommunautes = false;
      },
      error: () => {
        this.loadingCommunautes = false;
      }
    });
  }

  save(): void {
    this.error = '';

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

    this.loading = true;

    const payload = {
      id: null as any,
      titre: this.formData.titre.trim(),
      description: this.formData.description.trim(),
      date: new Date(this.formData.date),
      communauteId: this.formData.communauteId,
      organisateurId: this.formData.organisateurId
    };

    this.evenementService.create(payload).subscribe({
      next: () => {
        this.router.navigate(['/client/evenement']);
      },
      error: (err) => {
        const backendMsg =
          err.error?.message ||
          err.error?.error ||
          (typeof err.error === 'string' ? err.error : null) ||
          err.message ||
          'Erreur inconnue';
        this.error = 'Erreur lors de la création : ' + backendMsg;
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/client/evenement']);
  }
}

