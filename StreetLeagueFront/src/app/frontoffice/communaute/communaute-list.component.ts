import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { AdherenceCommunauteService } from '../../services/adherence-communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';
import { AuthService } from '../../services/auth.service';

interface CommunauteAvecAdhesion extends CommunauteDTO {
  estMembre?: boolean;
}

@Component({
  selector: 'app-communaute-list',
  templateUrl: './communaute-list.component.html',
  styleUrls: ['./communaute-list.component.css']
})
export class CommunauteListComponent implements OnInit {
  communautes: CommunauteAvecAdhesion[] = [];
  userId: number = 0;
  loading = false;
  error = '';
  successMessage = '';
  currentUserRole: string | null = null;

  constructor(
    private communauteService: CommunauteService,
    private adherenceService: AdherenceCommunauteService,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    this.loadUserId();
    this.loadCommunautes();
  }

  loadUserId(): void {
    const userIdStr = localStorage.getItem('UserIdConnect');
    if (userIdStr) {
      this.userId = parseInt(userIdStr);
      return;
    }
    // Fallback : générer un id stable depuis l'email (déjà sauvegardé au login)
    const email = localStorage.getItem('EmailUserConnect') || '';
    if (email) {
      let hash = 0;
      for (let i = 0; i < email.length; i++) {
        hash = ((hash << 5) - hash) + email.charCodeAt(i);
        hash |= 0;
      }
      this.userId = Math.abs(hash);
      localStorage.setItem('UserIdConnect', String(this.userId));
    }
  }

  loadCommunautes(): void {
    this.loading = true;
    this.error = '';

    this.communauteService.getAll().subscribe({
      next: (data) => {
        this.communautes = data;
        // Vérifier pour chaque community si l'utilisateur en est membre
        this.checkMembership();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des communities: ' + err.message;
        this.loading = false;
      }
    });
  }

  checkMembership(): void {
    this.communautes.forEach((communaute) => {
      if (this.userId) {
        this.adherenceService.estMembre(this.userId, communaute.id).subscribe({
          next: (estMembre) => {
            communaute.estMembre = estMembre;
          },
          error: () => {
            // Par défaut, l'utilisateur n'est pas membre
            communaute.estMembre = false;
          }
        });
      }
    });
  }

  join(communauteId: number): void {
    if (!this.userId) {
      this.error = 'Vous devez être connecté pour join une community';
      return;
    }

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    this.adherenceService.join(this.userId, communauteId).subscribe({
      next: () => {
        this.successMessage = 'Vous avez rejoint la community avec succès! 🎉';
        const communaute = this.communautes.find(c => c.id === communauteId);
        if (communaute) {
          communaute.estMembre = true;
        }
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'adhésion: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  leave(communauteId: number): void {
    if (!confirm('Are you sure de vouloir leave cette community?')) {
      return;
    }

    if (!this.userId) {
      this.error = 'Vous devez être connecté';
      return;
    }

    this.loading = true;
    this.error = '';

    this.adherenceService.leave(this.userId, communauteId).subscribe({
      next: () => {
        const communaute = this.communautes.find(c => c.id === communauteId);
        if (communaute) {
          communaute.estMembre = false;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du départ: ' + err.message;
        this.loading = false;
      }
    });
  }

  delete(id: number): void {
    if (!confirm('Are you sure de vouloir delete cette community?')) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.communauteService.delete(id).subscribe({
      next: () => {
        this.communautes = this.communautes.filter(c => c.id !== id);
        this.successMessage = 'Community supprimée avec succès';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = 'Erreur lors de la suppression: ' + err.message;
        this.loading = false;
      }
    });
  }

  createNew(): void {
    this.router.navigate(['/client/communaute/new']);
  }

  viewDetail(id: number): void {
    this.router.navigate(['/client/communaute', id]);
  }

  editCommunaute(id: number): void {
    this.router.navigate(['/client/communaute', id, 'edit']);
  }

  // Méthodes pour vérifier les permissions - Réservé aux clients (ROLE_PLAYER)
  canCreate(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  canEdit(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  canDelete(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  canJoin(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }
}
