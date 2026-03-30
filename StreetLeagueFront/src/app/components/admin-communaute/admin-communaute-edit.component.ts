import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';

@Component({
  selector: 'app-admin-communaute-edit',
  templateUrl: './admin-communaute-edit.component.html',
  styleUrls: ['./admin-communaute-edit.component.css']
})
export class AdminCommunauteEditComponent implements OnInit {
  formData = { nom: '', description: '', type: 'Sports', dateCreation: '', createurId: 1 };
  typeOptions = ['Sports', 'Loisir', 'Professionnel', 'Culture', 'Santé', 'Education'];
  communauteId = 0;
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communauteService: CommunauteService
  ) {}

  ngOnInit(): void {
    this.communauteId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.communauteId) this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.communauteService.getById(this.communauteId).subscribe({
      next: (data) => {
        this.formData = {
          nom: data.nom,
          description: data.description,
          type: data.type,
          dateCreation: data.dateCreation ? new Date(data.dateCreation).toISOString().split('T')[0] : '',
          createurId: data.createurId
        };
        this.loading = false;
      },
      error: (err) => { this.error = 'Chargement impossible : ' + err.message; this.loading = false; }
    });
  }

  save(): void {
    this.error = '';
    if (!this.formData.nom.trim()) { this.error = 'Le nom est obligatoire.'; return; }
    if (!this.formData.description.trim()) { this.error = 'La description est obligatoire.'; return; }
    this.saving = true;
    const payload: any = {
      id: this.communauteId,
      nom: this.formData.nom.trim(),
      description: this.formData.description.trim(),
      type: this.formData.type,
      dateCreation: new Date(this.formData.dateCreation),
      createurId: this.formData.createurId
    };
    this.communauteService.update(this.communauteId, payload).subscribe({
      next: () => {
        this.successMessage = 'Communauté mise à jour !';
        this.saving = false;
        setTimeout(() => this.router.navigate(['/admin/communaute']), 1500);
      },
      error: (err) => {
        this.error = err.error?.message || err.message || 'Erreur inconnue';
        this.saving = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/communaute']); }
}
