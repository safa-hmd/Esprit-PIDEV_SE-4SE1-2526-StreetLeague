// src/app/models/notification.model.ts

export type NotifType = 'reminder_48h' | 'reminder_24h' | 'reminder_2h' | 'match_closed' | 'default';

export interface NotificationResponse {
  idNotification: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}

// Utilitaire : détecte le type de notif selon le message
export function detectNotifType(message: string): NotifType {
  if (message.includes('J-2')) return 'reminder_48h';
  if (message.includes('J-1')) return 'reminder_24h';
  if (message.includes('H-2')) return 'reminder_2h';
  if (message.includes('terminé')) return 'match_closed';
  return 'default';
}

export function notifIcon(type: NotifType): string {
  switch (type) {
    case 'reminder_48h': return 'calendar';
    case 'reminder_24h': return 'clock';
    case 'reminder_2h':  return 'alert';
    case 'match_closed': return 'flag';
    default:             return 'bell';
  }
}

export function notifAccent(type: NotifType): string {
  switch (type) {
    case 'reminder_48h': return '#185FA5';   // bleu
    case 'reminder_24h': return '#BA7517';   // amber
    case 'reminder_2h':  return '#A32D2D';   // rouge
    case 'match_closed': return '#0F6E56';   // teal
    default:             return '#5F5E5A';   // gris
  }
}