import { environment } from 'src/environments/environment';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService]
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllPosts', () => {
    it('should fetch all posts from API', (done) => {
      const mockPosts = [
        { id: 1, title: 'Post 1', description: 'Description 1', likes: 5 },
        { id: 2, title: 'Post 2', description: 'Description 2', likes: 10 }
      ];

      service.getAllPosts().subscribe((posts) => {
        expect(posts.length).toBe(2);
        expect(posts).toEqual(mockPosts);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/getAll`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle error when fetching posts', (done) => {
      service.getAllPosts().subscribe(
        () => fail('should have failed with 500 error'),
        (error) => {
          expect(error.status).toBe(500);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/getAll`);
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('addPost', () => {
    it('should add a new post with FormData', (done) => {
      const title = 'New Post';
      const description = 'New Description';
      const mockFile = new File(['mock content'], 'test.jpg', { type: 'image/jpeg' });
      const mockResponse = { id: 3, title, description, imageType: 'jpg' };

      service.addPost(title, description, mockFile).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/add`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBeTruthy();
      req.flush(mockResponse);
    });

    it('should include title and description in FormData', (done) => {
      const title = 'Test Title';
      const description = 'Test Description';
      const mockFile = new File([''], 'test.jpg', { type: 'image/jpeg' });

      service.addPost(title, description, mockFile).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/add`);
      expect(req.request.body.get('title')).toBe(title);
      expect(req.request.body.get('description')).toBe(description);
      expect(req.request.body.get('image')).toBe(mockFile);
      req.flush({ id: 1 });
    });

    it('should handle error when adding post', (done) => {
      const mockFile = new File([''], 'test.jpg', { type: 'image/jpeg' });

      service.addPost('Title', 'Description', mockFile).subscribe(
        () => fail('should have failed with 400 error'),
        (error) => {
          expect(error.status).toBe(400);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/add`);
      req.flush('Bad request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('deletePost', () => {
    it('should delete a post by id', (done) => {
      const postId = 1;

      service.deletePost(postId).subscribe(() => {
        expect(true).toBeTruthy();
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/delete/${postId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle error when deleting post', (done) => {
      service.deletePost(999).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/delete/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('updatePost', () => {
    it('should update a post', (done) => {
      const postId = 1;
      const updatedPost = { title: 'Updated Title', description: 'Updated Description' };
      const mockResponse = { id: postId, ...updatedPost };

      service.updatePost(postId, updatedPost).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/update/${postId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedPost);
      req.flush(mockResponse);
    });

    it('should send correct data in update request', (done) => {
      const postId = 1;
      const postData = { title: 'New Title', description: 'New Desc' };

      service.updatePost(postId, postData).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/update/${postId}`);
      expect(req.request.body.title).toBe('New Title');
      expect(req.request.body.description).toBe('New Desc');
      req.flush({ id: postId });
    });
  });

  describe('likePost', () => {
    it('should like a post by id', (done) => {
      const postId = 1;

      service.likePost(postId).subscribe(() => {
        expect(true).toBeTruthy();
        done();
      });

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/like/${postId}`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true });
    });

    it('should handle error when liking post', (done) => {
      service.likePost(999).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
          done();
        }
      );

      const req = httpMock.expectOne(`${environment.baseUrl}/posts/like/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });
});


