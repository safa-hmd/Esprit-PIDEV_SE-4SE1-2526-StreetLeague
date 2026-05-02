import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { EvenementCommunauteDTO } from '../../models/evenement-communaute-dto';

@Component({
  selector: 'app-evenement-detail',
  templateUrl: './evenement-detail.component.html',
  styleUrls: ['./evenement-detail.component.css']
})
export class EvenementDetailComponent implements OnInit {
  evenement: EvenementCommunauteDTO | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evenementService: EvenementCommunauteService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadEvenement(id);
    }
  }

  loadEvenement(id: number): void {
    this.loading = true;
    this.error = '';
    this.evenementService.getById(id).subscribe({
      next: (data) => {
        this.evenement = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Event introuvable : ' + err.message;
        this.loading = false;
      }
    });
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

  getDaysUntil(date: Date): number {
    const diff = new Date(date).getTime() - new Date().getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  edit(): void {
    if (this.evenement) {
      this.router.navigate(['/client/evenement', this.evenement.id, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/client/evenement']);
  }
}


