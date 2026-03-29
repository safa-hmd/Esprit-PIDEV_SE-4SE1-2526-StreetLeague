import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';

@Component({
  selector: 'app-transport-management',
  templateUrl: './transport-management.component.html',
  styleUrls: ['./transport-management.component.css']
})
export class TransportManagementComponent implements OnInit {
  transports: any[] = [];
  displayModal: boolean = false;
  editing: boolean = false;
  transportDto: any = {};

  constructor(private travelService: TravelService) {}

  ngOnInit() {
    this.loadTransports();
  }

  loadTransports() {
    this.travelService.getTransports().subscribe(res => {
      this.transports = res;
    });
  }

  openModal(t?: any) {
    if (t) {
      this.editing = true;
      this.transportDto = { ...t };
    } else {
      this.editing = false;
      this.transportDto = { type: 'BUS', destination: '', availableSeats: 0, pricePerSeat: 0, departureTime: '', returnTime: '' };
    }
    this.displayModal = true;
  }

  closeModal() {
    this.displayModal = false;
  }

  save() {
    if (this.editing) {
      this.travelService.updateTransport(this.transportDto.id, this.transportDto).subscribe(() => {
        this.loadTransports();
        this.closeModal();
      });
    } else {
      this.travelService.createTransport(this.transportDto).subscribe(() => {
        this.loadTransports();
        this.closeModal();
      });
    }
  }

  delete(id: number) {
    if (confirm('Are you sure you want to delete this transport?')) {
      this.travelService.deleteTransport(id).subscribe(() => {
        this.loadTransports();
      });
    }
  }

  approve(id: number) {
    this.travelService.approveTransport(id).subscribe(() => {
      this.loadTransports();
    });
  }

  reject(id: number) {
    this.travelService.rejectTransport(id).subscribe(() => {
      this.loadTransports();
    });
  }
}
