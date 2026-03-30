import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';

@Component({
  selector: 'app-communaute-form',
  templateUrl: './communaute-form.component.html',
  styleUrls: ['./communaute-form.component.css']
})
export class CommunauteFormComponent implements OnInit {
  formData = {
    nom: '',
    description: '',
    type: 'Sports',
    dateCreation: '',
    createurId: 1   // valeur par défaut
  };

  loading = false;
  error = '';

  typeOptions = ['Sports', 'Loisir', 'Professionnel', 'Culture', 'Santé', 'Education'];

  constructor(
    private communauteService: CommunauteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.resolveUserId();

    // Date du jour par défaut
    const today = new Date();
    this.formData.dateCreation = today.toISOString().split('T')[0];
  }

  /** Résolution de l'ID utilisateur depuis localStorage.
   *  Même logique que CommunauteListComponent. */
  private resolveUserId(): void {
    // 1. Essai direct
    const userIdStr = localStorage.getItem('UserIdConnect');
    if (userIdStr) {
      const parsed = parseInt(userIdStr, 10);
      if (!isNaN(parsed) && parsed > 0) {
        this.formData.createurId = parsed;
        return;
      }
    }

    // 2. Fallback : hash stable de l'email
    const email = localStorage.getItem('EmailUserConnect') || '';
    if (email) {
      let hash = 0;
      for (let i = 0; i < email.length; i++) {
        hash = ((hash << 5) - hash) + email.charCodeAt(i);
        hash |= 0;
      }
      const userId = Math.abs(hash) || 1;
      this.formData.createurId = userId;
      localStorage.setItem('UserIdConnect', String(userId));
    }
    // 3. Si aucune info → createurId reste à 1 (valeur par défaut acceptable)
  }

  save(): void {
    this.error = '';

    if (!this.formData.nom || this.formData.nom.trim() === '') {
      this.error = 'Le nom est obligatoire.';
      return;
    }
    if (!this.formData.type || this.formData.type.trim() === '') {
      this.error = 'Le type est obligatoire.';
      return;
    }
    if (!this.formData.description || this.formData.description.trim() === '') {
      this.error = 'La description est obligatoire.';
      return;
    }
    if (!this.formData.dateCreation) {
      this.error = 'La date de création est obligatoire.';
      return;
    }

    this.loading = true;

    // id: null → Hibernate utilise GenerationType.IDENTITY (auto-incrément)
    const payload = {
      id: null as any,
      nom: this.formData.nom.trim(),
      description: this.formData.description.trim(),
      type: this.formData.type,
      dateCreation: new Date(this.formData.dateCreation),
      createurId: this.formData.createurId
    };

    this.communauteService.create(payload).subscribe({
      next: () => {
        this.router.navigate(['/client/communaute']);
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
    this.router.navigate(['/client/communaute']);
  }
}
