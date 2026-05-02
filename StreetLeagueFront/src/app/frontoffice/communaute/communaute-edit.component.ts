import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-communaute-edit',
  templateUrl: './communaute-edit.component.html',
  styleUrls: ['./communaute-edit.component.css']
})
export class CommunauteEditComponent implements OnInit {

  communauteId: number = 0;
  loading = true;
  saving = false;
  error = '';
  successMessage = '';

  formData = {
    nom: '',
    description: '',
    type: '',
    dateCreation: '',
    createurId: 0
  };

  typeOptions = ['Sports', 'Loisir', 'Professionnel', 'Culture', 'Health', 'Education'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communauteService: CommunauteService
  ) { }

  ngOnInit(): void {
    this.communauteId = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.communauteId) {
      this.error = 'Identifiant invalide';
      this.loading = false;
      return;
    }

    // Charger les données actuelles pour pré-remplir le formulaire
    this.communauteService.getById(this.communauteId).subscribe({
      next: (data) => {
        this.formData = {
          nom: data.nom || '',
          description: data.description || '',
          type: data.type || '',
          dateCreation: data.dateCreation
            ? new Date(data.dateCreation).toISOString().split('T')[0]
            : '',
          createurId: data.createurId || 0
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.formData.nom || !this.formData.type) {
      this.error = 'Le name et le type sont obligatoires.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.successMessage = '';

    const payload: CommunauteDTO = {
      id: this.communauteId,
      nom: this.formData.nom,
      description: this.formData.description,
      type: this.formData.type,
      dateCreation: this.formData.dateCreation ? new Date(this.formData.dateCreation) : new Date(),
      createurId: this.formData.createurId
    };

    this.communauteService.update(this.communauteId, payload).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Community modifiée avec succès !';
        setTimeout(() => {
          this.router.navigate(['/client/communaute', this.communauteId]);
        }, 1500);
      },
      error: (err) => {
        this.saving = false;
        this.error = 'Erreur lors de la modification: ' + (err.error?.message || err.message);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/client/communaute', this.communauteId]);
  }

  goToList(): void {
    this.router.navigate(['/client/communaute']);
  }
}

