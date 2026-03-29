import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NewsComponent } from './news.component';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { of } from 'rxjs';

describe('NewsComponent (Backoffice) - Contrôles de Saisie', () => {
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

    // ✅ setup قبل detectChanges
    postService.getAllPosts.and.returnValue(of([]));
    commentService.getCommentsByPost.and.returnValue(of([]));

    fixture = TestBed.createComponent(NewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Validation - Formulaire d\'ajout de post', () => {
    it('ne devrait pas ajouter un post si titre est vide', () => {
      component.newPost = { title: '', description: 'Description valide' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please fill in title and description');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('ne devrait pas ajouter un post si description est vide', () => {
      component.newPost = { title: 'Titre valide', description: '' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please fill in title and description');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('ne devrait pas ajouter un post si titre contient seulement espaces', () => {
      component.newPost = { title: '   ', description: 'Description valide' };
      component.selectedImage = new File([''], 'test.jpg', { type: 'image/jpeg' });

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalled();
    });

    it('ne devrait pas ajouter un post si l\'image est vide', () => {
      component.newPost = { title: 'Titre valide', description: 'Description valide' };
      component.selectedImage = null;

      spyOn(window, 'alert');
      component.addPost();

      expect(window.alert).toHaveBeenCalledWith('Please select an image');
      expect(postService.addPost).not.toHaveBeenCalled();
    });

    it('devrait ajouter un post avec données valides', (done) => {
      component.newPost = { title: 'Titre', description: 'Description' };
      component.selectedImage = new File(['content'], 'test.jpg', { type: 'image/jpeg' });
      postService.addPost.and.returnValue(of({ id: 1 })); // ✅

      component.addPost();

      setTimeout(() => {
        expect(postService.addPost).toHaveBeenCalled();
        expect(component.newPost.title).toBe('');
        done();
      }, 150);
    });
  });

  describe('Validation - Sélection d\'image', () => {
    it('devrait sauvegarder l\'image sélectionnée', () => {
      const file = new File(['content'], 'photo.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };

      component.onImageSelected(event);

      expect(component.selectedImage).toBe(file);
    });

    it('devrait générer un aperçu de l\'image', (done) => {
      const file = new File(['content'], 'photo.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };

      component.onImageSelected(event);

      setTimeout(() => {
        expect(component.imagePreview).toBeTruthy();
        done();
      }, 100);
    });
  });

  describe('Validation - Modification de post', () => {
    it('devrait ouvrir le modal de modification', () => {
      const mockPost = { id: 1, title: 'Post 1', description: 'Desc 1' };
      component.openEditModal(mockPost);

      expect(component.editPost.id).toBe(1);
      expect(component.showEditForm).toBe(true);
    });

    it('ne devrait pas mettre à jour si validation échoue', () => {
      component.editPost = { id: 1, title: '', description: 'Description' };
      component.updatePost();

      expect(postService.updatePost).not.toHaveBeenCalled();
    });
  });

  describe('Validation - Initiales', () => {
    it('devrait formater le nom complet en initiales', () => {
      expect(component.getInitials('John Doe')).toBe('JD');
    });

    it('devrait retourner ?? si nom invalide', () => {
      expect(component.getInitials(null as any)).toBe('??');
    });
  });

  it('devrait être créé', () => {
    expect(component).toBeTruthy();
  });
});