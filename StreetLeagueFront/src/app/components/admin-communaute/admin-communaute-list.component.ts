import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommunauteService } from '../../services/communaute.service';
import { CommunauteDTO } from '../../models/communaute-dto';

@Component({
  selector: 'app-admin-communaute-list',
  templateUrl: './admin-communaute-list.component.html',
  styleUrls: ['./admin-communaute-list.component.css']
})
export class AdminCommunauteListComponent implements OnInit {
  communautes: CommunauteDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(private communauteService: CommunauteService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.communauteService.getAll().subscribe({
      next: (data) => { this.communautes = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/admin/communaute/new']); }
  edit(id: number): void { this.router.navigate(['/admin/communaute', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Supprimer cette communauté ?')) return;
    this.loading = true;
    this.communauteService.delete(id).subscribe({
      next: () => {
        this.communautes = this.communautes.filter(c => c.id !== id);
        this.successMessage = 'Communauté supprimée.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }
}
