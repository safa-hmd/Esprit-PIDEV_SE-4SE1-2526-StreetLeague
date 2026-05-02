import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  private client!: Client;
  private commentSubs: { [postId: number]: any } = {};
  private isConnected = false;

  newPost$ = new Subject<any>();
  updatePost$ = new Subject<any>();
  deletePost$ = new Subject<number>();
  newComment$ = new Subject<any>();
  updateComment$ = new Subject<any>();
  likeUpdate$ = new Subject<{ postId: number, likes: number }>();

  connect() {
    if (this.isConnected) return;

    this.client = new Client({
      brokerURL: 'ws://localhost:8086/StreetLeague/ws/websocket',
      reconnectDelay: 5000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 20000,
      onConnect: () => {
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
      },
      onDisconnect: () => {
        console.log('❌ WebSocket disconnected');
        this.isConnected = false;
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      }
    });

    this.client.activate();
  }

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

  disconnect() {
    if (this.client?.active) {
      this.client.deactivate();
      this.isConnected = false;
    }
  }
}