import { Component, OnInit } from '@angular/core';
import { CommandeService, Commande, CommandeStatus } from '../../../services/commande.service';

@Component({
  selector: 'app-commande-list',
  templateUrl: './commande-list.component.html',
  styleUrls: ['./commande-list.component.css']
})
export class CommandeListComponent implements OnInit {

  // ── Données ──────────────────────────────────────────
  commandes: Commande[] = [];
  filteredCommandes: Commande[] = [];

  // ── Détail (modal inline) ─────────────────────────────
  selectedCommande: Commande | null = null;
  showDetail = false;

  // ── Recherche / filtre ───────────────────────────────
  searchId = '';
  filterStatut: CommandeStatus | '' = '';

  // ── Statuts disponibles ──────────────────────────────
  statuts: CommandeStatus[] = ['EN_ATTENTE', 'PREPAREE', 'VALIDEE', 'LIVREE', 'ANNULEE'];

  // ── UI ───────────────────────────────────────────────
  loading = false;
  successMsg = '';
  errorMsg = '';

  constructor(private commandeService: CommandeService) {}

  ngOnInit(): void {
    this.loadCommandes();
  }

  // ── Chargement ───────────────────────────────────────
  loadCommandes(): void {
    this.loading = true;
    this.commandeService.getAll().subscribe({
      next: data => {
        this.commandes = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.showError('Erreur lors du chargement des commandes.');
        this.loading = false;
      }
    });
  }

  // ── Filtres ──────────────────────────────────────────
  applyFilters(): void {
    let result = [...this.commandes];

    if (this.searchId.trim()) {
      result = result.filter(c => c.id.toString().includes(this.searchId.trim()));
    }

    if (this.filterStatut) {
      result = result.filter(c => c.statut === this.filterStatut);
    }

    this.filteredCommandes = result;
  }

  resetFilters(): void {
    this.searchId = '';
    this.filterStatut = '';
    this.applyFilters();
  }

  // ── Détail ───────────────────────────────────────────
  openDetail(commande: Commande): void {
    this.selectedCommande = commande;
    this.showDetail = true;
  }

  closeDetail(): void {
    this.showDetail = false;
    this.selectedCommande = null;
  }

  // ── Update statut (depuis la liste) ─────────────────
  updateStatus(commande: Commande, newStatut: CommandeStatus): void {
    this.commandeService.updateStatus(commande.id, newStatut).subscribe({
      next: updated => {
        commande.statut = updated.statut;
        if (this.selectedCommande?.id === commande.id) {
          this.selectedCommande.statut = updated.statut;
        }
        this.showSuccess(`Commande #${commande.id} → ${newStatut}`);
        this.applyFilters();
      },
      error: () => this.showError('Erreur lors de la mise à jour du statut.')
    });
  }

  // ── Badges ───────────────────────────────────────────
  getBadgeClass(statut: CommandeStatus): string {
    const map: Record<CommandeStatus, string> = {
      EN_ATTENTE: 'a-badge-orange',
      PREPAREE:   'a-badge-blue',
      VALIDEE:    'a-badge-green',
      LIVREE:     'a-badge-gray',
      ANNULEE:    'a-badge-red'
    };
    return map[statut] || 'a-badge-gray';
  }

  // ── Stats ────────────────────────────────────────────
  get stats() {
    return {
      total:      this.commandes.length,
      enAttente:  this.commandes.filter(c => c.statut === 'EN_ATTENTE').length,
      preparees:  this.commandes.filter(c => c.statut === 'PREPAREE').length,
      validees:   this.commandes.filter(c => c.statut === 'VALIDEE').length,
      livrees:    this.commandes.filter(c => c.statut === 'LIVREE').length,
      annulees:   this.commandes.filter(c => c.statut === 'ANNULEE').length,
      chiffreAffaires: this.commandes
        .filter(c => c.statut !== 'ANNULEE')
        .reduce((sum, c) => sum + c.montantTotal, 0)
    };
  }

  // ── Helpers ──────────────────────────────────────────
  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3500);
  }

  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 4000);
  }
}