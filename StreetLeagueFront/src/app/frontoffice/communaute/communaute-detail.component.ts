import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-communaute-detail',
  templateUrl: './communaute-detail.component.html',
  styleUrls: ['./communaute-detail.component.css']
})
export class CommunauteDetailComponent implements OnInit {
  communaute: CommunauteDTO | null = null;
  loading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communauteService: CommunauteService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error = 'Identifiant invalide';
      this.loading = false;
      return;
    }
    this.communauteService.getById(id).subscribe({
      next: (data) => {
        this.communaute = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  goEdit(): void {
    if (this.communaute) {
      this.router.navigate(['/client/communaute', this.communaute.id, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/client/communaute']);
  }
}
