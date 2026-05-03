import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { WebSocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.css']
})
export class NewsComponent implements OnInit, AfterViewInit, OnDestroy {

  posts: any[] = [];
  page = 0;
  size = 5;
  totalPages = 0;
  isLastPage = false;
  isLoading = false;

  private observer!: IntersectionObserver;
  @ViewChild('sentinel') sentinel!: ElementRef;

  searchKeyword = '';
  searchCategory = '';
  searchSort = 'date';
  isSearching = false;

  newComment: { [postId: number]: string } = {};
  editingComment: any = null;
  editCommentContent = '';
  showEditCommentForm = false;
  showForm = false;
  selectedPost: any = null;
  selectedPostId: number = 0;

  showCommentError: { [postId: number]: boolean } = {};
  commentErrorMessage: { [postId: number]: string } = {};
  showEditError = false;
  editErrorMessage = '';

  likeInProgress: { [postId: number]: boolean } = {};

  private wsSubscription!: Subscription;

  constructor(
    private postService: PostService,
   private cdr: ChangeDetectorRef,

    private commentService: CommentService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit() {
    this.loadPosts();
    this.webSocketService.connect();
    this.setupWebSocketSubscriptions();
  }

  ngAfterViewInit() {
    this.observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && !this.isLoading && !this.isLastPage) {
        this.loadMore();
      }
    }, { threshold: 0.1 });
    this.observer.observe(this.sentinel.nativeElement);
  }

  ngOnDestroy() {
    if (this.observer) this.observer.disconnect();
    if (this.wsSubscription) this.wsSubscription.unsubscribe();
    this.webSocketService.disconnect();
  }

  loadMore() {
    this.page++;
    if (this.isSearching) {
      this.loadSearchResults();
    } else {
      this.loadPosts();
    }
  }

  loadPosts() {
    this.isLoading = true;
    this.postService.getAllPosts(this.page, this.size).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);
        const mapped = list.map((p: any) => ({
          ...p,
          comments: [],
          showComments: false,
          liked: p.liked || false,
          likes: p.likes || 0,
          commentCount: p.commentCount ?? 0
        }));
        console.log("mapped posts:", mapped);
        
        if (this.page === 0) {
          this.posts = mapped;
        } else {
          this.posts = [...this.posts, ...mapped];
        }
        this.isLastPage = data.last || false;
        this.totalPages = data.totalPages || 1;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.isLoading = false;
      }
    });
  }

  onSearch() {
    this.page = 0;
    this.posts = [];
    this.isLastPage = false;
    const hasKeyword = this.searchKeyword.trim().length > 0;
    const hasCategory = this.searchCategory.trim().length > 0;
    if (hasKeyword || hasCategory) {
      this.isSearching = true;
      this.loadSearchResults();
    } else {
      this.isSearching = false;
      this.loadPosts();
    }
  }

  loadSearchResults() {
    this.isLoading = true;
    this.postService.searchPosts(
      this.searchKeyword,
      this.searchCategory,
      this.searchSort,
      this.page,
      this.size
    ).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);
        const mapped = list.map((p: any) => ({
          ...p,
          comments: [],
          showComments: false,
          liked: p.liked || false,
          likes: p.likes || 0,
          commentCount: p.commentCount ?? 0
        }));
        if (this.page === 0) {
          this.posts = mapped;
        } else {
          this.posts = [...this.posts, ...mapped];
        }
        this.isLastPage = data.last || false;
        this.totalPages = data.totalPages || 1;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error searching posts:', err);
        this.isLoading = false;
      }
    });
  }

  onSortChange() {
    this.page = 0;
    this.posts = [];
    this.isLastPage = false;
    if (this.isSearching) {
      this.loadSearchResults();
    } else {
      this.loadPosts();
    }
  }

  clearSearch() {
    this.searchKeyword = '';
    this.searchCategory = '';
    this.searchSort = 'date';
    this.isSearching = false;
    this.page = 0;
    this.posts = [];
    this.isLastPage = false;
    this.loadPosts();
  }

  toggleLike(post: any) {
    
    // ✅ FIX 1: prevent double-click
    if (this.likeInProgress[post.id]) return;

    // ✅ FIX 2: save original state for rollback
    const previousLiked = post.liked;
    const previousLikes = post.likes;

    // ✅ FIX 3: optimistic update
    post.liked = !post.liked;
    post.likes = post.liked
      ? (previousLikes || 0) + 1
      : Math.max(0, (previousLikes || 1) - 1);

    this.likeInProgress[post.id] = true;

    // ✅ FIX 4: use previousLiked to decide API call (not post.liked after mutation)
    const apiCall = previousLiked
      ? this.postService.dislikePost(post.id)
      : this.postService.likePost(post.id);

    apiCall.subscribe({
      next: (data) => {
        console.log('Server response:', data);
        console.log('post.liked before:', post.liked);
        
        // ✅ FIX 5: server truth wins - update both likes count AND liked status
        // Handle both possible response formats: { likes, liked } or { post: { likes, liked } }
        const likes = data.likes !== undefined ? data.likes : (data.post?.likes || previousLikes);
        const liked = data.liked !== undefined ? data.liked : (data.post?.liked !== undefined ? data.post.liked : !previousLiked);
        
        console.log('Setting post.liked to:', liked);
        console.log('Setting post.likes to:', likes);
        
        post.likes = likes;
        post.liked = liked;
        this.likeInProgress[post.id] = false;
        
        console.log('post.liked after:', post.liked);
      },
      error: (err) => {
        // ✅ FIX 6: rollback on error
        console.error('Like/dislike error:', err);
        post.liked = previousLiked;
        post.likes = previousLikes;
        this.likeInProgress[post.id] = false;
      }
    });
  }

  setupWebSocketSubscriptions() {
    this.wsSubscription = this.webSocketService.likeUpdate$.subscribe((update) => {
      const post = this.posts.find(p => p.id === update.postId);
      if (post && !this.likeInProgress[post.id]) {
        // ✅ FIX 7: only update likes count from WebSocket, NEVER touch post.liked
        // post.liked is per-user state, WebSocket is global broadcast
        post.likes = update.likes;
      }
    });
  }

  toggleComments(post: any) {
    post.showComments = !post.showComments;
    if (post.showComments) {
      this.loadComments(post);
      this.webSocketService.subscribeToComments(post.id);
    }
  }

  loadComments(post: any) {
    this.commentService.getCommentsByPost(post.id).subscribe({
      next: (data) => {
        post.comments = data;
        post.commentCount = data.length;
        // ✅ Force change detection by creating new reference
        this.posts = [...this.posts];
      },
      error: (err) => console.error('Error loading comments:', err)
    });
  }

  addComment(post: any) {
    const content = this.newComment[post.id]?.trim();
    if (!content) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment cannot be empty';
      setTimeout(() => { this.showCommentError[post.id] = false; }, 5000);
      return;
    }
    if (content.length < 2) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment must be at least 2 characters';
      setTimeout(() => { this.showCommentError[post.id] = false; }, 5000);
      return;
    }
    if (content.length > 500) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment must not exceed 500 characters';
      setTimeout(() => { this.showCommentError[post.id] = false; }, 5000);
      return;
    }
    this.commentService.addComment({ content, postId: post.id }).subscribe({
      next: (response) => {
        this.newComment[post.id] = '';
        this.showCommentError[post.id] = false;
        
        // ✅ Update comment count immediately if server returns it
        if (response && response.commentCount !== undefined) {
          post.commentCount = response.commentCount;
        } else {
          // Otherwise reload all comments
          this.loadComments(post);
        }
        
        // Force change detection
        this.posts = [...this.posts];
        this.cdr.detectChanges();
      
      },
      error: (err) => {
        this.showCommentError[post.id] = true;
        this.commentErrorMessage[post.id] = 'Échec de l\'ajout du commentaire. Veuillez réessayer.';
        console.error('Error adding comment:', err);
      }
    });
    
  }

  openEditComment(comment: any, post: any) {
    this.editingComment = comment;
    this.editCommentContent = comment.content;
    this.selectedPost = post;
    this.showEditCommentForm = true;
  }

  saveEditComment(post: any) {
    const content = this.editCommentContent?.trim();
    if (!content) {
      this.showEditError = true;
      this.editErrorMessage = 'Comment cannot be empty';
      setTimeout(() => { this.showEditError = false; }, 5000);
      return;
    }
    if (content.length < 2) {
      this.showEditError = true;
      this.editErrorMessage = 'Comment must be at least 2 characters';
      setTimeout(() => { this.showEditError = false; }, 5000);
      return;
    }
    if (content.length > 500) {
      this.showEditError = true;
      this.editErrorMessage = 'Comment must not exceed 500 characters';
      setTimeout(() => { this.showEditError = false; }, 5000);
      return;
    }
    this.commentService.updateComment(this.editingComment.id, {
      content: this.editCommentContent,
      postId: post.id
    }).subscribe({
      next: () => {
        this.showEditCommentForm = false;
        this.editingComment = null;
        this.editCommentContent = '';
        this.showEditError = false;
        this.loadComments(post);
      },
      error: (err) => {
        this.showEditError = true;
        this.editErrorMessage = 'Failed to update comment. Please try again.';
        console.error(err);
      }
    });
  }

  deleteComment(commentId: number, post: any) {
    this.commentService.deleteComment(commentId).subscribe({
      next: () => this.loadComments(post),
      error: (err) => console.error(err)
    });
  }

  getInitials(name: string): string {
    return name?.split(' ').map(n => n[0]).join('').toUpperCase() || '??';
  }

  getImageUrl(post: any): string {
    return post.imageUrl || 'assets/placeholder.png';
  }

  getCategoryStyle(category: string): string {
    const styles: { [key: string]: string } = {
      'Event': 'background:#FEE2E2;color:#E61920;border:1.5px solid #FECACA;',
      'Health': 'background:#DCFCE7;color:#16A34A;border:1.5px solid #BBF7D0;',
      'Celebration': 'background:#D1D5C8;color:#4B5320;border:1.5px solid #A3A58A;',
      'Entertainment': 'background:#EDE9FE;color:#7C3AED;border:1.5px solid #DDD6FE;',
      'Tournament': 'background:#DBEAFE;color:#2563EB;border:1.5px solid #BFDBFE;',
      'Promotion': 'background:#FEF9C3;color:#CA8A04;border:1.5px solid #FEF08A;',
    };
    return (styles[category] || 'background:#F3F4F6;color:#6B7280;border:1.5px solid #E5E7EB;')
         + 'padding:3px 12px;border-radius:999px;font-size:0.75rem;font-weight:700;white-space:nowrap;';
  }

  getTimeAgo(item: any): string {
    const dateStr = item.updatedAt || item.createdAt;
    const isEdited = item.updatedAt && item.updatedAt !== item.createdAt;
    const now = new Date();
    const date = new Date(dateStr);
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    let timeStr = '';
    if (seconds < 60) timeStr = `il y a ${seconds} sec`;
    else {
      const minutes = Math.floor(seconds / 60);
      if (minutes < 60) timeStr = `il y a ${minutes} min`;
      else {
        const hours = Math.floor(minutes / 60);
        if (hours < 24) timeStr = `il y a ${hours}h`;
        else {
          const days = Math.floor(hours / 24);
          if (days < 7) timeStr = `il y a ${days} j`;
          else {
            const weeks = Math.floor(days / 7);
            if (weeks < 4) timeStr = `il y a ${weeks} sem`;
            else {
              const months = Math.floor(days / 30);
              if (months < 12) timeStr = `il y a ${months} mois`;
              else timeStr = `il y a ${Math.floor(days / 365)} an(s)`;
            }
          }
        }
      }
    }
    return isEdited ? `${timeStr} · modifié` : timeStr;
  }

  trackComment(index: number, comment: any): number {
    return comment.id;
  }
}