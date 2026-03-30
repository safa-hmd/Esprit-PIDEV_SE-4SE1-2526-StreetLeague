import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsoringEvenementService } from '../../services/sponsoring-evenement.service';
import { SponsoringEvenementDTO } from '../../models/sponsoring-evenement-dto';

@Component({
  selector: 'app-admin-sponsoring-evenement-detail',
  templateUrl: './admin-sponsoring-evenement-detail.component.html',
  styleUrls: ['./admin-sponsoring-evenement-detail.component.css']
})
export class AdminSponsoringEvenementDetailComponent implements OnInit {
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

  back(): void { this.router.navigate(['/admin/sponsoring-evenement']); }

  getContributionBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('financier')) return 'badge-finance';
    if (t.includes('matériel') || t.includes('materiel')) return 'badge-materiel';
    if (t.includes('service')) return 'badge-service';
    return 'badge-autre';
  }
}
