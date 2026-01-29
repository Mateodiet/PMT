import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Project } from '../shared/enum';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  baseUrl: string = 'http://localhost:8081/api';
  
  constructor(private http: HttpClient) { }

  createProject(project: Project): Observable<any> {
    return this.http.post(`${this.baseUrl}/project/create`, project);
  }

  updateProject(projectName: string, project: Project): Observable<any> {
    return this.http.put(`${this.baseUrl}/project/updateProject/${projectName}`, project);
  }

  deleteProject(projectName: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/project/deleteProject/${projectName}`);
  }

  getAllProject(): Observable<any> {
    return this.http.get(`${this.baseUrl}/project/getAllProjects`);
  }

  getProject(projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/project/getProject?projectName=${projectName}`);
  }

  // Invitation methods
  inviteMember(email: string, projectName: string, role: string, invitedBy: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/project/inviteWithRole`, {
      email,
      projectName,
      role,
      invitedBy
    });
  }

  inviteMemberSimple(email: string, projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/project/invite/${email}/${projectName}`);
  }

  acceptInvitation(email: string, projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/project/projectInviteAccept/${email}/${projectName}`);
  }

  // Member management
  getProjectMembers(projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/project/getProjectMembers/${projectName}`);
  }

  updateMemberRole(email: string, projectName: string, role: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/project/updateMemberRole/${email}/${projectName}/${role}`, {});
  }

  removeMember(email: string, projectName: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/project/removeMember/${email}/${projectName}`);
  }

  getUsersInProject(projectName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/getUsersByProject?projectName=${projectName}`);
  }
}