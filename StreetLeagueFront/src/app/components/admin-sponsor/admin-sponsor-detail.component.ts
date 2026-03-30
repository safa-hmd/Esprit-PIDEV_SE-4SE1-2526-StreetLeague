import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';

@Component({
  selector: 'app-admin-sponsor-detail',
  templateUrl: './admin-sponsor-detail.component.html',
  styleUrls: ['./admin-sponsor-detail.component.css']
})
export class AdminSponsorDetailComponent implements OnInit {
  sponsor: SponsorDTO | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sponsorService: SponsorService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loading = true;
      this.sponsorService.getById(id).subscribe({
        next: (data) => { this.sponsor = data; this.loading = false; },
        error: (err) => { this.error = 'Impossible de charger : ' + err.message; this.loading = false; }
      });
    }
  }

  back(): void { this.router.navigate(['/admin/sponsor']); }

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }
}
