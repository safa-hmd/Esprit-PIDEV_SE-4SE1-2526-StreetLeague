import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { EvenementCommunauteDTO } from '../../models/evenement-communaute-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-evenement-list',
  templateUrl: './evenement-list.component.html',
  styleUrls: ['./evenement-list.component.css']
})
export class EvenementListComponent implements OnInit {
  evenements: EvenementCommunauteDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(
    private evenementService: EvenementCommunauteService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadEvenements();
  }

  loadEvenements(): void {
    this.loading = true;
    this.error = '';
    this.evenementService.getAll().subscribe({
      next: (data) => {
        this.evenements = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des events : ' + err.message;
        this.loading = false;
      }
    });
  }

  delete(id: number): void {
    if (!confirm('Are you sure de vouloir delete cet event ?')) return;
    this.loading = true;
    this.error = '';
    this.evenementService.delete(id).subscribe({
      next: () => {
        this.evenements = this.evenements.filter(e => e.id !== id);
        this.successMessage = 'Event supprimé avec succès';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = 'Erreur lors de la suppression : ' + err.message;
        this.loading = false;
      }
    });
  }

  createNew(): void {
    this.router.navigate(['/client/evenement/new']);
  }

  viewDetail(id: number): void {
    this.router.navigate(['/client/evenement', id]);
  }

  editEvenement(id: number): void {
    this.router.navigate(['/client/evenement', id, 'edit']);
  }

  // Méthodes pour vérifier les permissions ÉVÉNEMENTS - Réservé aux clients (ROLE_PLAYER)
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  isUpcoming(date: Date): boolean {
    return new Date(date) >= new Date();
  }

  getTypeIcon(titre: string): string {
    const t = titre.toLowerCase();
    if (t.includes('tournoi') || t.includes('tournament')) return '🏆';
    if (t.includes('charity') || t.includes('social')) return '❤️';
    if (t.includes('basket') || t.includes('foot')) return '🏀';
    if (t.includes('training') || t.includes('entrainement') || t.includes('entraîn')) return '💪';
    return '📅';
  }
}


