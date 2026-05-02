import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sponsor-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class SponsorHomeComponent {
  userName: string = '';

  constructor(private router: Router, private authService: AuthService) {
    this.userName = this.authService.getName() || 'Sponsor';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
