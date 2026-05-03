import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
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

  // ===== PAGINATION / INFINITE SCROLL =====
  page = 0;
  size = 5;
  totalPages = 0;
  isLastPage = false;
  isLoadingPosts = false;

  private observer!: IntersectionObserver;
  @ViewChild('sentinel') sentinel!: ElementRef;

  // ===== SEARCH =====
  searchKeyword = '';
  searchCategory = '';
  searchSort = 'date';
  isSearching = false;

  // ===== POSTS =====
  posts: any[] = [];
  newPost = { title: '', description: '', category: '', imageUrl: '' };
  selectedImage: File | null = null;
  imagePreview: string | null = null;
  showForm = false;
  showEditForm = false;
  isLoading = false;
  editPost: any = { id: null, title: '', description: '', category: '' };
  commentsByPost: { [postId: number]: any[] } = {};

  // ===== STATISTICS =====
  totalPosts = 0;
  totalComments = 0;
  totalLikes = 0;
  mostCommented: any = null;
  mostLiked: any = null;

  // ===== CHARTS =====
  private statsChart?: Chart;
  private refreshInterval: any;
  private catChart?: Chart;

  catColors = ['#E61920', '#3B82F6', '#F59E0B', '#10B981', '#8B5CF6', '#F97316'];
  readonly catNames = ['Event', 'Health', 'Celebration', 'Entertainment', 'Tournament', 'Promotion'];

  // ===== ERRORS =====
  showAddPostError = false;
  addPostErrorMessage = '';
  showEditPostError = false;
  editPostErrorMessage = '';

  // ===== AI =====
  aiPrompt = '';
  isGenerating = false;

  constructor(
    private postService: PostService,
    private commentService: CommentService
  ) {}

  // ===== LIFECYCLE =====

  ngOnInit() {
    this.loadPosts();
    this.refreshInterval = setInterval(() => {
      // refresh seulement si on est sur la première page et pas en train de scroller
      if (this.page === 0) {
        this.silentRefresh();
      }
    }, 10000);
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.initStatsChart();
    }, 500);

    this.observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && !this.isLoadingPosts && !this.isLastPage) {
        this.loadMore();
      }
    }, { threshold: 0.1 });

    this.observer.observe(this.sentinel.nativeElement);
  }

  ngOnDestroy() {
    if (this.refreshInterval) clearInterval(this.refreshInterval);
    if (this.observer) this.observer.disconnect();
    if (this.catChart) this.catChart.destroy();
    if (this.statsChart) this.statsChart.destroy();
  }

  // ===== INFINITE SCROLL =====

  loadMore() {
    this.page++;
    if (this.isSearching) {
      this.loadSearchResults();
    } else {
      this.loadPosts();
    }
  }

  // ===== LOAD POSTS =====

  loadPosts() {
    this.isLoadingPosts = true;
    this.postService.getAllPosts(this.page, this.size).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);

        if (this.page === 0) {
          this.posts = list;
        } else {
          this.posts = [...this.posts, ...list];
        }

        this.totalPages = data.totalPages || 1;
        this.isLastPage = data.last || false;
        this.isLoadingPosts = false;

        list.forEach((post: any) => this.loadComments(post.id));
        this.updateStatistics();
        this.loadTopPosts();
      },
      error: (err) => {
        console.error(err);
        this.isLoadingPosts = false;
      }
    });
  }

  // refresh silencieux toutes les 10s (sans reset la liste)
  silentRefresh() {
    this.postService.getAllPosts(0, this.size).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);
        // Mise à jour seulement des stats, pas de reset des posts
        list.forEach((post: any) => this.loadComments(post.id));
        this.updateStatistics();
        this.loadTopPosts();
      },
      error: (err) => console.error(err)
    });
  }

  // ===== SEARCH =====

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
    this.isLoadingPosts = true;
    this.postService.searchPosts(
      this.searchKeyword,
      this.searchCategory,
      this.searchSort,
      this.page,
      this.size
    ).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data.content || []);

        if (this.page === 0) {
          this.posts = list;
        } else {
          this.posts = [...this.posts, ...list];
        }

        this.totalPages = data.totalPages || 1;
        this.isLastPage = data.last || false;
        this.isLoadingPosts = false;

        list.forEach((post: any) => this.loadComments(post.id));
        this.updateStatistics();
      },
      error: (err) => {
        console.error(err);
        this.isLoadingPosts = false;
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

  // ===== STATISTICS =====

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

  // ✅ FIXED: Proper null checking and logging
  loadTopPosts() {
    this.postService.getTopPosts().subscribe({
      next: (data) => {
        console.log('Top posts response:', data); // Debug
        
        // Check if data exists and has properties
        if (data) {
          this.mostCommented = data.mostCommented || null;
          this.mostLiked = data.mostLiked || null;
          
          console.log('Most Commented:', this.mostCommented);
          console.log('Most Liked:', this.mostLiked);
        }
      },
      error: (err) => {
        console.error('Top posts error:', err);
        this.mostCommented = null;
        this.mostLiked = null;
      }
    });
  }

  get categoryStats() {
    return this.catNames.map(cat => ({
      name: cat,
      count: this.posts.filter(p => p.category === cat).length
    }));
  }

  // ===== COMMENTS =====

  loadComments(postId: number) {
    this.commentService.getCommentsByPost(postId).subscribe({
      next: (comments) => {
        this.commentsByPost[postId] = comments;
      },
      error: (err) => console.error(err)
    });
  }

  // ===== ADD POST =====

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

  addPost() {
    const title = this.newPost.title?.trim();
    const description = this.newPost.description?.trim();
    const category = this.newPost.category?.trim();

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
    if (!category) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Please select a category';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }
    if (!this.selectedImage && !this.newPost.imageUrl) {
      this.showAddPostError = true;
      this.addPostErrorMessage = 'Please select an image or generate one with AI';
      setTimeout(() => { this.showAddPostError = false; }, 5000);
      return;
    }

    this.isLoading = true;

    if (!this.selectedImage && this.newPost.imageUrl) {
      this.postService.addPostWithAIImage(
        this.newPost.title,
        this.newPost.description,
        this.newPost.category,
        this.newPost.imageUrl
      ).subscribe({
        next: () => {
          this.isLoading = false;
          this.resetForm();
          this.resetAndReload();
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

    this.postService.addPost(
      this.newPost.title,
      this.newPost.description,
      this.newPost.category,
      this.selectedImage!
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.resetForm();
        this.resetAndReload();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.showAddPostError = true;
        this.addPostErrorMessage = 'Failed to add post';
        console.error(err);
      }
    });
  }

  resetForm() {
    this.newPost = { title: '', description: '', category: '', imageUrl: '' };
    this.selectedImage = null;
    this.imagePreview = null;
    this.aiPrompt = '';
    this.showForm = false;
    this.showAddPostError = false;
    this.addPostErrorMessage = '';
  }

  // reset la liste et recharge depuis page 0
  resetAndReload() {
    this.page = 0;
    this.posts = [];
    this.isLastPage = false;
    this.loadPosts();
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
    this.resetForm();
  }

  // ===== DELETE POST =====

  deletePost(id: number) {
    this.postService.deletePost(id).subscribe({
      next: () => this.resetAndReload(),
      error: (err) => console.error(err)
    });
  }

  // ===== EDIT POST =====

  openEditModal(post: any) {
  this.editPost = {
    id: post.id,
    title: post.title,
    description: post.description,
    category: post.category
  };
  this.showEditPostError = false;
  this.editPostErrorMessage = '';
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

  // ✅ Envoie seulement les champs nécessaires, pas l'id dans le body
  this.postService.updatePost(this.editPost.id, {
    title: title,
    description: description,
    category: category
  }).subscribe({
    next: () => {
      this.showEditForm = false;
      this.showEditPostError = false;
      this.editPostErrorMessage = '';
      this.resetAndReload();
    },
    error: (err: any) => {
      this.showEditPostError = true;
      let errorMsg = 'Failed to update post';
      if (err?.status === 0) errorMsg = 'Network error - check if backend is running';
      else if (err?.status === 403) errorMsg = 'Access denied - user must be ADMIN';
      else if (err?.status === 400) errorMsg = 'Bad request - ' + (err?.error?.message || 'invalid data');
      else if (err?.error?.message) errorMsg = err.error.message;
      this.editPostErrorMessage = errorMsg;
      console.error(err);
    }
  });
}

  
  // ===== CHART =====

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

  // ===== UTILS =====

  getInitials(name: string): string {
    return name?.split(' ').map(n => n[0]).join('').toUpperCase() || '??';
  }

  onImageError(event: any) {
    event.target.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22%3E%3Crect fill=%22%23333%22 width=%22100%25%22 height=%22100%25%22/%3E%3C/svg%3E';
  }

  getImageUrl(post: any): string {
    return post?.imageUrl || 'assets/placeholder.png';
  }

  getCategoryStyle(category: string): string {
    const styles: { [key: string]: string } = {
      'Event':         'background:#FEE2E2;color:#E61920;border:1.5px solid #FECACA;',
      'Health':        'background:#DCFCE7;color:#16A34A;border:1.5px solid #BBF7D0;',
      'Celebration':   'background:#D1D5C8;color:#4B5320;border:1.5px solid #A3A58A;',
      'Entertainment': 'background:#EDE9FE;color:#7C3AED;border:1.5px solid #DDD6FE;',
      'Tournament':    'background:#DBEAFE;color:#2563EB;border:1.5px solid #BFDBFE;',
      'Promotion':     'background:#FEF9C3;color:#CA8A04;border:1.5px solid #FEF08A;',
    };
    return (styles[category] || 'background:#F3F4F6;color:#6B7280;border:1.5px solid #E5E7EB;')
         + 'padding:3px 12px;border-radius:999px;font-size:0.75rem;font-weight:700;white-space:nowrap;';
  }

  getTimeAgo(dateStr: string, updatedAt?: string): string {
    if (!dateStr) return '';

    const isEdited = updatedAt && updatedAt !== dateStr;
    const refDate = isEdited ? updatedAt! : dateStr;

    const now = new Date();
    const date = new Date(refDate);
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    let timeStr = '';
    if (seconds < 60)          timeStr = `il y a ${seconds} sec`;
    else {
      const minutes = Math.floor(seconds / 60);
      if (minutes < 60)        timeStr = `il y a ${minutes} min`;
      else {
        const hours = Math.floor(minutes / 60);
        if (hours < 24)        timeStr = `il y a ${hours}h`;
        else {
          const days = Math.floor(hours / 24);
          if (days < 7)        timeStr = `il y a ${days} j`;
          else {
            const weeks = Math.floor(days / 7);
            if (weeks < 4)     timeStr = `il y a ${weeks} sem`;
            else {
              const months = Math.floor(days / 30);
              if (months < 12) timeStr = `il y a ${months} mois`;
              else             timeStr = `il y a ${Math.floor(days / 365)} an(s)`;
            }
          }
        }
      }
    }

    return isEdited ? `${timeStr} · ` : timeStr;
  }
}