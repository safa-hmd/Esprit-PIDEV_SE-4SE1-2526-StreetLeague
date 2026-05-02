import {
  Component, OnInit, OnDestroy,
  Input, ViewChild, ElementRef, ChangeDetectorRef
} from '@angular/core';
import { Subscription } from 'rxjs';
import { WebSocketService, ChatMessage } from '../../../services/websocket.service';

export interface ModernChatMessage {
  id: string;
  type: 'text' | 'audio';
  text?: string;
  audioUrl?: string;
  audioDuration?: number;    /** 'me' = affiché à DROITE, 'other' = affiché à GAUCHE */
  sender: 'me' | 'other';
  senderName: string;
  senderRole: string;
  timestamp: Date;
  reaction?: string;
  showReactionPicker?: boolean;
  showDeleteConfirm?: boolean;
}

const SESSION_KEY    = 'sl_chat_user';
const ROLE_KEY       = 'sl_chat_role';
const DISP_NAME_KEY  = 'sl_chat_display';

@Component({
  selector: 'app-contract-chat',
  templateUrl: './contract-chat.component.html',
  styleUrls: ['./contract-chat.component.css']
})
export class ContractChatComponent implements OnInit, OnDestroy {
  @Input() contractId!: number;
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  messages: ModernChatMessage[] = [];
  messageText = '';

  /** Identifiant STABLE de cet onglet (persisté en sessionStorage) */
  currentUser = '';      currentRole = '';    // ex: "ADMIN" ou "SPONSOR"
  displayName = '';    
  private chatSubscription!: Subscription;

  // Audio recording
  isRecording = false;
  recordingDuration = 0;
  private recordingTimer: any;
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  private mediaStream: MediaStream | null = null;

  // Audio playback
  currentlyPlayingId: string | null = null;
  private audioElements = new Map<string, HTMLAudioElement>();

  // Emojis & Reactions
  emojis = ['😀', '😂', '😍', '😭', '🙏', '👍', '🔥', '🎉', '❤️', '🤔', '👀', '💯'];
  reactions = ['❤️', '👍', '😂', '😮', '😢', '🙏'];
  showEmojiPicker = false;

  constructor(
    private wsService: WebSocketService,
    private cdr: ChangeDetectorRef
  ) {}

      
  ngOnInit() {
    this.initIdentity();
    this.initMicrophone();
    this.loadHistory();
  }

  ngOnDestroy() {
    if (this.chatSubscription) this.chatSubscription.unsubscribe();
    if (this.recordingTimer) clearInterval(this.recordingTimer);
    if (this.mediaStream) this.mediaStream.getTracks().forEach(t => t.stop());
    this.audioElements.forEach(a => a.pause());
    this.wsService.disconnect();
  }

      
  private initIdentity() {
        let storedUser = sessionStorage.getItem(SESSION_KEY);
    let storedRole = sessionStorage.getItem(ROLE_KEY);

    if (!storedUser || !storedRole) {
            const url = window.location.pathname;
      if (url.includes('/admin/')) {
        storedRole = 'ADMIN';
        storedUser = 'Admin_' + Math.random().toString(36).substr(2, 6);
      } else if (url.includes('/sponsor/')) {
        storedRole = 'SPONSOR';
        storedUser = 'Sponsor_' + Math.random().toString(36).substr(2, 6);
      } else {
        storedRole = 'USER';
        storedUser = 'User_' + Math.random().toString(36).substr(2, 6);
      }
      sessionStorage.setItem(SESSION_KEY, storedUser);
      sessionStorage.setItem(ROLE_KEY, storedRole);
    }

        const storedDisplay = sessionStorage.getItem(DISP_NAME_KEY)
      || (storedRole === 'ADMIN' ? 'Admin' : storedRole === 'SPONSOR' ? 'Sponsor' : 'User');
    sessionStorage.setItem(DISP_NAME_KEY, storedDisplay);
    this.displayName = storedDisplay;

    this.currentUser = storedUser;
    this.currentRole = storedRole;
  }

    // HISTORIQUE & WEBSOCKET
  
  private loadHistory() {
    this.wsService.getChatHistory(this.contractId).subscribe({
      next: (history: ChatMessage[]) => {
        this.messages = history.map(m => this.toModern(m));
        this.connectWS();
      },
      error: () => this.connectWS()
    });
  }

  private connectWS() {
    this.chatSubscription = this.wsService.connect(this.contractId).subscribe((msg: ChatMessage) => {
      
      // Manage la suppression
      if (msg.type === 'DELETE') {
        this.messages = this.messages.filter(m => m.id !== msg.id?.toString());
        this.cdr.detectChanges();
        return;
      }

      // Manage les réactions
      if (msg.type === 'REACTION') {
        const m = this.messages.find(x => x.id === msg.id?.toString());
        if (m) {
          let decR = msg.reaction || '';
          if (decR.startsWith('ENC:')) {
            try { decR = decodeURIComponent(decR.substring(4)); } catch (e) {}
          }
          m.reaction = decR;
          this.cdr.detectChanges();
        }
        return;
      }

      const isMe = msg.expediteur === this.currentUser;

      if (isMe) {
                if (this.messages.some(m => m.id === msg.id?.toString())) {
          return; // Doublon ignoré
        }

                if (msg.type === 'TEXT') {
          let decodedContent = msg.content || '';
          if (decodedContent.startsWith('ENC:')) {
            try { decodedContent = decodeURIComponent(decodedContent.substring(4)); } catch (e) {}
          }

          const localMatch = this.messages.find(m =>
            m.sender === 'me' &&
            m.type === 'text' &&
            m.text === decodedContent &&
            m.id.length >= 12           );

          if (localMatch) {
            localMatch.id = msg.id!.toString(); // Updated avec l'ID officiel
            return; // Doublon ignoré
          }
        }
      }

      // New message
      this.messages.push(this.toModern(msg));
      this.scrollToBottom();
      this.cdr.detectChanges();
    });
  }

    // CONVERSION BACKEND → FRONTEND
  
  private toModern(msg: ChatMessage): ModernChatMessage {
    const isVoice = msg.type === 'VOICE' || (!!msg.mediaData && msg.mediaData.startsWith('data:audio'));
    const isMe = msg.expediteur === this.currentUser;

    let dispName = msg.expediteur || '';
    if (dispName.startsWith('Admin_')) dispName = 'Admin';
    else if (dispName.startsWith('Sponsor_')) dispName = 'Sponsor';
    else if (dispName.startsWith('User_')) dispName = 'User';

        const parsedDuration = isVoice && msg.content ? parseInt(msg.content, 10) : 0;

    let decodedText = msg.content || '';
    if (!isVoice && decodedText.startsWith('ENC:')) {
      try {
        decodedText = decodeURIComponent(decodedText.substring(4));
      } catch (e) {
        decodedText = msg.content || ''; // Fallback en cas d'erreur
      }
    }

    let decodedReaction = msg.reaction || '';
    if (decodedReaction.startsWith('ENC:')) {
      try {
        decodedReaction = decodeURIComponent(decodedReaction.substring(4));
      } catch (e) {
        decodedReaction = msg.reaction || '';
      }
    }

    const modern: ModernChatMessage = {
      id: msg.id?.toString() || Math.random().toString(),
      type: isVoice ? 'audio' : 'text',
      text: !isVoice ? decodedText : undefined,
      audioUrl: isVoice ? msg.mediaData : undefined,
      audioDuration: isFinite(parsedDuration) && parsedDuration > 0 ? parsedDuration : 0,
      sender: isMe ? 'me' : 'other',
      senderName: dispName,
      senderRole: msg.role || '',
      timestamp: msg.timestamp ? new Date(msg.timestamp) : new Date(),
      reaction: decodedReaction,
      showReactionPicker: false,
      showDeleteConfirm: false
    };

    return modern;
  }

      
  sendTextMessage() {
    const text = this.messageText.trim();
    if (!text) return;

    // 1. Affichage local immédiat
    const localId = Date.now().toString();
    this.messages.push({
      id: localId,
      type: 'text',
      text,
      sender: 'me',
      senderName: this.displayName,  // Name propre (Admin/Sponsor)
      senderRole: this.currentRole,
      timestamp: new Date()
    });
    this.messageText = '';
    this.scrollToBottom();

            this.wsService.sendMessage(this.contractId, {
      contractSponsorId: this.contractId,
      expediteur: this.currentUser,
      role: this.currentRole,
      content: 'ENC:' + encodeURIComponent(text),
      type: 'TEXT',
      timestamp: new Date()
    });
  }

      
  async initMicrophone() {
    try {
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ audio: true });
    } catch (e) {
      console.warn('Microphone no disponible:', e);
    }
  }

  startRecording(event: Event) {
    event.preventDefault();
    if (!this.mediaStream || this.isRecording) return;

    this.audioChunks = [];
    this.isRecording = true;
    this.recordingDuration = 0;

    this.mediaRecorder = new MediaRecorder(this.mediaStream);
    this.mediaRecorder.ondataavailable = (e: BlobEvent) => {
      if (e.data.size > 0) this.audioChunks.push(e.data);
    };
    this.mediaRecorder.onstop = () => this.finalizeRecording();
    this.mediaRecorder.start(100); // slice all 100ms

    this.recordingTimer = setInterval(() => {
      this.recordingDuration++;
      this.cdr.detectChanges();
    }, 1000);
  }

  stopRecording() {
    if (!this.isRecording || !this.mediaRecorder) return;
    this.isRecording = false;
    clearInterval(this.recordingTimer);
    this.mediaRecorder.stop();
    this.cdr.detectChanges();
  }

  cancelRecording() {
    this.isRecording = false;
    clearInterval(this.recordingTimer);
    if (this.mediaRecorder) this.mediaRecorder.stop();
    this.audioChunks = [];
    this.cdr.detectChanges();
  }

  private finalizeRecording() {
    if (this.audioChunks.length === 0) return;

    const blob = new Blob(this.audioChunks, { type: 'audio/webm' });
    if (blob.size < 100) return; 
    const duration = this.recordingDuration;
    const localId = Date.now().toString();
    const localUrl = URL.createObjectURL(blob);

        this.messages.push({
      id: localId,
      type: 'audio',
      audioUrl: localUrl,
      audioDuration: duration,
      sender: 'me',
      senderName: this.displayName,
      senderRole: this.currentRole,
      timestamp: new Date()
    });
    this.scrollToBottom();
    this.cdr.detectChanges();

        const formData = new FormData();
    formData.append('file', blob, 'audio.webm');
    formData.append('contractId', String(this.contractId));
    formData.append('expediteur', this.currentUser);
    formData.append('role', this.currentRole);
    formData.append('duration', String(duration)); 
    this.wsService.uploadAudio(formData).subscribe({
      next: (response: any) => {
                        const localMsg = this.messages.find(m => m.id === localId);
        if (localMsg && response?.id) {
          localMsg.id = String(response.id);
        }
      },
      error: (err: any) => console.error('Audio upload failed:', err)
    });

    this.audioChunks = [];
  }

      
  toggleAudio(msg: ModernChatMessage) {
    if (!msg.audioUrl) return;

    if (this.currentlyPlayingId === msg.id) {
      this.stopCurrentAudio();
      return;
    }

    this.stopCurrentAudio();
    const audio = new Audio(msg.audioUrl);
    this.audioElements.set(msg.id, audio);
    this.currentlyPlayingId = msg.id;
    audio.play().catch(() => this.stopCurrentAudio());
    audio.onended = () => {
      this.currentlyPlayingId = null;
      this.audioElements.delete(msg.id);
      this.cdr.detectChanges();
    };
    this.cdr.detectChanges();
  }

  private stopCurrentAudio() {
    if (this.currentlyPlayingId) {
      const a = this.audioElements.get(this.currentlyPlayingId);
      if (a) { a.pause(); a.currentTime = 0; }
      this.audioElements.delete(this.currentlyPlayingId);
      this.currentlyPlayingId = null;
      this.cdr.detectChanges();
    }
  }

      
  addEmoji(emoji: string) {
    this.messageText += emoji;
    this.showEmojiPicker = false;
  }

  toggleEmojiPicker() {
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  deleteMessage(msg: ModernChatMessage) {
    msg.showDeleteConfirm = true;
  }

  confirmDelete(msg: ModernChatMessage) {
    msg.showDeleteConfirm = false;
        this.messages = this.messages.filter(m => m.id !== msg.id);
    this.wsService.deleteMessage(Number(msg.id), this.contractId).subscribe();
  }

  cancelDelete(msg: ModernChatMessage) {
    msg.showDeleteConfirm = false;
  }

  addReaction(msg: ModernChatMessage, reaction: string) {
    msg.showReactionPicker = false;
    msg.reaction = reaction; // Updated instantanée locale
    this.wsService.reactToMessage(Number(msg.id), reaction, this.contractId).subscribe();
  }

      
  formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  formatMessageTime(date: Date): string {
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  getSenderClass(role: string): string {
    const r = (role || '').toUpperCase();
    if (r.includes('ADMIN')) return 'sender-admin';
    if (r.includes('SPONSOR')) return 'sender-sponsor';
    return '';
  }

  private scrollToBottom() {
    setTimeout(() => {
      try {
        if (this.scrollContainer && this.scrollContainer.nativeElement) {
          const el = this.scrollContainer.nativeElement;
          el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
        }
      } catch (_) {}
    }, 150);
  }
}



