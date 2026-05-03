import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      const role = this.authService.getRole();
      if (role === 'ADMIN') {
        this.router.navigate(['/admin/transport']);
      } else if (role === 'COACH') {
        this.router.navigate(['/coach/transport']);
      } else {
        this.router.navigate(['/client']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }
}
