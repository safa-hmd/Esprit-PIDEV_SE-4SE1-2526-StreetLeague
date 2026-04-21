import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';

@Component({
  selector: 'app-admin-communaute-form',
  templateUrl: './admin-communaute-form.component.html',
  styleUrls: ['./admin-communaute-form.component.css']
})
export class AdminCommunauteFormComponent implements OnInit {
  formData = { nom: '', description: '', type: 'Sports', dateCreation: '', createurId: 1 };
  typeOptions = ['Sports', 'Loisir', 'Professionnel', 'Culture', 'Santé', 'Education'];
  loading = false;
  error = '';

  constructor(private communauteService: CommunauteService, private router: Router) {}

  ngOnInit(): void {
    this.formData.dateCreation = new Date().toISOString().split('T')[0];
    const uid = localStorage.getItem('UserIdConnect');
    if (uid) this.formData.createurId = parseInt(uid, 10) || 1;
  }

  save(): void {
    this.error = '';
    if (!this.formData.nom.trim()) { this.error = 'Le nom est obligatoire.'; return; }
    if (!this.formData.description.trim()) { this.error = 'La description est obligatoire.'; return; }
    this.loading = true;
    const payload: any = {
      id: null,
      nom: this.formData.nom.trim(),
      description: this.formData.description.trim(),
      type: this.formData.type,
      dateCreation: new Date(this.formData.dateCreation),
      createurId: this.formData.createurId
    };
    this.communauteService.create(payload).subscribe({
      next: () => this.router.navigate(['/admin/communaute']),
      error: (err) => {
        this.error = err.error?.message || err.message || 'Erreur inconnue';
        this.loading = false;
      }
    });
  }

  cancel(): void { this.router.navigate(['/admin/communaute']); }
}
