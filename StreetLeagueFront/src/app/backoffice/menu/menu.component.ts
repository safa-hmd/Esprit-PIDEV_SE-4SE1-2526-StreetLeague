import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  @Input() collapsed = false;
  @Input() mobileOpen = false;

  closeMobile(): void {
    this.mobileOpen = false;
  }
}