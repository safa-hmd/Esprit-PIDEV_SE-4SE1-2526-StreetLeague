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
  newPost = { title: '', description: '' };
  selectedImage: File | null = null;
  imagePreview: string | null = null;
  showForm = false;
  showEditForm = false;
  isLoading = false;
  editPost: any = { id: null, title: '', description: '' };
  commentsByPost: { [postId: number]: any[] } = {};
  
  // Statistics
  totalPosts = 0;
  totalComments = 0;
  totalLikes = 0;

  constructor(private postService: PostService, private commentService: CommentService) {}

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts() {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        this.posts = data;
        data.forEach(post => this.loadComments(post.id));
        this.updateStatistics();
      },
      error: (err) => console.error(err)
    });
  }

  updateStatistics() {
    this.totalPosts = this.posts.length;
    this.totalLikes = this.posts.reduce((sum, post) => sum + (post.likes || 0), 0);
    this.totalComments = this.posts.reduce((sum, post) => sum + (post.comments?.length || 0), 0);
  }

  loadComments(postId: number) {
    this.commentService.getCommentsByPost(postId).subscribe({
      next: (comments) => {
        this.commentsByPost[postId] = comments;
        this.updateStatistics();
      },
      error: (err) => console.error(err)
    });
  }

  addPost() {
    if (!this.newPost.title.trim() || !this.newPost.description.trim()) {
      alert('Please fill in title and description');
      return;
    }
    if (!this.selectedImage) {
      alert('Please select an image');
      return;
    }
    
    this.isLoading = true;
    console.log('📤 Sending post with FormData:');
    console.log('Title:', this.newPost.title);
    console.log('Description:', this.newPost.description);
    console.log('Image:', this.selectedImage.name, 'Size:', this.selectedImage.size);
    
    this.postService.addPost(this.newPost.title, this.newPost.description, this.selectedImage).subscribe({
      next: (response: any) => {
        console.log('✅ Full response:', JSON.stringify(response));
        this.isLoading = false;
        this.newPost = { title: '', description: '' }; 
        this.selectedImage = null;
        this.imagePreview = null;
        this.showForm = false; 
        this.loadPosts();
        setTimeout(() => alert('✅ Post added successfully!'), 100);
      },
      error: (err: any) => {
        console.error('❌ Raw error object:', err);
        console.error('Error status:', err?.status);
        console.error('Error statusText:', err?.statusText);
        console.error('Error error:', err?.error);
        console.error('Error message:', err?.message);
        this.isLoading = false;
        
        let errorMsg = 'Failed to add post';
        if (err?.status === 0) {
          errorMsg = 'Network error - check if backend is running';
        } else if (err?.status === 403) {
          errorMsg = 'Access denied - user must be ADMIN';
        } else if (err?.status === 400) {
          errorMsg = 'Bad request - ' + (err?.error?.message || 'invalid data');
        } else if (err?.error?.message) {
          errorMsg = err.error.message;
        } else if (err?.message) {
          errorMsg = err.message;
        }
        
        setTimeout(() => alert(`❌ Error: ${errorMsg}`), 100);
      }
    });
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  closeModal() {
    this.showForm = false;
    this.newPost = { title: '', description: '' };
    this.selectedImage = null;
    this.imagePreview = null;
  }

  deletePost(id: number) {
    this.postService.deletePost(id).subscribe({
      next: () => this.loadPosts(),
      error: (err) => console.error(err)
    });
  }

  openEditModal(post: any) {
    this.editPost = { id: post.id, title: post.title, description: post.description };
    this.showEditForm = true;
  }

  updatePost() {
    if (!this.editPost.title.trim() || !this.editPost.description.trim()) return;
    this.postService.updatePost(this.editPost.id, this.editPost).subscribe({
      next: () => { this.showEditForm = false; this.loadPosts(); },
      error: (err) => console.error(err)
    });
  }

  getInitials(name: string): string {
    return name?.split(' ').map(n => n[0]).join('').toUpperCase() || '??';
  }

  onImageError(event: any) {
    event.target.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22%3E%3Crect fill=%22%23333%22 width=%22100%25%22 height=%22100%25%22/%3E%3C/svg%3E';
  }
getImageUrl(postId: number): string {
  return `http://localhost:8086/StreetLeague/posts/image/${postId}`;
}
}