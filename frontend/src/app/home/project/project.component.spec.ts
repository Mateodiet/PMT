import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectComponent } from './project.component';
import { TaskService } from '../../services/task.service';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('ProjectComponent', () => {
  let component: ProjectComponent;
  let fixture: ComponentFixture<ProjectComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let modalServiceSpy: jasmine.SpyObj<NgbModal>;

  const mockTasks = [
    { taskId: 1, taskName: 'Task 1', taskDescription: 'Desc 1', taskStatus: 'TODO', creatorEmail: 'test@test.com', projectName: 'Project1' },
    { taskId: 2, taskName: 'Task 2', taskDescription: 'Desc 2', taskStatus: 'IN_PROGRESS', creatorEmail: 'test@test.com', projectName: 'Project1' },
    { taskId: 3, taskName: 'Task 3', taskDescription: 'Desc 3', taskStatus: 'DONE', creatorEmail: 'test@test.com', projectName: 'Project1' }
  ];

  beforeEach(async () => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasksInProject', 'createTask', 'updateTask', 'deleteTask']);
    modalServiceSpy = jasmine.createSpyObj('NgbModal', ['open', 'dismissAll']);

    taskServiceSpy.getTasksInProject.and.returnValue(of({ data: mockTasks }));

    await TestBed.configureTestingModule({
      imports: [ProjectComponent, ReactiveFormsModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
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

  // Test 3: ngOnInit appelle getAllTasks
  it('should call getAllTasks on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    
    expect(taskServiceSpy.getTasksInProject).toHaveBeenCalledWith('Project1');
  }));

  // Test 4: getAllTasks filtre les tâches par statut
  it('should filter tasks by status', fakeAsync(() => {
    component.getAllTasks();
    tick();
    
    expect(component.todo.length).toBe(1);
    expect(component.progg.length).toBe(1);
    expect(component.done.length).toBe(1);
  }));

  // Test 5: open() en mode création
  it('should open modal for new task', () => {
    component.open({}, false);
    
    expect(component.isEdit).toBeFalse();
    expect(component.taskForm.get('taskStatus')?.value).toBe('TODO');
    expect(modalServiceSpy.open).toHaveBeenCalled();
  });

  // Test 6: open() en mode édition
  it('should open modal for edit task', () => {
    const task = { taskName: 'Test Task', taskDescription: 'Desc', taskStatus: 'IN_PROGRESS', creatorEmail: 'test@test.com', projectName: 'Project1' };
    
    component.open({}, true, task);
    
    expect(component.isEdit).toBeTrue();
    expect(component.taskForm.get('taskName')?.value).toBe('Test Task');
    expect(component.taskForm.get('taskStatus')?.value).toBe('IN_PROGRESS');
  });

  // Test 7: saveForm() appelle createtask si pas en mode édition
  it('should call createtask when saveForm and not editing', fakeAsync(() => {
    taskServiceSpy.createTask.and.returnValue(of({ data: {} }));
    component.isEdit = false;
    
    component.taskForm.get('taskName')?.setValue('New Task');
    component.taskForm.get('taskDescription')?.setValue('Description');
    component.taskForm.get('taskStatus')?.setValue('TODO');
    component.taskForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveForm();
    tick();
    
    expect(taskServiceSpy.createTask).toHaveBeenCalled();
  }));

  // Test 8: saveForm() appelle updatetask si en mode édition
  it('should call updatetask when saveForm and editing', fakeAsync(() => {
    taskServiceSpy.updateTask.and.returnValue(of({ data: {} }));
    component.isEdit = true;
    
    component.taskForm.get('taskName')?.setValue('Updated Task');
    component.taskForm.get('taskDescription')?.setValue('Updated Desc');
    component.taskForm.get('taskStatus')?.setValue('DONE');
    component.taskForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveForm();
    tick();
    
    expect(taskServiceSpy.updateTask).toHaveBeenCalled();
  }));

  // Test 9: createtask() ne fait rien si formulaire invalide
  it('should not create task if form is invalid', () => {
    component.createtask();
    expect(taskServiceSpy.createTask).not.toHaveBeenCalled();
  });

  // Test 10: updatetask() ne fait rien si formulaire invalide
  it('should not update task if form is invalid', () => {
    component.updatetask();
    expect(taskServiceSpy.updateTask).not.toHaveBeenCalled();
  });

  // Test 11: deletetask() appelle le service
  it('should call deleteTask service', fakeAsync(() => {
    taskServiceSpy.deleteTask.and.returnValue(of({ data: {} }));
    
    component.deletetask('Task1');
    tick();
    
    expect(taskServiceSpy.deleteTask).toHaveBeenCalledWith('Task1');
  }));

  // Test 12: Formulaire a les validateurs required
  it('should have required validators on form fields', () => {
    expect(component.taskForm.get('taskName')?.hasError('required')).toBeTrue();
    expect(component.taskForm.get('taskDescription')?.hasError('required')).toBeTrue();
  });
});