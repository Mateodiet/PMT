import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { UserService } from '../../services/user.service';
import { of } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', [
      'getDashboardData',
      'getDashboardTasks'
    ]);

    userServiceSpy.getDashboardData.and.returnValue(of({ data: { count: '5' } }));
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: { count: '3', projectList: [] } }));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.projectCount).toBe('0');
    expect(component.todoCount).toBe('0');
    expect(component.progCount).toBe('0');
    expect(component.doneCount).toBe('0');
  });

  it('should call all data fetching methods on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    expect(userServiceSpy.getDashboardData).toHaveBeenCalled();
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('TODO');
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('IN_PROGRESS');
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('DONE');
  }));

  it('should get projects count', fakeAsync(() => {
    component.getProjects();
    tick();

    expect(component.projectCount).toBe('5');
  }));

  it('should get TODO tasks', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ 
      data: { count: '10', projectList: [{ taskName: 'Task1' }] } 
    }));

    component.getTasksTodo();
    tick();

    expect(component.todoCount).toBe('10');
    expect(component.todoTasks.length).toBe(1);
  }));

  it('should get IN_PROGRESS tasks', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ 
      data: { count: '7', projectList: [{ taskName: 'Task2' }] } 
    }));

    component.getTaskProg();
    tick();

    expect(component.progCount).toBe('7');
    expect(component.progTasks.length).toBe(1);
  }));

  it('should get DONE tasks', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ 
      data: { count: '15', projectList: [{ taskName: 'Task3' }] } 
    }));

    component.getTaskDone();
    tick();

    expect(component.doneCount).toBe('15');
    expect(component.doneTasks.length).toBe(1);
  }));

  it('should handle null response gracefully for projects', fakeAsync(() => {
    userServiceSpy.getDashboardData.and.returnValue(of({ data: null }));

    component.getProjects();
    tick();

    expect(component.projectCount).toBe('0');
  }));

  it('should handle null response gracefully for tasks', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: null }));

    component.getTasksTodo();
    tick();

    expect(component.todoCount).toBe('0');
    expect(component.todoTasks).toEqual([]);
  }));

  it('should have empty task arrays initially', () => {
    expect(component.todoTasks).toEqual([]);
    expect(component.progTasks).toEqual([]);
    expect(component.doneTasks).toEqual([]);
  });
});