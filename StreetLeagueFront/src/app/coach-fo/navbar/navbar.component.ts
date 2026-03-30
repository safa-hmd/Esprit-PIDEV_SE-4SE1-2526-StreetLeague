import { Component, OnInit, OnDestroy, HostListener, ViewChild, ElementRef } from '@angular/core';
import { NotificationService } from 'src/app/services/notification.service';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  notifications: any[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;
  userId: string | null = null;
  private pollSubscription?: Subscription;

  @ViewChild('notificationContainer') notificationContainer!: ElementRef;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.userId = localStorage.getItem('idUser') || 
                  localStorage.getItem('userId') || 
                  localStorage.getItem('UserIdConnect');
                  
    const numericUserId = parseInt(this.userId || '0');
    console.log('DEBUG [Navbar] userId resolved as integer:', numericUserId);
                  
    if (this.userId) {
      this.loadNotifications();
      // Poll every 15 seconds
      this.pollSubscription = interval(15000).subscribe(() => this.loadNotifications());
    }
  }

  ngOnDestroy() {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }

  loadNotifications() {
    if (!this.userId) return;
    this.notificationService.getMyNotifications(this.userId).subscribe({
      next: (res) => {
        this.notifications = res;
        this.unreadCount = this.notifications.filter(n => !n.read).length;
      },
      error: (err) => console.error('Error loading notifications', err)
    });
  }

  toggleNotifications(event: MouseEvent) {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
  }

  markAsRead(n: any) {
    if (n.read) return;
    this.notificationService.markAsRead(n.id).subscribe({
      next: () => {
        n.read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      }
    });
  }

  markAllAsRead() {
    if (!this.userId) return;
    this.notificationService.markAllAsRead(this.userId).subscribe({
      next: () => {
        this.notifications.forEach(n => n.read = true);
        this.unreadCount = 0;
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (this.notificationContainer && !this.notificationContainer.nativeElement.contains(event.target)) {
      this.showNotifications = false;
    }
  }
}
