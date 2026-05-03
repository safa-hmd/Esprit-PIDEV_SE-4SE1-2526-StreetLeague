import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-sponsor-communaute-detail',
  templateUrl: './communaute-detail.component.html',
  styleUrls: ['./communaute-detail.component.css']
})
export class SponsorCommunauteDetailComponent implements OnInit {
  communaute: CommunauteDTO | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communauteService: CommunauteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loading = true;
      this.communauteService.getById(Number(id)).subscribe({
        next: (data) => { 
          this.communaute = data; 
          this.loading = false; 
        },
        error: (err) => { 
          this.error = 'Impossible de charger : ' + err.message; 
          this.loading = false; 
        }
      });
    }
  }

  back(): void {
    this.router.navigate(['/sponsor/communaute']);
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit', 
      year: 'numeric'
    });
  }
}
