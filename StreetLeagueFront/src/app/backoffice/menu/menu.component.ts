import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  userRole: string = '';

  constructor() {}

  ngOnInit(): void {
    this.userRole = localStorage.getItem('RoleUserConnect') || '';
    this.userRole = this.userRole.replace('ROLE_', '');
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }

  isCoach(): boolean {
    return this.userRole === 'COACH';
  }
}
