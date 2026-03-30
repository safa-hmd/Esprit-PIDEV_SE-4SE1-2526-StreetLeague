import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-oauth2-callback',
  template: `
    <div style="display:flex;justify-content:center;align-items:center;height:100vh">
      <p>Connexion en cours...</p>
    </div>
  `
})
export class OAuth2CallbackComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const email = params['email'];
      const role  = params['role'];
      const id    = params['id'];

      if (token) {
        // Sauvegarder exactement comme le login normal
        localStorage.setItem('TokenUserConnect', token);
        localStorage.setItem('EmailUserConnect', email);
        localStorage.setItem('RoleUserConnect',  role);
        localStorage.setItem('UserIdConnect',    id);

        // Rediriger selon le rôle
        this.redirectByRole(role);
      } else {
        this.router.navigateByUrl('/login');
      }
    });
  }

  private redirectByRole(role: string): void {
    switch (role) {
      case 'ROLE_ADMIN':    this.router.navigateByUrl('/admin');  break;
      case 'ROLE_COACH':    this.router.navigateByUrl('/coach');  break;
      default:              this.router.navigateByUrl('/client'); break;
    }
  }
}