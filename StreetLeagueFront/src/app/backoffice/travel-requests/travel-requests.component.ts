import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';

@Component({
  selector: 'app-travel-requests',
  templateUrl: './travel-requests.component.html',
  styleUrls: ['./travel-requests.component.css']
})
export class TravelRequestsComponent implements OnInit {
  requests: any[] = [];
  displayModal = false;
  activeRequest: any = null;
  adminComment: string = '';

  constructor(private travelService: TravelService) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.travelService.getAllTravelRequests().subscribe(res => {
      this.requests = res;
    });
  }

  openDecisionModal(r: any) {
    this.activeRequest = r;
    this.adminComment = r.adminComment || '';
    this.displayModal = true;
  }

  closeModal() {
    this.displayModal = false;
    this.activeRequest = null;
  }

  decide(status: string) {
    const payload = {
      status: status,
      adminComment: this.adminComment
    };
    this.travelService.decideTravelRequest(this.activeRequest.id, payload).subscribe(() => {
      this.loadRequests();
      this.closeModal();
    });
  }
}
