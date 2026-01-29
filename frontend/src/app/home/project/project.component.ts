import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbModal, NgbModalConfig, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { task } from '../../shared/enum';
import { ActivatedRoute } from '@angular/router';

interface Member {
  userId: number;
  email: string;
  name: string;
  role: string;
  status: string;
  joinedAt: string;
}

interface HistoryEntry {
  historyId: number;
  fieldName: string;
  oldValue: string;
  newValue: string;
  changeDescription: string;
  modifiedByEmail: string;
  modifiedAt: string;
}

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [NgbDropdownModule, ReactiveFormsModule, CommonModule, NgbNavModule],
  templateUrl: './project.component.html',
  styleUrl: './project.component.css'
})
export class ProjectComponent implements OnInit {
  task: task[] = [];
  members: Member[] = [];
  taskHistory: HistoryEntry[] = [];
  activeTab = 1;
  
  taskForm = new FormGroup({
    taskName: new FormControl('', Validators.required),
    taskDescription: new FormControl('', Validators.required),
    taskStatus: new FormControl('TODO', Validators.required),
    priority: new FormControl('MEDIUM', Validators.required),
    taskDueDate: new FormControl(''),
    creatorEmail: new FormControl('', Validators.required)
  });

  inviteForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    role: new FormControl('MEMBER', Validators.required)
  });

  assignForm = new FormGroup({
    taskName: new FormControl('', Validators.required),
    userEmail: new FormControl('', Validators.required)
  });

  projectName: string = '';
  isEdit: boolean = false;
  currentTaskName: string = '';
  todo: task[] = [];
  progg: task[] = [];
  done: task[] = [];
  currentUserEmail: string = '';
  currentUserRole: string = 'ADMIN'; // Valeur par défaut pour debug

  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private taskService: TaskService,
    private projectService: ProjectService,
    private route: ActivatedRoute
  ) {
    config.backdrop = 'static';
    config.keyboard = false;
    this.projectName = this.route.snapshot.params['id'];
    this.currentUserEmail = localStorage.getItem('user') || '';
  }

  ngOnInit() {
    console.log('Current user email:', this.currentUserEmail);
    this.getProjectMembers();
    this.getAllTasks();
  }

  openTaskModal(content: any, isEdit: boolean, task?: task) {
    console.log('Opening task modal, canModify:', this.canModify());
    this.taskForm.reset();
    this.isEdit = false;
    if (isEdit && task) {
      this.currentTaskName = task.taskName;
      this.taskForm.get('taskName')?.setValue(task.taskName);
      this.taskForm.get('taskDescription')?.setValue(task.taskDescription);
      this.taskForm.get('taskStatus')?.setValue(task.taskStatus);
      this.taskForm.get('priority')?.setValue(task.priority || 'MEDIUM');
      this.taskForm.get('taskDueDate')?.setValue(task.taskDueDate || '');
      this.taskForm.get('creatorEmail')?.setValue(this.currentUserEmail);
      this.isEdit = true;
    } else {
      this.taskForm.get('creatorEmail')?.setValue(this.currentUserEmail);
      this.taskForm.patchValue({ taskStatus: 'TODO', priority: 'MEDIUM' });
    }
    this.modalService.open(content, { size: 'lg' });
  }

  openInviteModal(content: any) {
    this.inviteForm.reset();
    this.inviteForm.patchValue({ role: 'MEMBER' });
    this.modalService.open(content);
  }

  openAssignModal(content: any, task: task) {
    console.log('Opening assign modal for task:', task.taskName);
    this.assignForm.reset();
    this.assignForm.get('taskName')?.setValue(task.taskName);
    this.modalService.open(content);
  }

  openHistoryModal(content: any, task: task) {
    this.taskService.getTaskHistory(task.taskName).subscribe({
      next: (res: any) => {
        this.taskHistory = res.data?.history || res.data || [];
        this.modalService.open(content, { size: 'lg' });
      },
      error: (err: any) => {
        console.error('Error getting history:', err);
        this.taskHistory = [];
        this.modalService.open(content, { size: 'lg' });
      }
    });
  }

  saveTask() {
    console.log('Saving task, isEdit:', this.isEdit);
    if (this.isEdit) {
      this.updateTask();
    } else {
      this.createTask();
    }
  }

  createTask() {
    if (this.taskForm.invalid) {
      console.log('Form invalid:', this.taskForm.errors);
      return;
    }
    const form: task = {
      taskName: this.taskForm.value.taskName ?? '',
      taskDescription: this.taskForm.value.taskDescription ?? '',
      taskStatus: this.taskForm.value.taskStatus ?? 'TODO',
      priority: this.taskForm.value.priority ?? 'MEDIUM',
      taskDueDate: this.taskForm.value.taskDueDate ?? '',
      creatorEmail: this.taskForm.value.creatorEmail ?? '',
      projectName: this.projectName
    };
    console.log('Creating task:', form);
    this.taskService.createTask(form).subscribe({
      next: (res: any) => {
        console.log('Task created:', res);
        this.getAllTasks();
        this.modalService.dismissAll();
      },
      error: (err: any) => {
        console.error('Error creating task:', err);
        alert('Error creating task: ' + (err.error?.responseDesc || 'Unknown error'));
      }
    });
  }

  updateTask() {
    if (this.taskForm.invalid) return;
    const form: task = {
      taskName: this.taskForm.value.taskName ?? '',
      taskDescription: this.taskForm.value.taskDescription ?? '',
      taskStatus: this.taskForm.value.taskStatus ?? '',
      priority: this.taskForm.value.priority ?? 'MEDIUM',
      taskDueDate: this.taskForm.value.taskDueDate ?? '',
      creatorEmail: this.taskForm.value.creatorEmail ?? '',
      projectName: this.projectName
    };
    console.log('Updating task:', this.currentTaskName, form);
    this.taskService.updateTask(this.currentTaskName, form).subscribe({
      next: (res: any) => {
        console.log('Task updated:', res);
        this.getAllTasks();
        this.isEdit = false;
        this.modalService.dismissAll();
      },
      error: (err: any) => {
        console.error('Error updating task:', err);
        alert('Error updating task: ' + (err.error?.responseDesc || 'Unknown error'));
      }
    });
  }

  deleteTask(taskName: string) {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(taskName).subscribe({
        next: (res: any) => {
          this.getAllTasks();
        },
        error: (err: any) => {
          alert('Error deleting task: ' + (err.error?.responseDesc || 'Unknown error'));
        }
      });
    }
  }

  getAllTasks() {
    this.taskService.getTasksInProject(this.projectName).subscribe({
      next: (res: any) => {
        console.log('Tasks loaded:', res);
        this.task = res.data || [];
        this.todo = this.task.filter(t => t.taskStatus === 'TODO');
        this.progg = this.task.filter(t => t.taskStatus === 'IN_PROGRESS');
        this.done = this.task.filter(t => t.taskStatus === 'DONE');
      },
      error: (err: any) => {
        console.error('Error loading tasks:', err);
        this.task = [];
        this.todo = [];
        this.progg = [];
        this.done = [];
      }
    });
  }

  getProjectMembers() {
    this.projectService.getProjectMembers(this.projectName).subscribe({
      next: (res: any) => {
        console.log('Members loaded:', res);
        this.members = res.data || [];
        
        // Trouver le rôle de l'utilisateur actuel
        const currentMember = this.members.find(
          (m: Member) => m.email?.toLowerCase() === this.currentUserEmail?.toLowerCase()
        );
        
        if (currentMember) {
          this.currentUserRole = (currentMember.role || '').toUpperCase();
          console.log('Current user role found:', this.currentUserRole);
        } else {
          // Si pas trouvé dans les membres, le créateur est ADMIN par défaut
          console.log('User not found in members, checking if creator...');
          this.currentUserRole = 'ADMIN'; // Fallback pour le créateur
        }
        
        console.log('Final role:', this.currentUserRole);
        console.log('canModify():', this.canModify());
        console.log('isAdmin():', this.isAdmin());
      },
      error: (err: any) => {
        console.error('Error loading members:', err);
        this.members = [];
        this.currentUserRole = 'ADMIN'; // Fallback
      }
    });
  }

  inviteMember() {
    if (this.inviteForm.invalid) return;
    const email = this.inviteForm.value.email ?? '';
    const role = this.inviteForm.value.role ?? 'MEMBER';
    
    this.projectService.inviteMember(email, this.projectName, role, this.currentUserEmail).subscribe({
      next: (res: any) => {
        alert('Invitation sent successfully! An email notification has been sent.');
        this.getProjectMembers();
        this.modalService.dismissAll();
      },
      error: (err: any) => {
        alert('Error: ' + (err.error?.responseDesc || 'Failed to send invitation'));
      }
    });
  }

  updateMemberRole(email: string, newRole: string) {
    this.projectService.updateMemberRole(email, this.projectName, newRole).subscribe({
      next: (res: any) => {
        this.getProjectMembers();
      },
      error: (err: any) => {
        alert('Error updating role: ' + (err.error?.responseDesc || 'Unknown error'));
      }
    });
  }

  removeMember(email: string) {
    if (confirm('Are you sure you want to remove this member from the project?')) {
      this.projectService.removeMember(email, this.projectName).subscribe({
        next: (res: any) => {
          this.getProjectMembers();
        },
        error: (err: any) => {
          alert('Error removing member: ' + (err.error?.responseDesc || 'Unknown error'));
        }
      });
    }
  }

  assignTask() {
    if (this.assignForm.invalid) return;
    const taskName = this.assignForm.value.taskName ?? '';
    const userEmail = this.assignForm.value.userEmail ?? '';
    
    console.log('Assigning task:', taskName, 'to:', userEmail);
    this.taskService.assignTask(taskName, userEmail, this.currentUserEmail).subscribe({
      next: (res: any) => {
        alert('Task assigned successfully! Email notification sent to ' + userEmail);
        this.modalService.dismissAll();
      },
      error: (err: any) => {
        alert('Error: ' + (err.error?.responseDesc || 'Failed to assign task'));
      }
    });
  }

  // Vérifie si l'utilisateur est ADMIN
  isAdmin(): boolean {
    const role = this.currentUserRole?.toUpperCase() || '';
    return role === 'ADMIN';
  }

  // Vérifie si l'utilisateur peut modifier (ADMIN ou MEMBER)
  canModify(): boolean {
    const role = this.currentUserRole?.toUpperCase() || '';
    return role === 'ADMIN' || role === 'MEMBER';
  }

  // Vérifie si l'utilisateur est OBSERVER
  isObserver(): boolean {
    const role = this.currentUserRole?.toUpperCase() || '';
    return role === 'OBSERVER';
  }

  getAcceptedMembers(): Member[] {
    return this.members.filter(m => m.status === 'accepted');
  }

  getPriorityClass(priority: string | undefined): string {
  switch ((priority || 'MEDIUM').toUpperCase()) {
    case 'CRITICAL': return 'bg-danger';
    case 'HIGH': return 'bg-warning text-dark';
    case 'MEDIUM': return 'bg-info';
    case 'LOW': return 'bg-secondary';
    default: return 'bg-info';
    }
  } 

  getRoleClass(role: string): string {
    switch ((role || '').toUpperCase()) {
      case 'ADMIN': return 'bg-primary';
      case 'MEMBER': return 'bg-success';
      case 'OBSERVER': return 'bg-secondary';
      default: return 'bg-secondary';
    }
  }

  getStatusClass(status: string): string {
    return status === 'accepted' ? 'bg-success' : 'bg-warning text-dark';
  }
}