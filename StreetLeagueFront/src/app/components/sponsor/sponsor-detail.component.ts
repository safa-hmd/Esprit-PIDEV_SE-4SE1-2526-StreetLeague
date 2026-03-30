import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { SponsorDTO } from '../../models/sponsor-dto';

@Component({
  selector: 'app-sponsor-detail',
  templateUrl: './sponsor-detail.component.html',
  styleUrls: ['./sponsor-detail.component.css']
})
export class SponsorDetailComponent implements OnInit {
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

  edit(): void {
    if (this.sponsor) this.router.navigate(['/client/sponsor', this.sponsor.id, 'edit']);
  }

  back(): void { this.router.navigate(['/client/sponsor']); }

  getTypeBadgeClass(type: string): string {
    const t = type?.toLowerCase() || '';
    if (t.includes('or') || t.includes('gold') || t.includes('premium')) return 'badge-gold';
    if (t.includes('silver') || t.includes('argent')) return 'badge-silver';
    return 'badge-bronze';
  }
}
