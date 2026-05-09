import { Component, OnInit } from '@angular/core';
import { LivraisonService } from '../../../services/livraison.service';
import { Livraison, LivraisonStatus } from '../../../models/livraison.model';

@Component({
  selector: 'app-livraison-list',
  templateUrl: './livraison-list.component.html',
  styleUrls: ['./livraison-list.component.css']
})
export class LivraisonListComponent implements OnInit {

  livraisons: Livraison[] = [];
  filteredLivraisons: Livraison[] = [];

  // ✅ Nouveaux statuts sans EN_COURS
  statuts: LivraisonStatus[] = [
    'PREPAREE', 'ASSIGNEE', 'EXPEDIEE', 'OUT_FOR_DELIVERY', 'LIVREE', 'ECHEC'
  ];

  searchTerm = '';
  successMsg = '';
  errorMsg   = '';
  loading    = false;

  constructor(private livraisonService: LivraisonService) {}

  ngOnInit(): void { this.loadLivraisons(); }

  loadLivraisons(): void {
    this.loading = true;
    this.livraisonService.getAllLivraisons().subscribe({
      next: data => {
        this.livraisons = data;
        this.filteredLivraisons = data;
        this.loading = false;
      },
      error: () => {
        this.showError('Erreur lors du chargement des livraisons.');
        this.loading = false;
      }
    });
  }

  search(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredLivraisons = this.livraisons.filter(l =>
      l.adresse?.toLowerCase().includes(term) ||
      l.id?.toString().includes(term) ||
      l.commandeId?.toString().includes(term)
    );
  }

  updateStatus(livraison: Livraison, newStatut: LivraisonStatus): void {
    this.livraisonService.updateStatus(livraison.id!, {
      commandeId:     livraison.commandeId,
      livreurId:      (livraison as any).livreur?.id ?? livraison.livreurId,
      adresse:        livraison.adresse,
      fraisLivraison: livraison.fraisLivraison,
      statut:         newStatut
    }).subscribe({
      next:  () => { this.showSuccess('Statut mis à jour.'); this.loadLivraisons(); },
      error: () => this.showError('Erreur lors de la mise à jour du statut.')
    });
  }

  // ✅ getBadgeClass avec tous les nouveaux statuts
  getBadgeClass(statut: LivraisonStatus): string {
    const map: Record<LivraisonStatus, string> = {
      PREPAREE:         'a-badge-gray',
      ASSIGNEE:         'a-badge-orange',
      EXPEDIEE:         'a-badge-blue',
      OUT_FOR_DELIVERY: 'a-badge-purple',
      LIVREE:           'a-badge-green',
      ECHEC:            'a-badge-red'
    };
    return map[statut] || 'a-badge-gray';
  }

  // ✅ Stats sans EN_COURS
  get stats() {
    return {
      total:   this.livraisons.length,
      enCours: this.livraisons.filter(l =>
        l.statut === 'ASSIGNEE' ||
        l.statut === 'EXPEDIEE' ||
        l.statut === 'OUT_FOR_DELIVERY'
      ).length,
      livrees: this.livraisons.filter(l => l.statut === 'LIVREE').length,
      echec:   this.livraisons.filter(l => l.statut === 'ECHEC').length,
    };
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