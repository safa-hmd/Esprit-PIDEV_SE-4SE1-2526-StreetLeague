import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-delivery-fo',
  templateUrl: './delivery-fo.component.html',
  styleUrls: ['./delivery-fo.component.css']
})
export class DeliveryFoComponent implements OnInit {
  deliveryName = '';
  sidebarOpen = true;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const email = this.authService.getEmail() || 'Livreur';
    this.deliveryName = email.split('@')[0];
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}