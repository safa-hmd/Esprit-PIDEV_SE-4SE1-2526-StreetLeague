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
    if (!content) return;
    this.commentService.addComment({ content, postId: post.id }).subscribe({
      next: () => {
        this.newComment[post.id] = '';
        this.loadComments(post);
      },
      error: (err) => console.error(err)
    });
  }

  openEditComment(comment: any, post: any) {
    this.editingComment = comment;
    this.editCommentContent = comment.content;
    this.selectedPost = post;
    this.showEditCommentForm = true;
  }

  saveEditComment(post: any) {
    if (!this.editCommentContent.trim()) return;
    this.commentService.updateComment(this.editingComment.id, {
      content: this.editCommentContent,
      postId: post.id
    }).subscribe({
      next: () => {
        this.showEditCommentForm = false;
        this.editingComment = null;
        this.editCommentContent = '';
        this.loadComments(post);
      },
      error: (err) => console.error(err)
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
  console.log('loadPosts called'); // ← أضف
  this.postService.getAllPosts().subscribe({
    next: (data) => {
      console.log('data:', data); // ← أضف
      this.posts = data.map(p => ({ ...p, comments: [], showComments: false, liked: false }));
      console.log('posts array:', this.posts); // ← أضف
    },
    error: (err) => console.error('Error:', err)
  });
}

toggleLike(post: any) {
  if (post.liked) {
    post.likes = (post.likes || 1) - 1;
    post.liked = false;
  } else {
    this.postService.likePost(post.id).subscribe({
      next: (data) => {
        post.likes = data.likes;
        post.liked = true;
      },
      error: (err) => console.error(err)
    });
  }
}
getImageUrl(postId: number): string {
  return `http://localhost:8086/StreetLeague/posts/image/${postId}`;
}
}