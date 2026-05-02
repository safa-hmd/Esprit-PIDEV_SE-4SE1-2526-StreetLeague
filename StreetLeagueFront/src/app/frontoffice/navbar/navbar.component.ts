import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  userName: string = 'Utilisateur';
  userRole: string = 'PLAYER';
  dropdownOpen: boolean = false;
  menuOpen: boolean = false;

  constructor() {}

  ngOnInit(): void {
    // Initialisation sans dépendance à AuthService
    console.log('Navbar component chargé');
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  logout(): void {
    // Redirection simple vers la page de login
    window.location.href = '/';
  }
}


