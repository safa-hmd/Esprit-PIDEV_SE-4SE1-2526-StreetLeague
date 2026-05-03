import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
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
  selectedImage: File | null = null;
  imagePreview: string | null = null;

  showForm = false;
  showEditForm = false;
  isLoading = false;

  editPost: any = { id: null, title: '', description: '' };
  commentsByPost: { [postId: number]: any[] } = {};

  totalPosts = 0;
  totalComments = 0;
  totalLikes = 0;

  // Form Groups
  addPostForm: FormGroup;
  editPostForm: FormGroup;

  // Error flags for template
  showAddPostError = false;
  addPostErrorMessage = '';
  showEditPostError = false;
  editPostErrorMessage = '';

  private statsChart?: Chart;
  private refreshInterval: any;

  // Custom validator for whitespace
  private noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
    const isWhitespace = control.value && control.value.toString().trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { whitespace: true };
  }

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private fb: FormBuilder
  ) {
    // Initialize Add Post Form with validators
    this.addPostForm = this.fb.group({
      title: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100),
        this.noWhitespaceValidator
      ]],
      description: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(500),
        this.noWhitespaceValidator
      ]],
      image: [null, [Validators.required]]
    });

    // Initialize Edit Post Form with validators
    this.editPostForm = this.fb.group({
      title: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100),
        this.noWhitespaceValidator
      ]],
      description: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(500),
        this.noWhitespaceValidator
      ]]
    });
  }

  ngOnInit() {
    this.loadPosts();
    this.refreshInterval = setInterval(() => this.loadPosts(), 10000);
  }

  ngAfterViewInit() {
    setTimeout(() => this.initStatsChart(), 500);
  }

  ngOnDestroy() {
    if (this.refreshInterval) clearInterval(this.refreshInterval);
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

  loadComments(postId: number) {
    this.commentService.getCommentsByPost(postId).subscribe({
      next: (comments) => {
        this.commentsByPost[postId] = comments;
        this.updateStatistics();
      },
      error: (err) => console.error(err)
    });
  }

  updateStatistics() {
    this.totalPosts = this.posts.length;
    this.totalLikes = this.posts.reduce((s, p) => s + (p.likes || 0), 0);
    this.totalComments = Object.values(this.commentsByPost)
      .reduce((s: number, c: any[]) => s + (c?.length || 0), 0);

    this.initStatsChart();
  }

  // Helper method to get error message for add form
  getAddPostError(controlName: string): string {
    const control = this.addPostForm.get(controlName);
    if (control?.touched && control?.invalid) {
      if (control.errors?.['required']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} is required`;
      }
      if (control.errors?.['whitespace']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} cannot be empty or contain only spaces`;
      }
      if (control.errors?.['minlength']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} must be at least ${control.errors['minlength'].requiredLength} characters`;
      }
      if (control.errors?.['maxlength']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
      }
    }
    return '';
  }

  // Helper method to get error message for edit form
  getEditPostError(controlName: string): string {
    const control = this.editPostForm.get(controlName);
    if (control?.touched && control?.invalid) {
      if (control.errors?.['required']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} is required`;
      }
      if (control.errors?.['whitespace']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} cannot be empty or contain only spaces`;
      }
      if (control.errors?.['minlength']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} must be at least ${control.errors['minlength'].requiredLength} characters`;
      }
      if (control.errors?.['maxlength']) {
        return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
      }
    }
    return '';
  }

  addPost() {
    // Reset errors
    this.showAddPostError = false;
    this.addPostErrorMessage = '';

    // Mark all fields as touched to trigger validation display
    this.addPostForm.markAllAsTouched();

    // Check if form is valid
    if (this.addPostForm.invalid) {
      const titleControl = this.addPostForm.get('title');
      const descriptionControl = this.addPostForm.get('description');
      const imageControl = this.addPostForm.get('image');

      // Show alerts for backward compatibility with tests
      if (titleControl?.errors?.['required'] || titleControl?.errors?.['whitespace'] ||
          descriptionControl?.errors?.['required'] || descriptionControl?.errors?.['whitespace']) {
        alert('Please fill in title and description');
        return;
      }

      if (imageControl?.errors?.['required']) {
        alert('Please select an image');
        return;
      }

      return;
    }

    const title = this.addPostForm.get('title')?.value;
    const description = this.addPostForm.get('description')?.value;

    this.isLoading = true;

    this.postService.addPost(title, description, this.selectedImage!).subscribe({
      next: () => {
        this.isLoading = false;
        // Reset form
        this.addPostForm.reset();
        this.selectedImage = null;
        this.imagePreview = null;
        this.showForm = false;
        this.loadPosts();
      },
      error: (err) => {
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
      this.addPostForm.patchValue({ image: file });
      this.addPostForm.get('image')?.updateValueAndValidity();
      
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  closeModal() {
    this.showForm = false;
    this.addPostForm.reset();
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
    this.editPost = { ...post };
    this.editPostForm.patchValue({
      title: post.title,
      description: post.description
    });
    this.showEditForm = true;
    this.showEditPostError = false;
    this.editPostErrorMessage = '';
  }

  updatePost() {
    // Reset errors
    this.showEditPostError = false;
    this.editPostErrorMessage = '';

    // Mark all fields as touched to trigger validation display
    this.editPostForm.markAllAsTouched();

    // Check if form is valid
    if (this.editPostForm.invalid) {
      const titleControl = this.editPostForm.get('title');
      const descriptionControl = this.editPostForm.get('description');

      if (titleControl?.errors?.['required'] || titleControl?.errors?.['whitespace'] ||
          descriptionControl?.errors?.['required'] || descriptionControl?.errors?.['whitespace']) {
        alert('Title and description cannot be empty');
        return;
      }
      return;
    }

    const title = this.editPostForm.get('title')?.value;
    const description = this.editPostForm.get('description')?.value;

    this.postService.updatePost(this.editPost.id, { title, description }).subscribe({
      next: () => {
        this.showEditForm = false;
        this.editPostForm.reset();
        this.loadPosts();
      },
      error: (err) => {
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
      this.statsChart.data.datasets[0].data = [
        this.totalPosts,
        this.totalComments,
        this.totalLikes
      ];
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
          borderWidth: 2
        }]
      },
      options: { responsive: true }
    });
  }
}