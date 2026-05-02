import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client, Message } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';

if (typeof (window as any).global === 'undefined') {
  (window as any).global = window;
}

export interface ChatMessage {
  id?: number;
  contractSponsorId: number;
  expediteur: string;
  content: string;
  type?: 'TEXT' | 'IMAGE' | 'VOICE' | 'DELETE' | 'REACTION' | string;
  mediaData?: string;
  reaction?: string;
  timestamp?: Date;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private messageSubject: Subject<ChatMessage> = new Subject<ChatMessage>();
  private activeSubscription: any = null;

  constructor(private http: HttpClient) {
    this.client = new Client({
      // @ts-ignore
      webSocketFactory: () => new SockJS(`${environment.baseUrl}/ws-chat`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };
  }

  getChatHistory(contractId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${environment.baseUrl}/api/chat/history/${contractId}`);
  }

  connect(contractId: number): Observable<ChatMessage> {
    if (!this.client.active) {
      this.client.activate();
    }

    // S'abonner après la login
    this.client.onConnect = () => {

      if (this.activeSubscription) {
        this.activeSubscription.unsubscribe();
      }
      this.activeSubscription = this.client.subscribe(`/topic/chat/${contractId}`, (message: Message) => {
        if (message.body) {
          const chatMsg: ChatMessage = JSON.parse(message.body);
          this.messageSubject.next(chatMsg);
        }
      });
    };

    // Si déjà connecté, on s'abonne immédiatement
    if (this.client.connected) {
      if (this.activeSubscription) {
        this.activeSubscription.unsubscribe();
      }
      this.activeSubscription = this.client.subscribe(`/topic/chat/${contractId}`, (message: Message) => {
        if (message.body) {
          const chatMsg: ChatMessage = JSON.parse(message.body);
          this.messageSubject.next(chatMsg);
        }
      });
    }

    return this.messageSubject.asObservable();
  }

  sendMessage(contractId: number, message: ChatMessage): void {





    if (this.client.connected) {
      const destination = `/app/chat/${contractId}`;
      const body = JSON.stringify(message);


      this.client.publish({
        destination: destination,
        body: body
      });

    } else {
      console.error('❌ Client WebSocket no connecté!');
      console.error('❌ Tentative de relogin...');
      
      // Tentative de relogin
      this.client.activate();
    }
  }

  uploadAudio(formData: FormData): Observable<any> {
    return this.http.post(`${environment.baseUrl}/api/chat/send-audio`, formData);
  }

  deleteMessage(id: number, contractId: number): Observable<any> {
    return this.http.delete(`${environment.baseUrl}/api/chat/${id}?contratId=${contractId}`);
  }

  reactToMessage(id: number, reaction: string, contractId: number): Observable<any> {
    return this.http.post(`${environment.baseUrl}/api/chat/${id}/react?reaction=${encodeURIComponent(reaction)}&contratId=${contractId}`, {});
  }

  disconnect(): void {
    if (this.activeSubscription) {
      this.activeSubscription.unsubscribe();
      this.activeSubscription = null;
    }
    if (this.client.active) {
      this.client.deactivate();
    }
  }
}

