import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  userName: string = '';
  userRole: string = '';
  dropdownOpen: boolean = false;
  menuOpen: boolean = false;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.userName = profile.fullName;
        this.userRole = profile.role;
        // Mettre à jour le localStorage aussi
        localStorage.setItem('userName', profile.fullName);
        localStorage.setItem('userRole', profile.role);
      },
      error: () => {
        // Fallback sur le localStorage si l'appel échoue
        this.userName = localStorage.getItem('userName') ?? 'Player';
        this.userRole = localStorage.getItem('userRole') ?? '';
      }
    });
  }

  toggleDropdown(): void { this.dropdownOpen = !this.dropdownOpen; }
  toggleMenu(): void { this.menuOpen = !this.menuOpen; }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.account-menu')) this.dropdownOpen = false;
  }
}