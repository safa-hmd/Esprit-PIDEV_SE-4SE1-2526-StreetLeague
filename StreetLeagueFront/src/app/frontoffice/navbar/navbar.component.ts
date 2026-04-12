import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { NotificationService } from 'src/app/services/notification.service';
import { NotificationResponse } from 'src/app/models/notification.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  userName: string = '';
  userRole: string = '';
  dropdownOpen: boolean = false;
  menuOpen: boolean = false;
  notifOpen: boolean = false;
  notifications: NotificationResponse[] = [];
  unreadCount: number = 0;

  private notifPollSub?: Subscription;

  constructor(
    private router: Router,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.userName = profile.fullName;
        this.userRole = profile.role;
        localStorage.setItem('userName', profile.fullName);
        localStorage.setItem('userRole', profile.role);
      },
      error: () => {
        this.userName = localStorage.getItem('userName') ?? 'Player';
        this.userRole = localStorage.getItem('userRole') ?? '';
      }
    });

    // Charger immédiatement
    this.loadNotifications();

    // Polling toutes les 30 secondes pour les nouvelles notifications
    this.notifPollSub = interval(30000).subscribe(() => {
      this.loadNotifications();
    });
  }

  ngOnDestroy(): void {
    this.notifPollSub?.unsubscribe();
  }

  loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount = data.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Error loading notifications', err)
    });
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.notifOpen) this.dropdownOpen = false;
  }

  markAsRead(notification: NotificationResponse, event: Event): void {
    event.stopPropagation();
    if (notification.isRead) return;
    this.notificationService.markAsRead(notification.idNotification).subscribe({
      next: () => {
        notification.isRead = true;
        this.unreadCount = this.notifications.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Error marking as read', err)
    });
  }

  markAllAsRead(): void {
    const unread = this.notifications.filter(n => !n.isRead);
    unread.forEach(n => {
      this.notificationService.markAsRead(n.idNotification).subscribe({
        next: () => {
          n.isRead = true;
          this.unreadCount = this.notifications.filter(notif => !notif.isRead).length;
        }
      });
    });
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) this.notifOpen = false;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.account-menu')) this.dropdownOpen = false;
    if (!target.closest('.notif-menu')) this.notifOpen = false;
  }
}