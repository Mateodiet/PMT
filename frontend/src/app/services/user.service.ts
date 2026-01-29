import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { users } from '../shared/enum';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  baseUrl: string = 'http://localhost:8081/api';

  constructor(private http: HttpClient) { }

  // Auth
  registerUser(user: users): Observable<any> {
    return this.http.post(`${this.baseUrl}/user/signup`, user);
  }

  // Alias pour compatibilité
  signUpUser(user: users): Observable<any> {
    return this.registerUser(user);
  }

  loginUser(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/user/login`, user);
  }

  // User CRUD
  getAllUsers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/getAllUsers`);
  }

  getUserByEmail(email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/getUserByEmail?email=${email}`);
  }

  // Alias pour compatibilité (getUserById utilise l'email comme ID)
  getUserById(email: string): Observable<any> {
    return this.getUserByEmail(email);
  }

  updateUser(email: string, user: users): Observable<any> {
    return this.http.put(`${this.baseUrl}/user/updateUser/${email}`, user);
  }

  deleteUser(email: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/user/deleteUser/${email}`);
  }

  // Dashboard
  getDashboardData(): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard/getTotalProjects`);
  }

  getDashboardTasks(status: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard/getTaskByStatus/${status}`);
  }

  // Invitations
  getPendingInvitations(email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/getPendingInvitations?email=${email}`);
  }
}