import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-accept-invite',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card shadow">
            <div class="card-body text-center p-5">
              @if(loading) {
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-3">Processing invitation...</p>
              }
              @if(!loading && success) {
                <div class="text-success mb-3">
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-check-circle" viewBox="0 0 16 16">
                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                    <path d="M10.97 4.97a.235.235 0 0 0-.02.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-1.071-1.05z"/>
                  </svg>
                </div>
                <h4>Invitation Accepted!</h4>
                <p class="text-muted">You have successfully joined the project "{{projectName}}"</p>
                <a routerLink="/home/projects" class="btn btn-primary">Go to Projects</a>
              }
              @if(!loading && !success) {
                <div class="text-danger mb-3">
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-x-circle" viewBox="0 0 16 16">
                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                  </svg>
                </div>
                <h4>Error</h4>
                <p class="text-muted">{{errorMessage}}</p>
                <a routerLink="/home" class="btn btn-secondary">Go Home</a>
              }
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AcceptInviteComponent implements OnInit {
  loading = true;
  success = false;
  errorMessage = '';
  projectName = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {}

  ngOnInit() {
    const email = this.route.snapshot.params['email'];
    const projectName = this.route.snapshot.params['projectName'];
    this.projectName = projectName;

    if (email && projectName) {
      this.projectService.acceptInvitation(email, projectName).subscribe({
        next: (res) => {
          this.loading = false;
          this.success = true;
        },
        error: (err) => {
          this.loading = false;
          this.success = false;
          this.errorMessage = err.error?.responseDesc || 'Failed to accept invitation';
        }
      });
    } else {
      this.loading = false;
      this.errorMessage = 'Invalid invitation link';
    }
  }
}