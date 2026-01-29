import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { task } from '../shared/enum';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  baseUrl: string = 'http://localhost:8081/api';
  
  constructor(private http: HttpClient) { }

  createTask(task: task): Observable<any> {
    return this.http.post(`${this.baseUrl}/tasks/createtask`, task);
  }

  updateTask(taskName: string, task: task): Observable<any> {
    return this.http.put(`${this.baseUrl}/tasks/updateTask/${taskName}`, task);
  }

  deleteTask(taskName: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/tasks/deleteTask/${taskName}`);
  }

  getTaskByName(taskName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getTask?taskName=${taskName}`);
  }

  getTasks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getAllTasks`);
  }

  getTasksInProject(projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getTaskByProject?projectName=${projectName}`);
  }

  // Task assignment
  assignTask(taskName: string, userEmail: string, assignedByEmail: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/tasks/assignTask`, {
      taskName,
      userEmail,
      assignedByEmail
    });
  }

  getAssignedTasks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getAssignedTasks`);
  }

  getTasksAssignedToUser(email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getAssignedToUser?email=${email}`);
  }

  // Task history
  getTaskHistory(taskName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/tasks/getTaskHistory?taskName=${taskName}`);
  }
}