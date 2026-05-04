import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = `${environment.baseUrl}`;

  constructor(private http: HttpClient) {}

  // ✅ No more X-User-Id header - backend gets user from JWT token automatically

  /*getAllPosts(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts/getAll?page=${page}&size=${size}`);
  }*/
 getAllPosts(page = 0, size = 10): Observable<any> {

  const userId = localStorage.getItem('UserIdConnect'); // ou ton storage

  let headers = new HttpHeaders();

  if (userId) {
    headers = headers.set('X-User-Id', userId);
  }

  return this.http.get<any>(
    `${this.apiUrl}/posts/getAll?page=${page}&size=${size}`,
    { headers }
  );
}

  likePost(postId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/posts/like/${postId}`, {});
  }

  dislikePost(postId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/posts/dislike/${postId}`, {});
  }

  searchPosts(keyword = '', category = '', sort = 'date', page = 0, size = 5): Observable<any> {
    let params = new HttpParams()
      .set('sort', sort)
      .set('page', page.toString())
      .set('size', size.toString());
    if (keyword.trim()) params = params.set('keyword', keyword.trim());
    if (category.trim()) params = params.set('category', category.trim());
    return this.http.get<any>(`${this.apiUrl}/posts/search`, { params });
  }

  addPost(title: string, description: string, category: string, image: File): Observable<any> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('category', category);
    formData.append('image', image);
    return this.http.post(`${this.apiUrl}/posts/add`, formData);
  }

  deletePost(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/posts/delete/${id}`);
  }

  updatePost(id: number, post: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/posts/update/${id}`, post);
  }

  getTopPosts(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts/stats/top`);
  }

  getGeneralStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts/stats/general`);
  }

  generateAIImage(prompt: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/posts/generate-image?prompt=${encodeURIComponent(prompt)}`, {});
  }

  addPostWithAIImage(title: string, description: string, category: string, imageUrl: string): Observable<any> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('category', category);
    formData.append('imageUrl', imageUrl);
    return this.http.post(`${this.apiUrl}/posts/add-with-url`, formData);
  }
}
