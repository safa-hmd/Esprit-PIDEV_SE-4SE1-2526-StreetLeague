import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sponsor-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class SponsorNavbarComponent {
  userName: string = '';
  isMobileMenuOpen: boolean = false;
  currentRoute: string = '';

  constructor(private router: Router, private authService: AuthService) {
    this.userName = this.authService.getName() || 'Sponsor';
    this.currentRoute = this.router.url;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.isMobileMenuOpen = false;
  }

  navigateToPage(route: string): void {
    console.log('Navigation vers:', route);
    this.router.navigate([route]);
  }

  navigateToMobile(route: string): void {
    console.log('Navigation mobile vers:', route);
    this.router.navigate([route]);
    this.isMobileMenuOpen = false;
  }

  isActive(route: string): boolean {
    return this.router.url === route || this.router.url.startsWith(route + '/');
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }
}
