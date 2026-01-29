import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { task } from '../../shared/enum';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  projectCount: string = '0';
  todoCount: string = '0';
  progCount: string = '0';
  doneCount: string = '0';
  
  todoTasks: task[] = [];
  progTasks: task[] = [];
  doneTasks: task[] = [];

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.getProjects();
    this.getTasksTodo();
    this.getTaskProg();
    this.getTaskDone();
  }

  getProjects() {
    this.userService.getDashboardData().subscribe({
      next: res => {
        this.projectCount = res?.data?.count || '0';
      }
    });
  }

  getTasksTodo() {
    this.userService.getDashboardTasks('TODO').subscribe({
      next: res => {
        this.todoCount = res?.data?.count || '0';
        this.todoTasks = res?.data?.projectList || [];
      }
    });
  }

  getTaskProg() {
    this.userService.getDashboardTasks('IN_PROGRESS').subscribe({
      next: res => {
        this.progCount = res?.data?.count || '0';
        this.progTasks = res?.data?.projectList || [];
      }
    });
  }

  getTaskDone() {
    this.userService.getDashboardTasks('DONE').subscribe({
      next: res => {
        this.doneCount = res?.data?.count || '0';
        this.doneTasks = res?.data?.projectList || [];
      }
    });
  }
}