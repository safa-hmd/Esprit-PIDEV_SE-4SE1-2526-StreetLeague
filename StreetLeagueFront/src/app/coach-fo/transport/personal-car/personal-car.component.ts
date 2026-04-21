import { Component } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-personal-car',
  templateUrl: './personal-car.component.html',
  styleUrls: ['./personal-car.component.css']
})
export class PersonalCarComponent {
  carDto: any = {
    type: 'PRIVATE_CAR',
    destination: '',
    availableSeats: 0,
    pricePerSeat: 0,
    departureTime: '',
    returnTime: ''
  };

  constructor(private travelService: TravelService, private router: Router) {}

  submit() {
    this.travelService.submitPersonalCar(this.carDto).subscribe({
      next: () => {
        alert('Personal car submitted! Awaiting admin approval/validation.');
        this.router.navigate(['/coach/transport']);
      },
      error: (e) => alert('Error submitting personal car')
    });
  }
}
