import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = 'http://localhost:8086/StreetLeague';

  constructor(private http: HttpClient) {}

 getAllPosts(page: number = 0, size: number = 10): Observable<any> {
  return this.http.get<any>(
    `${this.apiUrl}/posts/getAll?page=${page}&size=${size}`
  );
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

 likePost(postId: number) {
  return this.http.post<any>(`${this.apiUrl}/posts/like/${postId}`, {});
}

dislikePost(postId: number) {
  return this.http.post<any>(`${this.apiUrl}/posts/dislike/${postId}`, {});
}

getTopPosts(): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/posts/stats/top`);
}
searchPosts(keyword = '', category = '', sort = 'date', page = 0, size = 5): Observable<any> {
  let params = new HttpParams()
    .set('sort', sort)
    .set('page', page.toString())
    .set('size', size.toString());
  
  if (keyword.trim())  params = params.set('keyword', keyword.trim());
  if (category.trim()) params = params.set('category', category.trim());
  
  return this.http.get<any>(`${this.apiUrl}/posts/search`, { params });
}

getGeneralStats(): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/posts/stats/general`);
}

generateAIImage(prompt: string): Observable<any> {
  return this.http.post<any>(
    `${this.apiUrl}/posts/generate-image?prompt=${encodeURIComponent(prompt)}`,
    {}
  );
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