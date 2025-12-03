import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomeComponent } from './home.component';
import { ProjectComponent } from './project/project.component';
import { UserComponent } from './user/user.component';
import { ProjectsComponent } from './projects/projects.component';

export const HOME_ROUTES: Routes = [
    {
        path: '',
        component: HomeComponent,
        children: [
          { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
          { path: 'dashboard', component: DashboardComponent},
          { path: 'users', component: UserComponent},
          { path: 'projects', component: ProjectsComponent},
          { path: 'project/:id', component: ProjectComponent},
        ]
      }
];
