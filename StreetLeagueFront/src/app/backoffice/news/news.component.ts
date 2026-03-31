import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';

Chart.register(...registerables);

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.css']
})
export class NewsComponent implements OnInit, AfterViewInit, OnDestroy {
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
  
  // Chart
  private statsChart?: Chart;
  
  // Auto-refresh
  private refreshInterval: any;
  
  // Form Validation
  showAddPostError = false;
  addPostErrorMessage = '';
  showEditPostError = false;
  editPostErrorMessage = '';

  constructor(private postService: PostService, private commentService: CommentService) {}

  ngOnInit() {
    this.loadPosts();
    
    // Auto-refresh statistics every 10 seconds
    this.refreshInterval = setInterval(() => {
      this.loadPosts();
    }, 10000);
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.initStatsChart();
    }, 500);
  }

  ngOnDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
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
    this.totalComments = Object.values(this.commentsByPost).reduce((sum, comments) => sum + (comments?.length || 0), 0);
    this.initStatsChart();
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
    const title = this.newPost.title?.trim();
    const description = this.newPost.description?.trim();
    
    // Validation Title
    if (!title) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Title cannot be empty';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    if (title.length < 3) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Title must be at least 3 characters';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    if (title.length > 100) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Title must not exceed 100 characters';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    // Validation Description
    if (!description) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Description cannot be empty';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    if (description.length < 5) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Description must be at least 5 characters';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    if (description.length > 500) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Description must not exceed 500 characters';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    // Validation Image
    if (!this.selectedImage) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Please select an image';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    if (!this.selectedImage.type.startsWith('image/')) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'File must be an image (JPEG, PNG, etc.)';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    const maxSizeMB = 5;
    if (this.selectedImage.size > maxSizeMB * 1024 * 1024) {
      this.showAddPostError = true;
      this.addPostErrorMessage = `Image size must not exceed ${maxSizeMB}MB`;
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }
    
    this.isLoading = true;
    this.postService.addPost(this.newPost.title, this.newPost.description, this.selectedImage).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.newPost = { title: '', description: '' }; 
        this.selectedImage = null;
        this.imagePreview = null;
        this.showForm = false;
        this.showAddPostError = false;
        this.loadPosts();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.showAddPostError = true;
        let errorMsg = 'Failed to add post';
        if (err?.status === 0) {
          errorMsg = 'Network error - check if backend is running';
        } else if (err?.status === 403) {
          errorMsg = 'Access denied - user must be ADMIN';
        } else if (err?.status === 400) {
          errorMsg = 'Bad request - ' + (err?.error?.message || 'invalid data');
        } else if (err?.error?.message) {
          errorMsg = err.error.message;
        }
        this.addPostErrorMessage = errorMsg;
        console.error(err);
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
    this.showAddPostError = false;
    this.addPostErrorMessage = '';
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
    const title = this.editPost.title?.trim();
    const description = this.editPost.description?.trim();
    
    // Validation Title
    if (!title) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Title cannot be empty';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    if (title.length < 3) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Title must be at least 3 characters';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    if (title.length > 100) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Title must not exceed 100 characters';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    // Validation Description
    if (!description) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Description cannot be empty';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    if (description.length < 5) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Description must be at least 5 characters';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    if (description.length > 500) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Description must not exceed 500 characters';
      setTimeout(() => { this.showEditPostError = false; }, 5000);
      return;
    }

    this.postService.updatePost(this.editPost.id, this.editPost).subscribe({
      next: () => {
        this.showEditForm = false;
        this.showEditPostError = false;
        this.loadPosts();
      },
      error: (err: any) => {
        this.showEditPostError = true;
        let errorMsg = 'Failed to update post';
        if (err?.status === 0) {
          errorMsg = 'Network error - check if backend is running';
        } else if (err?.status === 403) {
          errorMsg = 'Access denied - user must be ADMIN';
        } else if (err?.status === 400) {
          errorMsg = 'Bad request - ' + (err?.error?.message || 'invalid data');
        } else if (err?.error?.message) {
          errorMsg = err.error.message;
        }
        this.editPostErrorMessage = errorMsg;
        console.error(err);
      }
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

  private initStatsChart(): void {
    const canvas = document.getElementById('statsChart') as HTMLCanvasElement;
    if (!canvas) return;
    
    if (this.statsChart) {
      this.statsChart.data.datasets[0].data = [this.totalPosts, this.totalComments, this.totalLikes];
      this.statsChart.update();
      return;
    }

    this.statsChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Posts', 'Comments', 'Likes'],
        datasets: [{
          data: [this.totalPosts, this.totalComments, this.totalLikes],
          backgroundColor: ['#E61920', '#3B82F6', '#F59E0B'],
          borderColor: '#1e1e2a',
          borderWidth: 2,
          hoverOffset: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              color: '#9090a8',
              font: { size: 12, weight: 'bold' },
              padding: 15
            }
          }
        }
      }
    });
  }
}