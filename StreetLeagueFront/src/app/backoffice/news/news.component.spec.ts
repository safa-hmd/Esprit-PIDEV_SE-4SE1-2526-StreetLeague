import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NewsComponent } from './news.component';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { of } from 'rxjs';

describe('NewsComponent (Backoffice) - Input Validation', () => {
  let component: NewsComponent;
  let fixture: ComponentFixture<NewsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let commentService: jasmine.SpyObj<CommentService>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getAllPosts', 'addPost', 'deletePost', 'updatePost']);
    const commentServiceSpy = jasmine.createSpyObj('CommentService', ['getCommentsByPost']);

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

// ✅ setup before detectChanges
    postService.getAllPosts.and.returnValue(of([]));
    commentService.getCommentsByPost.and.returnValue(of([]));

    fixture = TestBed.createComponent(NewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Validation - Post Add Form', () => {
    it('should not add post if title is empty', () => {
      component.newPost = { title: '', description: 'Valid description' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please fill in title and description');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('should not add post if description is empty', () => {
      component.newPost = { title: 'Valid title', description: '' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please fill in title and description');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('should not add post if title contains only spaces', () => {
      component.newPost = { title: '   ', description: 'Valid description' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalled();
    });

    it('should not add post if image is missing', () => {
      component.newPost = { title: 'Valid title', description: 'Valid description' };
      component.selectedImage = null;

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please select an image');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('should add post with valid data', (done) => {
      component.newPost = { title: 'Title', description: 'Description' };
      component.selectedImage = new File(['content'], 'test.jpg', { type: 'image/jpeg' });
      postService.addPost.and.returnValue(of({ id: 1 }));

      component.addPost();

      setTimeout(() => {
        expect(postService.addPost).toHaveBeenCalled();
        expect(component.newPost.title).toBe('');
        done();
      }, 150);
    });
  });

  describe('Validation - Image Selection', () => {
    it('should save selected image', () => {
      const file = new File(['content'], 'photo.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };

      component.onImageSelected(event);

      expect(component.selectedImage).toBe(file);
    });

    it('should generate image preview', (done) => {
      const file = new File(['content'], 'photo.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };

      component.onImageSelected(event);

      setTimeout(() => {
        expect(component.imagePreview).toBeTruthy();
        done();
      }, 100);
    });
  });

  describe('Validation - Post Edit', () => {
    it('should open edit modal', () => {
      const mockPost = { id: 1, title: 'Post 1', description: 'Desc 1' };
      component.openEditModal(mockPost);

      expect(component.editPost.id).toBe(1);
      expect(component.showEditForm).toBe(true);
    });

    it('should not update if validation fails', () => {
      component.editPost = { id: 1, title: '', description: 'Description' };
      component.updatePost();

      expect(postService.updatePost).not.toHaveBeenCalled();
    });
  });

  describe('Validation - Initials', () => {
    it('should format full name as initials', () => {
      expect(component.getInitials('John Doe')).toBe('JD');
    });

    it('should return ?? if invalid name', () => {
      expect(component.getInitials(null as any)).toBe('??');
    });
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});