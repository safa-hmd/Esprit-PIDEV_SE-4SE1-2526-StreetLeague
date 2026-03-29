import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NewsComponent } from './news.component';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { of, throwError } from 'rxjs';

describe('NewsComponent (Frontoffice) - Contrôles de Saisie', () => {
  let component: NewsComponent;
  let fixture: ComponentFixture<NewsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let commentService: jasmine.SpyObj<CommentService>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getAllPosts', 'likePost']);
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

    // ✅ setup قبل detectChanges
    postService.getAllPosts.and.returnValue(of([]));
    commentService.getCommentsByPost.and.returnValue(of([]));

    fixture = TestBed.createComponent(NewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Validation - Chargement des posts', () => {
    it('devrait charger tous les posts au démarrage', (done) => {
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

    it('devrait gérer les erreurs lors du chargement', () => {
      postService.getAllPosts.and.returnValue(throwError(() => ({ status: 500 })));
      spyOn(console, 'error');

      component.loadPosts();

      expect(console.error).toHaveBeenCalled();
    });
  });

  describe('Validation - Commentaires', () => {
    beforeEach(() => {
      component.posts = [
        { id: 1, title: 'Post 1', comments: [], showComments: false }
      ];
    });

    it('devrait basculer l\'affichage des commentaires', () => {
      const post = component.posts[0];
      commentService.getCommentsByPost.and.returnValue(of([])); // ✅

      component.toggleComments(post);
      expect(post.showComments).toBe(true);

      component.toggleComments(post);
      expect(post.showComments).toBe(false);
    });

    it('deve charger commentaires lors du basculer', () => {
      const post = component.posts[0];
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.toggleComments(post);

      expect(commentService.getCommentsByPost).toHaveBeenCalledWith(1);
    });

    it('devrait ajouter un commentaire valide', () => {
      const post = component.posts[0];
      component.newComment[1] = 'Commentaire test';
      commentService.addComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.addComment(post);

      expect(commentService.addComment).toHaveBeenCalledWith({
        content: 'Commentaire test',
        postId: 1
      });
    });

    it('ne devrait pas ajouter un commentaire vide', () => {
      const post = component.posts[0];
      component.newComment[1] = '';

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
    });

    it('ne devrait pas ajouter un commentaire avec espaces seulement', () => {
      const post = component.posts[0];
      component.newComment[1] = '   ';

      component.addComment(post);

      expect(commentService.addComment).not.toHaveBeenCalled();
    });

    it('devrait ouvrir le formulaire d\'édition de commentaire', () => {
      const comment = { id: 1, content: 'Ancien texte' };
      const post = component.posts[0];

      component.openEditComment(comment, post);

      expect(component.editingComment).toBe(comment);
      expect(component.editCommentContent).toBe('Ancien texte');
      expect(component.showEditCommentForm).toBe(true);
    });

    it('devrait mettre à jour un commentaire valide', () => {
      const comment = { id: 1, content: 'Ancien texte' };
      const post = component.posts[0];
      component.editingComment = comment;
      component.editCommentContent = 'Nouveau texte';
      commentService.updateComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.saveEditComment(post);

      expect(commentService.updateComment).toHaveBeenCalled();
      expect(component.showEditCommentForm).toBe(false);
    });

    it('ne devrait pas mettre à jour avec texte vide', () => {
      const post = component.posts[0];
      component.editCommentContent = '';

      component.saveEditComment(post);

      expect(commentService.updateComment).not.toHaveBeenCalled();
    });

    it('devrait supprimer un commentaire', () => {
      const post = component.posts[0];
      commentService.deleteComment.and.returnValue(of({}));
      commentService.getCommentsByPost.and.returnValue(of([]));

      component.deleteComment(1, post);

      expect(commentService.deleteComment).toHaveBeenCalledWith(1);
    });
  });

  describe('Validation - Like de post', () => {
    beforeEach(() => {
      component.posts = [
        { id: 1, title: 'Post 1', liked: false, likes: 5 }
      ];
    });

    it('devrait liker un post', () => {
      const post = component.posts[0];
      postService.likePost.and.returnValue(of({ likes: 6 }));

      component.toggleLike(post);

      expect(postService.likePost).toHaveBeenCalledWith(1);
      expect(post.liked).toBe(true);
    });

    it('devrait enlever le like si déjà liké', () => {
      const post = component.posts[0];
      post.liked = true;
      post.likes = 6;

      component.toggleLike(post);

      expect(post.likes).toBe(5);
      expect(post.liked).toBe(false);
    });
  });

  describe('Validation - Formatage des initiales', () => {
    it('devrait formater le nom complet en initiales', () => {
      expect(component.getInitials('John Doe')).toBe('JD');
      expect(component.getInitials('Alice Johnson')).toBe('AJ');
    });

    it('devrait gérer un seul nom', () => {
      expect(component.getInitials('Alice')).toBe('A');
    });

    it('devrait retourner ?? si nom invalide', () => {
      expect(component.getInitials(null as any)).toBe('??');
    });
  });

  describe('Validation - URL de l\'image', () => {
    it('devrait générer l\'URL correcte de l\'image', () => {
      const url = component.getImageUrl(5);
      expect(url).toContain('http://localhost:8086');
      expect(url).toContain('/posts/image/5');
    });
  });

  it('devrait être créé', () => {
    expect(component).toBeTruthy();
  });
});