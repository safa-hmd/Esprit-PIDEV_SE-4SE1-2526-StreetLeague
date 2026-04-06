import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NewsComponent } from './news.component';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { of, throwError } from 'rxjs';

describe('NewsComponent (Frontoffice) - Input Validation', () => {
  let component: NewsComponent;
  let fixture: ComponentFixture<NewsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let commentService: jasmine.SpyObj<CommentService>;

  beforeEach(async () => {
    // CORRECTION: Ajouter 'dislikePost' au spy
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getAllPosts', 'likePost', 'dislikePost']);
    const commentServiceSpy = jasmine.createSpyObj('CommentService', [
      'getCommentsByPost', 'addComment', 'updateComment', 'deleteComment'
    ]);

    await TestBed.configureTestingModule({
      declarations: [NewsComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: CommentService, useValue: commentServiceSpy }
      ]
    }).compileComponents();

    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    commentService = TestBed.inject(CommentService) as jasmine.SpyObj<CommentService>;

    postService.getAllPosts.and.returnValue(of([]));
    postService.likePost.and.returnValue(of({ likes: 6, liked: true }));
    postService.dislikePost.and.returnValue(of({ likes: 5, liked: false }));
    commentService.getCommentsByPost.and.returnValue(of([]));

    fixture = TestBed.createComponent(NewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Validation - Post Loading', () => {
    it('should load all posts on startup', (done) => {
      const mockPosts = [
        { id: 1, title: 'Post 1', description: 'Desc 1', comments: [] },
        { id: 2, title: 'Post 2', description: 'Desc 2', comments: [] }
      ];
      postService.getAllPosts.and.returnValue(of(mockPosts));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.ngOnInit();

      setTimeout(() => {
        expect(component.posts.length).toBe(2);
        done();
      }, 100);
    });

    it('should handle errors during loading', () => {
      postService.getAllPosts.and.returnValue(throwError(() => ({ status: 500 })));
      spyOn(console, 'error');

      component.loadPosts();

      expect(console.error).toHaveBeenCalled();
    });
  });

  describe('Validation - Comments', () => {
    beforeEach(() => {
      component.posts = [
        { id: 1, title: 'Post 1', comments: [], showComments: false }
      ];
    });

    it('should toggle comments display', () => {
      const post = component.posts[0];
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.toggleComments(post);
      expect(post.showComments).toBe(true);

      component.toggleComments(post);
      expect(post.showComments).toBe(false);
    });

    it('should load comments when toggled', () => {
      const post = component.posts[0];
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.toggleComments(post);

      expect(commentService.getCommentsByPost).toHaveBeenCalledWith(1);
    });

    it('should add valid comment', () => {
      const post = component.posts[0];
      component.newComment[1] = 'Test comment';
      commentService.addComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.addComment(post);

      expect(commentService.addComment).toHaveBeenCalledWith({
        content: 'Test comment',
        postId: 1
      });
    });

    it('should reject empty comment', () => {
      const post = component.posts[0];
      component.newComment[1] = '';

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
      expect(component.showCommentError[1]).toBe(true);
      expect(component.commentErrorMessage[1]).toBe('Comment cannot be empty');
    });

    it('should reject comment with only spaces', () => {
      const post = component.posts[0];
      component.newComment[1] = '   ';

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
      expect(component.showCommentError[1]).toBe(true);
    });

    it('should reject comment with less than 2 characters', () => {
      const post = component.posts[0];
      component.newComment[1] = 'a';

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
      expect(component.showCommentError[1]).toBe(true);
      expect(component.commentErrorMessage[1]).toBe('Comment must be at least 2 characters');
    });

    it('should reject comment exceeding 500 characters', () => {
      const post = component.posts[0];
      component.newComment[1] = 'a'.repeat(501);

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
      expect(component.showCommentError[1]).toBe(true);
      expect(component.commentErrorMessage[1]).toBe('Comment must not exceed 500 characters');
    });

    it('should display error message for failed comment addition', (done) => {
      const post = component.posts[0];
      component.newComment[1] = 'Test comment';
      commentService.addComment.and.returnValue(throwError(() => ({ status: 500 })));

      component.addComment(post);

      setTimeout(() => {
        expect(component.showCommentError[1]).toBe(true);
        expect(component.commentErrorMessage[1]).toContain('Failed');
        done();
      }, 100);
    });

    it('should open edit comment form', () => {
      const comment = { id: 1, content: 'Old text' };
      const post = component.posts[0];

      component.openEditComment(comment, post);

      expect(component.editingComment).toBe(comment);
      expect(component.editCommentContent).toBe('Old text');
      expect(component.showEditCommentForm).toBe(true);
    });

    it('should update valid comment', () => {
      const comment = { id: 1, content: 'Old text' };
      const post = component.posts[0];
      component.editingComment = comment;
      component.editCommentContent = 'New text';
      commentService.updateComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.saveEditComment(post);

      expect(commentService.updateComment).toHaveBeenCalled();
      expect(component.showEditCommentForm).toBe(false);
    });

    it('should reject empty edit comment', () => {
      const post = component.posts[0];
      component.editCommentContent = '';

      component.saveEditComment(post);

      expect(commentService.updateComment).not.toHaveBeenCalled();
      expect(component.showEditError).toBe(true);
      expect(component.editErrorMessage).toBe('Comment cannot be empty');
    });

    it('should reject edit comment with less than 2 characters', () => {
      const post = component.posts[0];
      component.editCommentContent = 'a';

      component.saveEditComment(post);

      expect(commentService.updateComment).not.toHaveBeenCalled();
      expect(component.showEditError).toBe(true);
      expect(component.editErrorMessage).toBe('Comment must be at least 2 characters');
    });

    it('should reject edit comment exceeding 500 characters', () => {
      const post = component.posts[0];
      component.editCommentContent = 'a'.repeat(501);

      component.saveEditComment(post);

      expect(commentService.updateComment).not.toHaveBeenCalled();
      expect(component.showEditError).toBe(true);
      expect(component.editErrorMessage).toBe('Comment must not exceed 500 characters');
    });

    it('should delete comment', () => {
      const post = component.posts[0];
      commentService.deleteComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.deleteComment(1, post);

      expect(commentService.deleteComment).toHaveBeenCalledWith(1);
    });
  });

  describe('Validation - Post Likes', () => {
    beforeEach(() => {
      component.posts = [
        { id: 1, title: 'Post 1', liked: false, likes: 5 }
      ];
    });

    it('should like a post', () => {
      const post = component.posts[0];
      postService.likePost.and.returnValue(of({ likes: 6, liked: true }));

      component.toggleLike(post);

      expect(postService.likePost).toHaveBeenCalledWith(1);
      expect(post.liked).toBe(true);
      expect(post.likes).toBe(6);
    });

    it('should remove like if already liked', () => {
      const post = component.posts[0];
      post.liked = true;
      post.likes = 6;
      postService.dislikePost.and.returnValue(of({ likes: 5, liked: false }));

      component.toggleLike(post);

      expect(postService.dislikePost).toHaveBeenCalledWith(1);
      expect(post.liked).toBe(false);
      expect(post.likes).toBe(5);
    });
  });

  describe('Validation - Initial Format', () => {
    it('should format full name as initials', () => {
      expect(component.getInitials('John Doe')).toBe('JD');
      expect(component.getInitials('Alice Johnson')).toBe('AJ');
    });

    it('should handle single name', () => {
      expect(component.getInitials('Alice')).toBe('A');
    });

    it('should return ?? if invalid name', () => {
      expect(component.getInitials(null as any)).toBe('??');
    });
  });

  describe('Validation - Image URL', () => {
    it('should generate correct image URL', () => {
      const url = component.getImageUrl(5);
      expect(url).toContain('http://localhost:8086');
      expect(url).toContain('/posts/image/5');
    });
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});