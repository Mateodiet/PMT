import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { TaskService } from '../../services/task.service';
import { task } from '../../shared/enum';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [NgbDropdownModule, ReactiveFormsModule],
  templateUrl: './project.component.html',
  styleUrl: './project.component.css'
})
export class ProjectComponent {
  task : task[] = []
    taskForm = new FormGroup({
      taskName : new FormControl('', Validators.required),
      taskDescription : new FormControl('', Validators.required),
      taskStatus : new FormControl('todo', Validators.required),
      creatorEmail : new FormControl('', Validators.required)
    })
    projectName : string = '';
    isEdit: boolean = false;
    todo: task[] = [];
progg: task[] = [];
done: task[] = [];
;
  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private taskService : TaskService,
    private route : ActivatedRoute
  ) {
    // customize default values of modals used by this component tree
    config.backdrop = 'static';
    config.keyboard = false;
    this.projectName = this.route.snapshot.params['id'];
  }
  ngOnInit(){
    this.getAllTasks();
  }
  open(content:any,isEdit : boolean, task ?: task) {
    this.taskForm.reset();
    this.isEdit = false;
    if(isEdit) {
      this.taskForm.get('taskName')?.setValue(task?.taskName!);
      this.taskForm.get('taskDescription')?.setValue(task?.taskDescription!);
      this.taskForm.get('taskStatus')?.setValue(task?.taskStatus!);
      this.taskForm.get('creatorEmail')?.setValue(localStorage.getItem('user')!);
      this.isEdit = true;
    }else{
      this.taskForm.get('creatorEmail')?.setValue(localStorage.getItem('user')!);
      this.taskForm.patchValue({ taskStatus: 'TODO' });
    }
    this.modalService.open(content);
  }

  saveForm(){
    if(this.isEdit) this.updatetask();
    else this.createtask();
  }
    createtask(){
      if(this.taskForm.invalid) return;
      const form : task = {
        taskName: this.taskForm.value.taskName  ?? '',
        taskDescription: this.taskForm.value.taskDescription  ?? '',
        taskStatus: this.taskForm.value.taskStatus  ?? '',
        creatorEmail: this.taskForm.value.creatorEmail  ?? '',
        projectName : this.projectName
      };
      this.taskService.createTask(form).subscribe({
        next : res => {
          this.getAllTasks();
          this.modalService.dismissAll('');
        }
      })
    }
    updatetask(){
      if(this.taskForm.invalid) return;
      const form : task = {
        taskName: this.taskForm.value.taskName  ?? '',
        taskDescription: this.taskForm.value.taskDescription  ?? '',
        creatorEmail: this.taskForm.value.creatorEmail  ?? '',
        taskStatus: this.taskForm.value.taskStatus  ?? '',
        projectName : this.projectName

      };
      this.taskService.updateTask(form.taskName!,form).subscribe({
        next : res => {
          this.getAllTasks();
          this.isEdit = false;
          this.modalService.dismissAll('');
        }
      })
    }
  
    deletetask(taskName : string){
      this.taskService.deleteTask(taskName).subscribe({
        next: res => {
          this.getAllTasks();
        }
      })
    }
    
    getAllTasks(){
      this.taskService.getTasksInProject(this.projectName).subscribe({
        next : res => {
          this.task = res.data;
          this.todo = this.task.filter(tasks => tasks.taskStatus == 'TODO');
          this.progg = this.task.filter(tasks => tasks.taskStatus == 'IN_PROGRESS');
          this.done = this.task.filter(tasks => tasks.taskStatus == 'DONE');
          console.log(this.todo,this.progg );
          
        }
      })
    }
}
