import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  dropdownOpen: boolean = false;
  menuOpen: boolean = false;
  hasNotifications: boolean = true;
  pageTitle: string = 'Overview';
  adminName: string = 'Alice Martin';
  adminEmail: string = 'alice@test.com';
  adminInitial: string = 'A';
  currentUserRole: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.currentUserRole = this.authService.normalizeRole(this.authService.getRole());
    // Update les infos utilisateur selon le rôle
    this.updateUserInfo();
  }

  updateUserInfo(): void {
    const email = this.authService.getEmail();
    const name = this.authService.getName();
    
    if (email) this.adminEmail = email;
    if (name) this.adminName = name;
    if (name) this.adminInitial = name.charAt(0).toUpperCase();
    
    // Adapter le title selon le rôle
    switch(this.currentUserRole) {
      case 'ROLE_PLAYER':
        this.pageTitle = 'Espace Joueur';
        break;
      case 'ROLE_SPONSOR':
        this.pageTitle = 'Espace Sponsor';
        break;
      case 'ROLE_ADMIN':
        this.pageTitle = 'Administration';
        break;
      case 'ROLE_COMMUNITY_MANAGER':
        this.pageTitle = 'Gestion Community';
        break;
      default:
        this.pageTitle = 'Overview';
    }
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  logout(): void {
    this.authService.logout();
  }

  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown')) {
      this.dropdownOpen = false;
    }
  }

  // Méthodes pour vérifier les permissions dans le header
  canCreate(): boolean {
    switch(this.currentUserRole) {
      case 'ROLE_PLAYER':
      case 'ROLE_ADMIN':
      case 'ROLE_COMMUNITY_MANAGER':
        return true;
      default:
        return false;
    }
  }

  canEdit(): boolean {
    return this.canCreate(); // Mêmes permissions que création
  }

  canDelete(): boolean {
    return this.canCreate(); // Mêmes permissions que création
  }

  isPlayer(): boolean {
    return this.currentUserRole === 'ROLE_PLAYER';
  }

  isSponsor(): boolean {
    return this.currentUserRole === 'ROLE_SPONSOR';
  }

  isAdmin(): boolean {
    return this.currentUserRole === 'ROLE_ADMIN';
  }

  isCommunityManager(): boolean {
    return this.currentUserRole === 'ROLE_COMMUNITY_MANAGER';
  }
}
