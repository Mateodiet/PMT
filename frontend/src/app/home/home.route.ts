import { Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserComponent } from './user/user.component';
import { ProjectsComponent } from './projects/projects.component';
import { ProjectComponent } from './project/project.component';
import { AcceptInviteComponent } from './accept-invite/accept-invite.component';

export const HOME_ROUTES: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UserComponent },
      { path: 'projects', component: ProjectsComponent },
      { path: 'project/:id', component: ProjectComponent },
      { path: 'accept-invite/:email/:projectName', component: AcceptInviteComponent }
    ]
  }
];