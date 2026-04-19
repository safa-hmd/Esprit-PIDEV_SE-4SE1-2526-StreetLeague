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
  page = 0;
  size = 5;
  totalPages = 0;
  mostCommented: any = null;
  mostLiked: any = null;

  searchKeyword = '';
  searchCategory = '';
  searchSort = 'date';
  isSearching = false;

  posts: any[] = [];
  newPost = { title: '', description: '', category: '', imageUrl: '' };
  selectedImage: File | null = null;
  imagePreview: string | null = null;
  showForm = false;
  showEditForm = false;
  isLoading = false;
  editPost: any = { id: null, title: '', description: '', category: '' };
  commentsByPost: { [postId: number]: any[] } = {};

  totalPosts = 0;
  totalComments = 0;
  totalLikes = 0;

  private statsChart?: Chart;
  private refreshInterval: any;
  private catChart?: Chart;

  catColors = ['#E61920', '#3B82F6', '#F59E0B', '#10B981', '#8B5CF6', '#F97316'];
  readonly catNames = ['Event', 'Health', 'Celebration', 'Entertainment', 'Tournament', 'Promotion'];

  showAddPostError = false;
  addPostErrorMessage = '';
  showEditPostError = false;
  editPostErrorMessage = '';

  aiPrompt = '';
  isGenerating = false;

  constructor(private postService: PostService, private commentService: CommentService) {}

  ngOnInit() {
    this.loadPosts();
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
    if (this.catChart) this.catChart.destroy();
    if (this.statsChart) this.statsChart.destroy();
  }

  generateImage() {
    if (!this.aiPrompt.trim()) return;

    this.isGenerating = true;
    this.postService.generateAIImage(this.aiPrompt).subscribe({
      next: (data) => {
        this.imagePreview = data.imageUrl;
        this.newPost.imageUrl = data.imageUrl;
        this.isGenerating = false;
      },
      error: (err) => {
        console.error(err);
        this.isGenerating = false;
      }
    });
  }

  loadPosts() {
    this.postService.getAllPosts(this.page, this.size).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);
        this.posts = list;
        this.totalPages = data.totalPages || 1;
        list.forEach((post: any) => this.loadComments(post.id));
        this.updateStatistics();
        this.loadTopPosts();
      },
      error: (err) => console.error(err)
    });
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      if (this.isSearching) {
        this.loadSearchResults();
      } else {
        this.loadPosts();
      }
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      if (this.isSearching) {
        this.loadSearchResults();
      } else {
        this.loadPosts();
      }
    }
  }

  updateStatistics() {
    this.postService.getGeneralStats().subscribe({
      next: (stats) => {
        this.totalPosts = stats.totalPosts;
        this.totalLikes = stats.totalLikes;
        this.totalComments = stats.totalComments;
        this.initStatsChart();
      },
      error: (err) => console.error(err)
    });
  }

  loadComments(postId: number) {
    this.commentService.getCommentsByPost(postId).subscribe({
      next: (comments) => {
        this.commentsByPost[postId] = comments;
      },
      error: (err) => console.error(err)
    });
  }

  get categoryStats() {
    return this.catNames.map(cat => ({
      name: cat,
      count: this.posts.filter(p => p.category === cat).length
    }));
  }

  addPost() {
    const title = this.newPost.title?.trim();
    const description = this.newPost.description?.trim();
    const category = this.newPost.category?.trim();

    // ... كل الـ validations تبقى كما هي ...

    if (!this.selectedImage && !this.newPost.imageUrl) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Please select an image or generate one with AI';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    this.isLoading = true;

    // ✅ إذا فيه AI image → استخدم endpoint مختلف
    if (!this.selectedImage && this.newPost.imageUrl) {
      this.postService.addPostWithAIImage(
        this.newPost.title,
        this.newPost.description,
        this.newPost.category,
        this.newPost.imageUrl
      ).subscribe({
        next: () => {
          this.isLoading = false;
          this.newPost = { title: '', description: '', category: '', imageUrl: '' };
          this.selectedImage = null;
          this.imagePreview = null;
          this.aiPrompt = '';
          this.showForm = false;
          this.loadPosts();
        },
        error: (err: any) => {
          this.isLoading = false;
          this.showAddPostError = true;
          this.addPostErrorMessage = 'Failed to add post';
          console.error(err);
        }
      });
      return;
    }

    // الحالة العادية — upload صورة
    this.postService.addPost(
      this.newPost.title,
      this.newPost.description,
      this.newPost.category,
      this.selectedImage!
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.newPost = { title: '', description: '', category: '', imageUrl: '' };
        this.selectedImage = null;
        this.imagePreview = null;
        this.aiPrompt = '';
        this.showForm = false;
        this.loadPosts();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.showAddPostError = true;
        this.addPostErrorMessage = 'Failed to add post';
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
    this.newPost = { title: '', description: '', category: '', imageUrl: '' };
    this.selectedImage = null;
    this.imagePreview = null;
    this.aiPrompt = '';
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
    this.editPost = {
      id: post.id,
      title: post.title,
      description: post.description,
      category: post.category
    };
    this.showEditForm = true;
  }

  updatePost() {
    const title = this.editPost.title?.trim();
    const description = this.editPost.description?.trim();
    const category = this.editPost.category?.trim();

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
    if (!category) {
      this.showEditPostError = true;
      this.editPostErrorMessage = 'Please select a category';
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

  getImageUrl(post: any): string {
    return post?.imageUrl || 'assets/placeholder.png';
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

  loadTopPosts() {
    this.postService.getTopPosts().subscribe({
      next: (data) => {
        this.mostCommented = data.mostCommented || null;
        this.mostLiked = data.mostLiked || null;
      },
      error: (err) => console.error('Top posts error', err)
    });
  }

  onSearch() {
    this.page = 0;
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
    this.postService.searchPosts(
      this.searchKeyword,
      this.searchCategory,
      this.searchSort,
      this.page,
      this.size
    ).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);
        this.posts = list;
        this.totalPages = data.totalPages || 1;
        list.forEach((post: any) => this.loadComments(post.id));
        this.updateStatistics();
      },
      error: (err) => console.error(err)
    });
  }

  onSortChange() {
    this.page = 0;
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
    this.loadPosts();
  }
}