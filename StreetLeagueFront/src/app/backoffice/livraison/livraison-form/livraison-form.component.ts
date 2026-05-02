import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LivraisonService } from '../../../services/livraison.service';
import { Livraison, LivraisonStatus, Transporteur } from '../../../models/livraison.model';

export interface DeliveryUser {
  id: number;
  fullName: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-livraison-form',
  templateUrl: './livraison-form.component.html',
  styleUrls: ['./livraison-form.component.css']
})
export class LivraisonFormComponent implements OnInit {
  transporteurs: Transporteur[] = [];
  livreurs: DeliveryUser[] = [];
  statuts: LivraisonStatus[] = ['PREPAREE', 'EXPEDIEE', 'EN_COURS', 'LIVREE', 'ECHEC'];

  form: Livraison = {
    commandeId: 0,
    transporteurId: 0,
    livreurId: undefined,
    adresse: '',
    fraisLivraison: 0,
    statut: 'PREPAREE'
  };

  successMsg = '';
  errorMsg = '';
  loading = false;

  constructor(private livraisonService: LivraisonService, private router: Router) {}

  ngOnInit(): void {
    // Charger les transporteurs
    this.livraisonService.getAllTransporteurs().subscribe({
      next: data => this.transporteurs = data,
      error: () => this.showError('Impossible de charger les transporteurs.')
    });

    // Charger les livreurs (users avec rôle DELIVERY)
    this.livraisonService.getAllDeliveryUsers().subscribe({
      next: data => this.livreurs = data,
      error: () => this.showError('Impossible de charger les livreurs.')
    });
  }

  submit(): void {
    if (!this.form.commandeId || !this.form.transporteurId || !this.form.adresse) {
      this.showError('Veuillez remplir tous les champs obligatoires.');
      return;
    }
    this.loading = true;
    this.livraisonService.createLivraison(this.form).subscribe({
      next: () => {
        this.loading = false;
        this.showSuccess('Livraison créée avec succès !');
        setTimeout(() => this.router.navigate(['/admin/livraisons']), 1500);
      },
      error: () => {
        this.loading = false;
        this.showError('Erreur lors de la création de la livraison.');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/admin/livraisons']);
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
  }
  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
  }
}