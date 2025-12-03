import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
projectCount!: string;
  todoCount!: string;
  progCount!: string;
  doneCount!: string;
constructor(private userService : UserService){}
ngOnInit(){
  this.getProjects();
  this.getTaskDone();
  this.getTasktodo();
  this.getTaskProg();
}
getProjects(){
  this.userService.getDashboardData().subscribe({
    next: res => {
      this.projectCount = res?.data?.count;
    }
  })
}
getTasktodo(){
  this.userService.getDashboardTasks('TODO').subscribe({
    next: res => {
      this.todoCount = res?.data?.count;
    }
  })
}
getTaskProg(){
  this.userService.getDashboardTasks('IN_PROGRESS').subscribe({
    next: res => {
      this.progCount = res?.data?.count;
    }
  })
}
getTaskDone(){
  this.userService.getDashboardTasks('DONE').subscribe({
    next: res => {
      this.doneCount = res?.data?.count;
    }
  })
}
}
