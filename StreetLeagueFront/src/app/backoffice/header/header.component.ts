import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  adminName     = 'Administrator';
  adminEmail    = '';
  adminInitial  = 'A';
  pageTitle     = 'Overview';
  dropdownOpen  = false;
  menuOpen      = false;
  hasNotifications = true;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
      const saved = localStorage.getItem('theme');
  this.isDark = saved ? saved === 'dark' : true;
  document.documentElement.setAttribute('data-theme', this.isDark ? 'dark' : 'light');
    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.adminName    = profile.fullName;
        this.adminEmail   = profile.email;
        this.adminInitial = profile.fullName.charAt(0).toUpperCase();
        localStorage.setItem('userName', profile.fullName);
      },
      error: () => {
        this.adminName    = localStorage.getItem('userName') ?? 'Administrator';
        this.adminEmail   = localStorage.getItem('userEmail') ?? '';
        this.adminInitial = this.adminName.charAt(0).toUpperCase();
      }
    });
  }

  toggleDropdown(): void { this.dropdownOpen = !this.dropdownOpen; }
  toggleMenu(): void     { this.menuOpen = !this.menuOpen; }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/admin-login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.account-wrap')) this.dropdownOpen = false;
  }

  isDark: boolean = true;


toggleTheme(): void {
  this.isDark = !this.isDark;
  const theme = this.isDark ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('theme', theme);
}
}