import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

type Role = 'PLAYER' | 'COACH' | 'SPONSOR' | 'DELIVERY';

@Component({
  selector: 'app-select-role',
  templateUrl: './select-role.component.html',
  styleUrls: ['./select-role.component.css']
})
export class SelectRoleComponent implements OnInit {

  email    = '';
  fullName = '';
  selectedRole: Role = 'PLAYER';
  isLoading = false;
  errorMessage = '';

  roles: { value: Role; label: string; emoji: string }[] = [
    { value: 'PLAYER',   label: 'Player',    emoji: '👤' },
    { value: 'COACH',    label: 'Coach',     emoji: '🎯' },
    { value: 'SPONSOR',  label: 'Sponsor',   emoji: '💼' },
    { value: 'DELIVERY', label: 'Delivery', emoji: '🚚' },
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.email    = params['email']    || '';
      this.fullName = params['name']     || '';
    });
  }

  selectRole(role: Role): void {
    this.selectedRole = role;
  }

  confirm(): void {
    this.isLoading    = true;
    this.errorMessage = '';

    this.http.post<any>(
      'http://localhost:8086/StreetLeague/auth/complete-google-register',
      {
        email:    this.email,
        fullName: this.fullName,
        role:     this.selectedRole
      }
    ).subscribe({
      next: (response) => {
        this.isLoading = false;

        // Sauvegarder le token
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);
        localStorage.setItem('UserIdConnect',    response.idUser);

        // Rediriger selon le rôle
        switch (response.role) {
          case 'ROLE_ADMIN':    this.router.navigateByUrl('/admin');  break;
          case 'ROLE_COACH':    this.router.navigateByUrl('/coach');  break;
          default:              this.router.navigateByUrl('/client'); break;
        }
      },
      error: () => {
        this.isLoading    = false;
        this.errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
      }
    });
  }
}