import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectComponent } from './project.component';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('ProjectComponent', () => {
  let component: ProjectComponent;
  let fixture: ComponentFixture<ProjectComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let modalServiceSpy: jasmine.SpyObj<NgbModal>;

  const mockTasks = [
    { taskId: 1, taskName: 'Task 1', taskDescription: 'Desc 1', taskStatus: 'TODO', priority: 'MEDIUM', creatorEmail: 'test@test.com', projectName: 'Project1' },
    { taskId: 2, taskName: 'Task 2', taskDescription: 'Desc 2', taskStatus: 'IN_PROGRESS', priority: 'HIGH', creatorEmail: 'test@test.com', projectName: 'Project1' },
    { taskId: 3, taskName: 'Task 3', taskDescription: 'Desc 3', taskStatus: 'DONE', priority: 'LOW', creatorEmail: 'test@test.com', projectName: 'Project1' }
  ];

  const mockMembers = [
    { userId: 1, email: 'test@test.com', name: 'Test User', role: 'ADMIN', status: 'accepted', joinedAt: '2024-01-01' },
    { userId: 2, email: 'member@test.com', name: 'Member User', role: 'MEMBER', status: 'accepted', joinedAt: '2024-01-02' }
  ];

  beforeEach(async () => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasksInProject', 'createTask', 'updateTask', 'deleteTask', 'assignTask', 'getTaskHistory']);
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getProjectMembers', 'inviteMember', 'updateMemberRole', 'removeMember']);
    modalServiceSpy = jasmine.createSpyObj('NgbModal', ['open', 'dismissAll']);

    taskServiceSpy.getTasksInProject.and.returnValue(of({ data: mockTasks }));
    projectServiceSpy.getProjectMembers.and.returnValue(of({ data: mockMembers }));

    await TestBed.configureTestingModule({
      imports: [ProjectComponent, ReactiveFormsModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: NgbModal, useValue: modalServiceSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { params: { id: 'Project1' } } } },
        NgbModalConfig
      ]
    }).compileComponents();

    spyOn(localStorage, 'getItem').and.returnValue('test@test.com');

    fixture = TestBed.createComponent(ProjectComponent);
    component = fixture.componentInstance;
  });

  // Test 1: Création
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: Récupère le nom du projet depuis la route
  it('should get project name from route params', () => {
    expect(component.projectName).toBe('Project1');
  });

  // Test 3: ngOnInit appelle getAllTasks et getProjectMembers
  it('should call getAllTasks and getProjectMembers on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    
    expect(taskServiceSpy.getTasksInProject).toHaveBeenCalledWith('Project1');
    expect(projectServiceSpy.getProjectMembers).toHaveBeenCalledWith('Project1');
  }));

  // Test 4: getAllTasks filtre les tâches par statut
  it('should filter tasks by status', fakeAsync(() => {
    component.getAllTasks();
    tick();
    
    expect(component.todo.length).toBe(1);
    expect(component.progg.length).toBe(1);
    expect(component.done.length).toBe(1);
  }));

  // Test 5: openTaskModal() en mode création
  it('should open modal for new task', () => {
    component.openTaskModal({}, false);
    
    expect(component.isEdit).toBeFalse();
    expect(component.taskForm.get('taskStatus')?.value).toBe('TODO');
    expect(component.taskForm.get('priority')?.value).toBe('MEDIUM');
    expect(modalServiceSpy.open).toHaveBeenCalled();
  });

  // Test 6: openTaskModal() en mode édition
  it('should open modal for edit task', () => {
    const task = { taskName: 'Test Task', taskDescription: 'Desc', taskStatus: 'IN_PROGRESS', priority: 'HIGH', creatorEmail: 'test@test.com', projectName: 'Project1' };
    
    component.openTaskModal({}, true, task);
    
    expect(component.isEdit).toBeTrue();
    expect(component.taskForm.get('taskName')?.value).toBe('Test Task');
    expect(component.taskForm.get('taskStatus')?.value).toBe('IN_PROGRESS');
    expect(component.taskForm.get('priority')?.value).toBe('HIGH');
  });

  // Test 7: saveTask() appelle createTask si pas en mode édition
  it('should call createTask when saveTask and not editing', fakeAsync(() => {
    taskServiceSpy.createTask.and.returnValue(of({ data: {} }));
    component.isEdit = false;
    
    component.taskForm.get('taskName')?.setValue('New Task');
    component.taskForm.get('taskDescription')?.setValue('Description');
    component.taskForm.get('taskStatus')?.setValue('TODO');
    component.taskForm.get('priority')?.setValue('MEDIUM');
    component.taskForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveTask();
    tick();
    
    expect(taskServiceSpy.createTask).toHaveBeenCalled();
  }));

  // Test 8: saveTask() appelle updateTask si en mode édition
  it('should call updateTask when saveTask and editing', fakeAsync(() => {
    taskServiceSpy.updateTask.and.returnValue(of({ data: {} }));
    component.isEdit = true;
    component.currentTaskName = 'Updated Task';
    
    component.taskForm.get('taskName')?.setValue('Updated Task');
    component.taskForm.get('taskDescription')?.setValue('Updated Desc');
    component.taskForm.get('taskStatus')?.setValue('DONE');
    component.taskForm.get('priority')?.setValue('HIGH');
    component.taskForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveTask();
    tick();
    
    expect(taskServiceSpy.updateTask).toHaveBeenCalled();
  }));

  // Test 9: createTask() ne fait rien si formulaire invalide
  it('should not create task if form is invalid', () => {
    component.createTask();
    expect(taskServiceSpy.createTask).not.toHaveBeenCalled();
  });

  // Test 10: updateTask() ne fait rien si formulaire invalide
  it('should not update task if form is invalid', () => {
    component.updateTask();
    expect(taskServiceSpy.updateTask).not.toHaveBeenCalled();
  });

  // Test 11: deleteTask() appelle le service avec confirmation
  it('should call deleteTask service when confirmed', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    taskServiceSpy.deleteTask.and.returnValue(of({ data: {} }));
    
    component.deleteTask('Task1');
    tick();
    
    expect(taskServiceSpy.deleteTask).toHaveBeenCalledWith('Task1');
  }));

  // Test 12: deleteTask() ne fait rien si non confirmé
  it('should not delete task if not confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    
    component.deleteTask('Task1');
    
    expect(taskServiceSpy.deleteTask).not.toHaveBeenCalled();
  });

  // Test 13: Formulaire a les validateurs required
  it('should have required validators on form fields', () => {
    expect(component.taskForm.get('taskName')?.hasError('required')).toBeTrue();
    expect(component.taskForm.get('taskDescription')?.hasError('required')).toBeTrue();
  });

  // Test 14: getProjectMembers récupère les membres
  it('should get project members', fakeAsync(() => {
    component.getProjectMembers();
    tick();
    
    expect(projectServiceSpy.getProjectMembers).toHaveBeenCalledWith('Project1');
    expect(component.members.length).toBe(2);
  }));

  // Test 15: currentUserRole est défini après getProjectMembers
  it('should set currentUserRole from members', fakeAsync(() => {
    component.getProjectMembers();
    tick();
    
    expect(component.currentUserRole).toBe('ADMIN');
  }));

  // Test 16: canModify() retourne true pour ADMIN
  it('should return true for canModify when ADMIN', () => {
    component.currentUserRole = 'ADMIN';
    expect(component.canModify()).toBeTrue();
  });

  // Test 17: canModify() retourne true pour MEMBER
  it('should return true for canModify when MEMBER', () => {
    component.currentUserRole = 'MEMBER';
    expect(component.canModify()).toBeTrue();
  });

  // Test 18: canModify() retourne false pour OBSERVER
  it('should return false for canModify when OBSERVER', () => {
    component.currentUserRole = 'OBSERVER';
    expect(component.canModify()).toBeFalse();
  });

  // Test 19: isAdmin() retourne true pour ADMIN
  it('should return true for isAdmin when ADMIN', () => {
    component.currentUserRole = 'ADMIN';
    expect(component.isAdmin()).toBeTrue();
  });

  // Test 20: isAdmin() retourne false pour non-ADMIN
  it('should return false for isAdmin when not ADMIN', () => {
    component.currentUserRole = 'MEMBER';
    expect(component.isAdmin()).toBeFalse();
  });

  // Test 21: getAcceptedMembers filtre les membres acceptés
  it('should filter accepted members', () => {
    component.members = mockMembers;
    const accepted = component.getAcceptedMembers();
    expect(accepted.length).toBe(2);
  });

  // Test 22: inviteMember appelle le service
  it('should call inviteMember service', fakeAsync(() => {
    projectServiceSpy.inviteMember.and.returnValue(of({ responseCode: 200 }));
    spyOn(window, 'alert');
    
    component.inviteForm.get('email')?.setValue('new@test.com');
    component.inviteForm.get('role')?.setValue('MEMBER');
    
    component.inviteMember();
    tick();
    
    expect(projectServiceSpy.inviteMember).toHaveBeenCalled();
  }));

  // Test 23: inviteMember ne fait rien si formulaire invalide
  it('should not invite member if form is invalid', () => {
    component.inviteMember();
    expect(projectServiceSpy.inviteMember).not.toHaveBeenCalled();
  });

  // Test 24: assignTask appelle le service
  it('should call assignTask service', fakeAsync(() => {
    taskServiceSpy.assignTask.and.returnValue(of({ responseCode: 200 }));
    spyOn(window, 'alert');
    
    component.assignForm.get('taskName')?.setValue('Task1');
    component.assignForm.get('userEmail')?.setValue('member@test.com');
    
    component.assignTask();
    tick();
    
    expect(taskServiceSpy.assignTask).toHaveBeenCalled();
  }));

  // Test 25: assignTask ne fait rien si formulaire invalide
  it('should not assign task if form is invalid', () => {
    component.assignTask();
    expect(taskServiceSpy.assignTask).not.toHaveBeenCalled();
  });

  // Test 26: getPriorityClass retourne les bonnes classes
  it('should return correct priority classes', () => {
    expect(component.getPriorityClass('CRITICAL')).toBe('bg-danger');
    expect(component.getPriorityClass('HIGH')).toBe('bg-warning text-dark');
    expect(component.getPriorityClass('MEDIUM')).toBe('bg-info');
    expect(component.getPriorityClass('LOW')).toBe('bg-secondary');
  });

  // Test 27: getRoleClass retourne les bonnes classes
  it('should return correct role classes', () => {
    expect(component.getRoleClass('ADMIN')).toBe('bg-primary');
    expect(component.getRoleClass('MEMBER')).toBe('bg-success');
    expect(component.getRoleClass('OBSERVER')).toBe('bg-secondary');
  });

  // Test 28: updateMemberRole appelle le service
  it('should call updateMemberRole service', fakeAsync(() => {
    projectServiceSpy.updateMemberRole.and.returnValue(of({ responseCode: 200 }));
    
    component.updateMemberRole('member@test.com', 'ADMIN');
    tick();
    
    expect(projectServiceSpy.updateMemberRole).toHaveBeenCalledWith('member@test.com', 'Project1', 'ADMIN');
  }));

  // Test 29: removeMember appelle le service avec confirmation
  it('should call removeMember service when confirmed', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    projectServiceSpy.removeMember.and.returnValue(of({ responseCode: 200 }));
    
    component.removeMember('member@test.com');
    tick();
    
    expect(projectServiceSpy.removeMember).toHaveBeenCalledWith('member@test.com', 'Project1');
  }));

  // Test 30: removeMember ne fait rien si non confirmé
  it('should not remove member if not confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    
    component.removeMember('member@test.com');
    
    expect(projectServiceSpy.removeMember).not.toHaveBeenCalled();
  });
});