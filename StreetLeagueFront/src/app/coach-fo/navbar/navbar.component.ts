import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
userName: string = 'Player';
  dropdownOpen: boolean = false;
  menuOpen: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Récupérer le nom depuis le localStorage ou votre AuthService
    const storedName = localStorage.getItem('userName');
    if (storedName) this.userName = storedName;
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  logout(): void {
    localStorage.clear(); // ou appeler votre AuthService
    this.router.navigate(['/login']);
  }

  // Fermer le dropdown en cliquant ailleurs
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.account-menu')) {
      this.dropdownOpen = false;
    }
  }
}