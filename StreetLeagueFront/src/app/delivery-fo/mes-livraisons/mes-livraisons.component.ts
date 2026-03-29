import { Component, OnInit } from '@angular/core';
import { LivraisonService } from '../../services/livraison.service';
import { AuthService } from '../../services/auth.service';
import { Livraison, LivraisonStatus } from '../../models/livraison.model';

@Component({
  selector: 'app-mes-livraisons',
  templateUrl: './mes-livraisons.component.html',
  styleUrls: ['./mes-livraisons.component.css']
})
export class MesLivraisonsComponent implements OnInit {

  livraisons: Livraison[] = [];
  filteredLivraisons: Livraison[] = [];
  userId = 0;

  activeFilter: LivraisonStatus | 'TOUTES' = 'TOUTES';
  statuts: LivraisonStatus[] = ['PREPAREE', 'EXPEDIEE', 'EN_COURS', 'LIVREE', 'ECHEC'];
  selectedLivraison: Livraison | null = null;

  loading = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private livraisonService: LivraisonService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (id) {
      this.userId = id;
      this.loadLivraisons();
    }
  }

  loadLivraisons(): void {
    this.loading = true;
    this.livraisonService.getAllLivraisons().subscribe({
      next: data => {
        // Le backend retourne livreur comme objet nested { id, fullName, email, role }
        // On supporte les deux cas : livreur.id (nested) ou livreurId (flat)
        this.livraisons = data.filter((l: any) => {
          const livreurId = l.livreur?.id ?? l.livreurId;
          return livreurId === this.userId;
        });
        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        this.showError('Erreur lors du chargement des livraisons.');
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    this.filteredLivraisons = this.activeFilter === 'TOUTES'
      ? [...this.livraisons]
      : this.livraisons.filter(l => l.statut === this.activeFilter);
  }

  setFilter(f: LivraisonStatus | 'TOUTES'): void {
    this.activeFilter = f;
    this.applyFilter();
  }

  updateStatus(livraison: Livraison, newStatut: LivraisonStatus): void {
    if (livraison.statut === newStatut) return;
    const livreurId = (livraison as any).livreur?.id ?? livraison.livreurId;

    this.livraisonService.updateStatus(livraison.id!, {
      commandeId:     livraison.commandeId,
      transporteurId: livraison.transporteurId,
      livreurId:      livreurId,
      adresse:        livraison.adresse,
      fraisLivraison: livraison.fraisLivraison,
      statut:         newStatut
    }).subscribe({
      next: () => {
        livraison.statut = newStatut;
        if (this.selectedLivraison?.id === livraison.id) {
          this.selectedLivraison!.statut = newStatut;
        }
        this.applyFilter();
        this.showSuccess(`Livraison #${livraison.id} → ${newStatut}`);
      },
      error: () => this.showError('Erreur lors de la mise à jour.')
    });
  }

  selectLivraison(l: Livraison): void {
    this.selectedLivraison = this.selectedLivraison?.id === l.id ? null : l;
  }

  get stats() {
    return {
      total:     this.livraisons.length,
      enCours:   this.livraisons.filter(l => l.statut === 'EN_COURS' || l.statut === 'EXPEDIEE').length,
      livrees:   this.livraisons.filter(l => l.statut === 'LIVREE').length,
      echecs:    this.livraisons.filter(l => l.statut === 'ECHEC').length,
      preparees: this.livraisons.filter(l => l.statut === 'PREPAREE').length,
    };
  }

  getBadgeClass(statut: LivraisonStatus): string {
    const map: Record<LivraisonStatus, string> = {
      PREPAREE: 'dfo-badge-orange',
      EXPEDIEE: 'dfo-badge-blue',
      EN_COURS: 'dfo-badge-blue',
      LIVREE:   'dfo-badge-green',
      ECHEC:    'dfo-badge-red'
    };
    return map[statut] || 'dfo-badge-gray';
  }

  getStatusBtnClass(statut: LivraisonStatus, current: LivraisonStatus): string {
    return `dfo-status-btn active-${statut}`;
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3500);
  }
  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 4000);
  }
}