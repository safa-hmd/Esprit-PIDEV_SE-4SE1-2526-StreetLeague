import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';

@Component({
  selector: 'app-my-requests',
  templateUrl: './my-requests.component.html',
  styleUrls: ['./my-requests.component.css']
})
export class MyRequestsComponent implements OnInit {
  requests: any[] = [];

  constructor(private travelService: TravelService) {}

  ngOnInit() {
    const coachId = localStorage.getItem('UserIdConnect');
    if (!coachId) {
        return; // Handled gracefully by UI (stays empty)
    }
    this.travelService.getMyTravelRequests(coachId).subscribe({
      next: (res) => {
        this.requests = res || [];
      },
      error: (err) => {
        console.error('Failed to load requests:', err);
      }
    });
  }

  getStatusClass(status: string) {
    if (status === 'APPROVED') return 'badge-success';
    if (status === 'REJECTED') return 'badge-danger';
    return 'badge-warning';
  }

  formatDate(val: string): string {
    if (!val) return '-';
    return new Date(val).toLocaleDateString();
  }
}
