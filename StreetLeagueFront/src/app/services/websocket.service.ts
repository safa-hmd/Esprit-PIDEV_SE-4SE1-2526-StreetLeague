import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client, Message } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';

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

  private readonly base = 'http://localhost:8086/StreetLeague';

  private client: Client;
  private messageSubject: Subject<ChatMessage> = new Subject<ChatMessage>();
  private activeSubscription: any = null;

  private commentSubs: { [postId: number]: any } = {};
  public isConnected = false;

  newPost$ = new Subject<any>();
  updatePost$ = new Subject<any>();
  deletePost$ = new Subject<number>();
  newComment$ = new Subject<any>();
  updateComment$ = new Subject<any>();
  likeUpdate$ = new Subject<{ postId: number, likes: number }>();

  constructor(private http: HttpClient) {
    this.client = new Client({
      // @ts-ignore
      webSocketFactory: () => new SockJS(`${this.base}/ws-chat`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.client.onConnect = () => {
      console.log('✅ WebSocket connected');
      this.isConnected = true;

      this.client.subscribe('/topic/posts', (msg) => {
        try {
          const data = JSON.parse(msg.body);
          if (data.type === 'LIKE_UPDATE') {
            this.likeUpdate$.next({ postId: data.postId, likes: data.likes });
          } else if (data.type === 'NEW_POST') {
            this.newPost$.next(data.post);
          } else if (data.type === 'DELETE_POST') {
            this.deletePost$.next(data.postId);
          }
        } catch (e) {
          console.error('Parse error:', e);
        }
      });
    };

    this.client.onDisconnect = () => {
      console.log('❌ WebSocket disconnected');
      this.isConnected = false;
    };
  }

  // ---- CHAT METHODS ----
  getChatHistory(contractId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.base}/api/chat/history/${contractId}`);
  }

  connect(contractId?: number): Observable<ChatMessage> {
    if (!this.client.active) {
      this.client.activate();
    }

    if (contractId !== undefined) {
      if (this.client.connected) {
        this._subscribeToChat(contractId);
      } else {
        const oldOnConnect = this.client.onConnect;
        this.client.onConnect = (frame) => {
          if (oldOnConnect) oldOnConnect(frame);
          this._subscribeToChat(contractId);
        };
      }
    }
    return this.messageSubject.asObservable();
  }

  private _subscribeToChat(contractId: number) {
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

  sendMessage(contractId: number, message: ChatMessage): void {
    if (this.client.connected) {
      this.client.publish({
        destination: `/app/chat/${contractId}`,
        body: JSON.stringify(message)
      });
    } else {
      console.error('❌ Client WebSocket non connecté! Tentative de relogin...');
      this.client.activate();
    }
  }

  uploadAudio(formData: FormData): Observable<any> {
    return this.http.post(`${this.base}/api/chat/send-audio`, formData);
  }

  deleteMessage(id: number, contractId: number): Observable<any> {
    return this.http.delete(`${this.base}/api/chat/${id}?contratId=${contractId}`);
  }

  reactToMessage(id: number, reaction: string, contractId: number): Observable<any> {
    return this.http.post(`${this.base}/api/chat/${id}/react?reaction=${encodeURIComponent(reaction)}&contratId=${contractId}`, {});
  }

  // ---- POST/COMMENT METHODS ----
  subscribeToComments(postId: number) {
    if (this.commentSubs[postId] || !this.isConnected) return;
    this.commentSubs[postId] = this.client.subscribe(
      `/topic/comments/${postId}`, (msg) => {
        try {
          const data = JSON.parse(msg.body);
          if (data.type === 'NEW_COMMENT') this.newComment$.next({ ...data.comment, postId });
          if (data.type === 'UPDATE_COMMENT') this.updateComment$.next({ ...data.comment, postId });
        } catch (e) { console.error(e); }
      }
    );
  }

  disconnect(): void {
    if (this.activeSubscription) {
      this.activeSubscription.unsubscribe();
      this.activeSubscription = null;
    }
    if (this.client.active) {
      this.client.deactivate();
      this.isConnected = false;
    }
  }
}