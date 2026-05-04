import { environment } from 'src/environments/environment';
/*import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommentService } from './comment.service';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CommentService]
    });
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCommentsByPost', () => {
    it('should fetch all comments for a post', (done) => {
      const postId = 1;
      const mockComments = [
        { id: 1, content: 'Great post!', user: { fullName: 'John Doe' }, postId: 1 },
        { id: 2, content: 'Thanks for sharing', user: { fullName: 'Jane Smith' }, postId: 1 }
      ];

      service.getCommentsByPost(postId).subscribe((comments) => {
        expect(comments.length).toBe(2);
        expect(comments).toEqual(mockComments);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/post/${postId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockComments);
    });

    it('should return empty array when no comments exist', (done) => {
      const postId = 999;

      service.getCommentsByPost(postId).subscribe((comments) => {
        expect(comments.length).toBe(0);
        expect(comments).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/post/${postId}`);
      req.flush([]);
    });

    it('should handle error when fetching comments', (done) => {
      service.getCommentsByPost(1).subscribe(
        () => fail('should have failed with 500 error'),
        (error) => {
          expect(error.status).toBe(500);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/post/1`);
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });

    it('should construct correct URL with postId', (done) => {
      const postId = 42;

      service.getCommentsByPost(postId).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/post/${postId}`);
      expect(req.request.url).toContain(`/comments/post/${postId}`);
      req.flush([]);
    });
  });

  describe('addComment', () => {
    it('should add a new comment to a post', (done) => {
      const commentData = { content: 'New comment', postId: 1 };
      const mockResponse = { id: 3, ...commentData, user: { fullName: 'Current User' } };

      service.addComment(commentData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/add`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(commentData);
      req.flush(mockResponse);
    });

    it('should send correct comment data', (done) => {
      const commentData = { content: 'Test comment', postId: 5 };

      service.addComment(commentData).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/add`);
      expect(req.request.body.content).toBe('Test comment');
      expect(req.request.body.postId).toBe(5);
      req.flush({ id: 1 });
    });

    it('should handle error when adding comment', (done) => {
      const commentData = { content: 'Comment', postId: 1 };

      service.addComment(commentData).subscribe(
        () => fail('should have failed with 400 error'),
        (error) => {
          expect(error.status).toBe(400);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/add`);
      req.flush('Invalid comment', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle error with 401 when not authenticated', (done) => {
      const commentData = { content: 'Comment', postId: 1 };

      service.addComment(commentData).subscribe(
        () => fail('should have failed with 401 error'),
        (error) => {
          expect(error.status).toBe(401);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/add`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('updateComment', () => {
    it('should update an existing comment', (done) => {
      const commentId = 1;
      const updatedData = { content: 'Updated comment', postId: 1 };
      const mockResponse = { id: commentId, ...updatedData };

      service.updateComment(commentId, updatedData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/update/${commentId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedData);
      req.flush(mockResponse);
    });

    it('should send correct updated data in request body', (done) => {
      const commentId = 2;
      const updatedData = { content: 'Modified text', postId: 3 };

      service.updateComment(commentId, updatedData).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/update/${commentId}`);
      expect(req.request.body.content).toBe('Modified text');
      expect(req.request.body.postId).toBe(3);
      req.flush({ id: commentId });
    });

    it('should handle error when updating non-existent comment', (done) => {
      const updatedData = { content: 'Comment', postId: 1 };

      service.updateComment(999, updatedData).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/update/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should construct correct URL with comment id', (done) => {
      const commentId = 42;
      const updatedData = { content: 'Test', postId: 1 };

      service.updateComment(commentId, updatedData).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/update/${commentId}`);
      expect(req.request.url).toContain(`/comments/update/${commentId}`);
      req.flush({ id: commentId });
    });
  });

  describe('deleteComment', () => {
    it('should delete a comment by id', (done) => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe(() => {
        expect(true).toBeTruthy();
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/delete/${commentId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle error when deleting non-existent comment', (done) => {
      service.deleteComment(999).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/delete/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should send delete request to correct endpoint', (done) => {
      const commentId = 5;

      service.deleteComment(commentId).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/delete/${commentId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should handle error with 403 when not authorized', (done) => {
      service.deleteComment(1).subscribe(
        () => fail('should have failed with 403 error'),
        (error) => {
          expect(error.status).toBe(403);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/comments/delete/1`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });
    });
  });
});*/


