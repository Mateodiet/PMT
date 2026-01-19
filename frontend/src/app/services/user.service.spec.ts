import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { users } from '../shared/enum';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  // MODIFIÉ : URL locale au lieu de ngrok
  baseUrl : string = 'http://localhost:8081/api';
  
  constructor(private http : HttpClient) { }

  getAllUsers(): Observable<any>{
    return this.http.get(`${this.baseUrl}/user/getAllUsers`);
  }
  loginUser(user : users): Observable<any>{
    return this.http.post(`${this.baseUrl}/user/login`,user);
  }
  signUpUser(user : users): Observable<any>{
    return this.http.post(`${this.baseUrl}/user/signup`,user);
  }
  getUserById(email : string): Observable<any>{
    return this.http.get(`${this.baseUrl}/user/getUserByEmail?email=${email}`);
  }
  deleteUser(email : string): Observable<any>{
    return this.http.delete(`${this.baseUrl}/user/deleteUser/${email}`);
  }
  updateUser(email : string,user : users): Observable<any>{
    return this.http.put(`${this.baseUrl}/user/updateUser/${email}`,user);
  }
  getDashboardData(): Observable<any>{
    return this.http.get(`${this.baseUrl}/dashboard/getTotalProjects`)
  }
  getDashboardTasks(taskStatus : string): Observable<any>{
    return this.http.get(`${this.baseUrl}/dashboard/getTaskByStatus/${taskStatus}`)
  }
}