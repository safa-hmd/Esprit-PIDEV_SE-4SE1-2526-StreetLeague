import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private apiUrl = 'http://localhost:8086/StreetLeague';

  constructor(private http: HttpClient) {}

  getCommentsByPost(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/comments/post/${postId}`);
  }

  addComment(comment: { content: string; postId: number }): Observable<any> {
    return this.http.post(`${this.apiUrl}/comments/add`, comment);
  }

  updateComment(id: number, comment: { content: string; postId: number }): Observable<any> {
    return this.http.put(`${this.apiUrl}/comments/update/${id}`, comment);
  }

  deleteComment(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/comments/delete/${id}`);
  }
}