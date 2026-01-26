import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Project } from '../shared/enum';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  baseUrl : string = 'http://localhost:8081/api';
  constructor(private http : HttpClient) { }

  createProject(project: Project) : Observable<any>{
    return this.http.post(`${this.baseUrl}/project/create`, project);
  }
  updateProject(projectName : string,project: Project) : Observable<any>{
    return this.http.put(`${this.baseUrl}/project/updateProject/${projectName}`, project);
  }
  deleteProject(projectName : string) : Observable<any>{
    return this.http.delete(`${this.baseUrl}/project/deleteProject/${projectName}`);
  }
  sendEmail(email : string,projectName : string) : Observable<any>{
    return this.http.get(`${this.baseUrl}/project/invite/${email}/${projectName}`);
  }
  getUsersInProject(projectname : string): Observable<any>{
    return this.http.get(`${this.baseUrl}/users/getUsersByProject?projectName=${projectname}`)
  }
  getAllProject(): Observable<any>{
    return this.http.get(`${this.baseUrl}/project/getAllProjects`)
  }
}