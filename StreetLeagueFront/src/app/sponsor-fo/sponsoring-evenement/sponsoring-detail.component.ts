import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';

@Component({
  selector: 'app-sponsoring-detail',
  templateUrl: './sponsoring-detail.component.html',
  styleUrls: ['./sponsoring-detail.component.css']
})
export class SponsoringDetailComponent implements OnInit {
  sponsoring: SponsoringEvenementDTO | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sponsoringService: SponsoringEvenementService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loading = true;
      this.sponsoringService.getById(id).subscribe({
        next: (data) => { this.sponsoring = data; this.loading = false; },
        error: (err) => { this.error = 'Impossible de charger : ' + err.message; this.loading = false; }
      });
    }
  }

  edit(): void {
    if (this.sponsoring) this.router.navigate(['/sponsor/sponsoring-evenement', this.sponsoring.id, 'edit']);
  }

  back(): void { this.router.navigate(['/sponsor/sponsoring-evenement']); }

  getContribBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }
}
