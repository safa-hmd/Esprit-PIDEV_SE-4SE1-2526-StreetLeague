import { Component, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.css']
})
export class NewsComponent implements OnInit {
  posts: any[] = [];

  // Comments
  newComment: { [postId: number]: string } = {};
  editingComment: any = null;
  editCommentContent = '';
  showEditCommentForm = false;
  showForm = false;
  selectedPost: any = null;
  selectedPostId: number = 0;
  
  // Validation
  showCommentError: { [postId: number]: boolean } = {};
  commentErrorMessage: { [postId: number]: string } = {};
  showEditError = false;
  editErrorMessage = '';
  
  // Like toggle state
  likeInProgress: { [postId: number]: boolean } = {};

  constructor(
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit() {
    this.loadPosts();
  }

  /*loadPosts() {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        this.posts = data.map(p => ({ ...p, comments: [], showComments: false, liked: false }));
      },
      error: (err) => console.error(err)
    });
  }*/

  // ===== LIKE =====
  /*toggleLike(post: any) {
    if (post.liked) {
      post.likes = (post.likes || 1) - 1;
      post.liked = false;
    } else {
      post.likes = (post.likes || 0) + 1;
      post.liked = true;
    }
  }*/

  // ===== COMMENTS =====
  toggleComments(post: any) {
    post.showComments = !post.showComments;
    if (post.showComments && post.comments.length === 0) {
      this.loadComments(post);
    }
  }

  loadComments(post: any) {
    this.commentService.getCommentsByPost(post.id).subscribe({
      next: (data) => post.comments = data,
      error: (err) => console.error(err)
    });
  }

  addComment(post: any) {
    const content = this.newComment[post.id]?.trim();
    
    if (!content) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment cannot be empty';
      setTimeout(() => { 
        this.showCommentError[post.id] = false; 
      }, 5000);
      return;
    }

    if (content.length < 2) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment must be at least 2 characters';
      setTimeout(() => { 
        this.showCommentError[post.id] = false; 
      }, 5000);
      return;
    }

    if (content.length > 500) {
      this.showCommentError[post.id] = true;
      this.commentErrorMessage[post.id] = 'Comment must not exceed 500 characters';
      setTimeout(() => { 
        this.showCommentError[post.id] = false; 
      }, 5000);
      return;
    }

    this.commentService.addComment({ content, postId: post.id }).subscribe({
      next: () => {
        this.newComment[post.id] = '';
        this.showCommentError[post.id] = false;
        this.loadComments(post);
      },
      error: (err) => {
        this.showCommentError[post.id] = true;
        this.commentErrorMessage[post.id] = 'Failed to add comment. Please try again.';
        console.error(err);
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
      setTimeout(() => { 
        this.showEditError = false; 
      }, 5000);
      return;
    }

    if (content.length < 2) {
      this.showEditError = true;
      this.editErrorMessage = 'Comment must be at least 2 characters';
      setTimeout(() => { 
        this.showEditError = false; 
      }, 5000);
      return;
    }

    if (content.length > 500) {
      this.showEditError = true;
      this.editErrorMessage = 'Comment must not exceed 500 characters';
      setTimeout(() => { 
        this.showEditError = false; 
      }, 5000);
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

  loadPosts() {
  console.log('loadPosts called'); 
  this.postService.getAllPosts().subscribe({
    next: (data) => {
      console.log('data:', data); 
      this.posts = data.map(p => ({ 
        ...p, 
        comments: [], 
        showComments: false, 
        liked: p.liked || false,
        likes: p.likes || 0
      }));
      console.log('posts array:', this.posts); 
    },
    error: (err) => console.error('Error:', err)
  });
}

toggleLike(post: any) {
  // Prevent multiple rapid clicks
  if (this.likeInProgress[post.id]) {
    return;
  }

  // Optimistic update - change UI immediately
  const previousLiked = post.liked;
  const previousLikes = post.likes;
  
  post.liked = !post.liked;
  post.likes = post.liked ? (post.likes || 0) + 1 : (post.likes || 1) - 1;
  this.likeInProgress[post.id] = true;

  const apiCall = post.liked 
    ? this.postService.likePost(post.id)
    : this.postService.dislikePost(post.id);

  apiCall.subscribe({
    next: (data) => {
      // Update with server response
      post.likes = data.likes;
      post.liked = data.liked !== undefined ? data.liked : post.liked;
      this.likeInProgress[post.id] = false;
    },
    error: (err) => {
      // Revert to previous state on error
      post.liked = previousLiked;
      post.likes = previousLikes;
      this.likeInProgress[post.id] = false;
      console.error('Like/Unlike failed:', err);
    }
  });
}
getImageUrl(postId: number): string {
  return `http://localhost:8086/StreetLeague/posts/image/${postId}`;
}
}