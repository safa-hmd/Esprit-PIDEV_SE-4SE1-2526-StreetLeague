import { Component, OnInit } from '@angular/core';
import { LivraisonService } from '../../../services/livraison.service';
import { Transporteur } from '../../../models/livraison.model';

@Component({
  selector: 'app-transporteur-list',
  templateUrl: './transporteur-list.component.html',
  styleUrls: ['./transporteur-list.component.css']
})
export class TransporteurListComponent implements OnInit {
  transporteurs: Transporteur[] = [];
  successMsg = '';
  errorMsg = '';
  loading = false;

  // Formulaire (création + édition)
  showForm = false;
  editMode = false;
  editId: number | null = null;

  form: Transporteur = { nomSociete: '', telephone: '', email: '' };

  constructor(private livraisonService: LivraisonService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.livraisonService.getAllTransporteurs().subscribe({
      next: data => { this.transporteurs = data; this.loading = false; },
      error: () => { this.showError('Erreur chargement transporteurs.'); this.loading = false; }
    });
  }

  openCreate(): void {
    this.editMode = false;
    this.editId = null;
    this.form = { nomSociete: '', telephone: '', email: '' };
    this.showForm = true;
  }

  openEdit(t: Transporteur): void {
    this.editMode = true;
    this.editId = t.id!;
    this.form = { nomSociete: t.nomSociete, telephone: t.telephone || '', email: t.email || '' };
    this.showForm = true;
  }

  submit(): void {
    if (!this.form.nomSociete.trim()) {
      this.showError('Le nom de la société est obligatoire.');
      return;
    }
    if (this.editMode && this.editId) {
      this.livraisonService.updateTransporteur(this.editId, this.form).subscribe({
        next: () => { this.showSuccess('Transporteur mis à jour.'); this.closeForm(); this.load(); },
        error: () => this.showError('Erreur lors de la mise à jour.')
      });
    } else {
      this.livraisonService.createTransporteur(this.form).subscribe({
        next: () => { this.showSuccess('Transporteur créé avec succès.'); this.closeForm(); this.load(); },
        error: () => this.showError('Erreur lors de la création.')
      });
    }
  }

  delete(id: number, nom: string): void {
    if (!confirm(`Supprimer le transporteur "${nom}" ?`)) return;
    this.livraisonService.deleteTransporteur(id).subscribe({
      next: () => { this.showSuccess('Transporteur supprimé.'); this.load(); },
      error: () => this.showError('Erreur lors de la suppression.')
    });
  }

  closeForm(): void {
    this.showForm = false;
    this.form = { nomSociete: '', telephone: '', email: '' };
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3000);
  }
  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 3000);
  }
}