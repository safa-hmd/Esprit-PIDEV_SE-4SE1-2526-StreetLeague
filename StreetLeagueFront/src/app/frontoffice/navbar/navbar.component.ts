// src/app/components/navbar/navbar.component.ts
import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { NotificationService } from 'src/app/services/notification.service';
import {
  NotificationResponse,
  detectNotifType,
  notifIcon,
  notifAccent
} from 'src/app/models/notification.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  userName     = '';
  userRole     = '';
  userInitials = '';   // NEW — displayed in avatar circle

  dropdownOpen = false;
  menuOpen     = false;
  notifOpen    = false;

  notifications: NotificationResponse[] = [];
  unreadCount = 0;

  activeFilter: 'all' | 'unread' | 'reminder' | 'closed' = 'all';

  private notifPollSub?: Subscription;

  // Expose helpers to template
  detectNotifType = detectNotifType;
  notifIcon       = notifIcon;
  notifAccent     = notifAccent;

  constructor(
    private router: Router,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.userName     = profile.fullName;
        this.userRole     = profile.role;
        this.userInitials = this.getInitials(profile.fullName);
        localStorage.setItem('userName', profile.fullName);
        localStorage.setItem('userRole', profile.role);
      },
      error: () => {
        this.userName     = localStorage.getItem('userName') ?? 'Player';
        this.userRole     = localStorage.getItem('userRole') ?? '';
        this.userInitials = this.getInitials(this.userName);
      }
    });

    this.loadNotifications();
    this.notifPollSub = interval(30_000).subscribe(() => this.loadNotifications());
  }

  ngOnDestroy(): void {
    this.notifPollSub?.unsubscribe();
  }

  // ── Helpers ───────────────────────────────────────────────────────────
  private getInitials(name: string): string {
    return name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(w => w[0].toUpperCase())
      .join('');
  }

  // ── Notifications ─────────────────────────────────────────────────────
  loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount   = data.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Notification load error', err)
    });
  }

  get filteredNotifications(): NotificationResponse[] {
    switch (this.activeFilter) {
      case 'unread':
        return this.notifications.filter(n => !n.isRead);
      case 'reminder':
        return this.notifications.filter(n => {
          const t = detectNotifType(n.message);
          return t === 'reminder_48h' || t === 'reminder_24h' || t === 'reminder_2h';
        });
      case 'closed':
        return this.notifications.filter(n => detectNotifType(n.message) === 'match_closed');
      default:
        return this.notifications;
    }
  }

  setFilter(f: 'all' | 'unread' | 'reminder' | 'closed'): void {
    this.activeFilter = f;
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.notifOpen) this.dropdownOpen = false;
  }

  markAsRead(n: NotificationResponse, event: Event): void {
    event.stopPropagation();
    if (n.isRead) return;
    this.notificationService.markAsRead(n.idNotification).subscribe({
      next: () => {
        n.isRead = true;
        this.unreadCount = this.notifications.filter(x => !x.isRead).length;
      }
    });
  }

  markAllAsRead(): void {
    this.notifications
      .filter(n => !n.isRead)
      .forEach(n => {
        this.notificationService.markAsRead(n.idNotification).subscribe({
          next: () => {
            n.isRead = true;
            this.unreadCount = this.notifications.filter(x => !x.isRead).length;
          }
        });
      });
  }

  deleteNotification(n: NotificationResponse, event: Event): void {
    event.stopPropagation();
    this.notificationService.deleteNotification(n.idNotification).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(
          x => x.idNotification !== n.idNotification
        );
        this.unreadCount = this.notifications.filter(x => !x.isRead).length;
      }
    });
  }

  onNotificationClick(n: NotificationResponse, event: Event): void {
    event.stopPropagation();

    if (!n.isRead) {
      this.notificationService.markAsRead(n.idNotification).subscribe({
        next: () => {
          n.isRead = true;
          this.unreadCount = this.notifications.filter(x => !x.isRead).length;
        }
      });
    }

    this.notifOpen = false;

    const msg = n.message.toLowerCase();

    if (msg.includes('match accepted') || msg.includes('match rejected') ||
        msg.includes('match updated')  || msg.includes('match cancelled') ||
        msg.includes('new match')      || msg.includes('match scheduled')) {
      this.router.navigate(['/client/team'], { queryParams: { tab: 'matches' } });
    } else if (msg.includes('training')) {
      this.router.navigate(['/client/training']);
    } else if (msg.includes('team')) {
      this.router.navigate(['/client/team']);
    } else {
      this.router.navigate(['/client/home']);
    }
  }

  // ── Account & Menu ────────────────────────────────────────────────────
  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) this.notifOpen = false;
  }

  toggleMenu(): void { this.menuOpen = !this.menuOpen; }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const t = event.target as HTMLElement;
    if (!t.closest('.account-menu')) this.dropdownOpen = false;
    if (!t.closest('.notif-menu'))   this.notifOpen   = false;
  }
}