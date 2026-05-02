import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EvenementCommunauteService } from '../../services/evenement-communaute.service';
import { EvenementCommunauteDTO } from '../../models/evenement-communaute-dto';

@Component({
  selector: 'app-admin-evenement-list',
  templateUrl: './admin-evenement-list.component.html',
  styleUrls: ['./admin-evenement-list.component.css']
})
export class AdminEvenementListComponent implements OnInit {
  evenements: EvenementCommunauteDTO[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(private evenementService: EvenementCommunauteService, private router: Router) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.loading = true;
    this.evenementService.getAll().subscribe({
      next: (data) => { this.evenements = data; this.loading = false; },
      error: (err) => { this.error = 'Erreur chargement : ' + err.message; this.loading = false; }
    });
  }

  createNew(): void { this.router.navigate(['/admin/evenement/new']); }
  edit(id: number): void { this.router.navigate(['/admin/evenement', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete cet event ?')) return;
    this.loading = true;
    this.evenementService.delete(id).subscribe({
      next: () => {
        this.evenements = this.evenements.filter(e => e.id !== id);
        this.successMessage = 'Event supprimé.';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { this.error = 'Erreur suppression : ' + err.message; this.loading = false; }
    });
  }
}
