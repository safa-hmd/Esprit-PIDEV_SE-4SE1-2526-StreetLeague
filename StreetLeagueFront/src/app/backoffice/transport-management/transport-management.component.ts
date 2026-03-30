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
  showDeleteConfirm: boolean = false;
  transportToDelete: any = null;
  validationError: string = '';

  governorates: string[] = [
    'Tunis', 'Ariana', 'Ben Arous', 'Manouba', 'Bizerte', 'Nabeul', 'Béja', 'Jendouba', 
    'Zaghouan', 'Siliana', 'Kef', 'Kasserine', 'Sidi Bouzid', 'Sousse', 'Monastir', 
    'Mahdia', 'Sfax', 'Kairouan', 'Gafsa', 'Tozeur', 'Kebili', 'Gabès', 'Medenine', 'Tataouine'
  ];

  // KPI Getters
  get activeCount(): number {
    return (this.transports || []).filter(t => t.status === 'APPROVED' || t.status === 'CONFIRMED').length;
  }

  get totalPassengers(): number {
    // Mocking league A & B passengers count based on seats
    return (this.transports || []).reduce((sum, t) => sum + (t.availableSeats || 0), 0);
  }

  get monthlyBudget(): number {
    return (this.transports || []).reduce((sum, t) => sum + (t.pricePerSeat || 0), 0);
  }

  get availability(): number {
    return 94; // Mocked per spec
  }

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
    this.validationError = '';
    
    if (!this.transportDto.departureTime || !this.transportDto.returnTime || !this.transportDto.destination) {
      this.validationError = "All fields are required.";
      return;
    }

    const now = new Date();
    const minDepartureDate = new Date();
    minDepartureDate.setDate(now.getDate() + 3);
    
    const departure = new Date(this.transportDto.departureTime);
    const returnTime = new Date(this.transportDto.returnTime);
    
    if (departure < minDepartureDate) {
      this.validationError = "Departure must be at least 3 days from now.";
      return;
    }
    
    if (returnTime <= departure) {
      this.validationError = "Return date must be later than departure date.";
      return;
    }

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

  delete(transport: any) {
    this.transportToDelete = transport;
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
    this.transportToDelete = null;
  }

  confirmDelete() {
    if (this.transportToDelete) {
      this.travelService.deleteTransport(this.transportToDelete.id).subscribe(() => {
        this.loadTransports();
        this.showDeleteConfirm = false;
        this.transportToDelete = null;
      });
    }
  }

  approve(id: number) {
    this.travelService.approveTransport(id).subscribe(() => {
      this.loadTransports();
      this.downloadPdf(id);
    });
  }

  reject(id: number) {
    this.travelService.rejectTransport(id).subscribe(() => {
      this.loadTransports();
      this.downloadPdf(id);
    });
  }

  private downloadPdf(id: number) {
    this.travelService.downloadTransportPdf(id).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `transport-decision-${id}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
