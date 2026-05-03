import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { EvenementCommunauteDTO } from '../../models/evenement-communaute-dto';

@Component({
  selector: 'app-sponsor-evenement-detail',
  templateUrl: './evenement-detail.component.html',
  styleUrls: ['./evenement-detail.component.css']
})
export class SponsorEvenementDetailComponent implements OnInit {
  evenement: EvenementCommunauteDTO | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evenementService: EvenementCommunauteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loading = true;
      this.evenementService.getById(Number(id)).subscribe({
        next: (data) => { 
          this.evenement = data; 
          this.loading = false; 
        },
        error: (err) => { 
          this.error = 'Impossible de charger : ' + err.message; 
          this.loading = false; 
        }
      });
    }
  }

  createSponsoring(): void {
    if (this.evenement) {
      this.router.navigate(['/sponsor/sponsoring-evenement/new'], { 
        queryParams: { evenementId: this.evenement.id } 
      });
    }
  }

  back(): void {
    this.router.navigate(['/sponsor/evenement']);
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
