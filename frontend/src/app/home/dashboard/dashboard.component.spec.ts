import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { UserService } from '../../services/user.service';
import { of } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getDashboardData', 'getDashboardTasks']);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    // Setup default responses
    userServiceSpy.getDashboardData.and.returnValue(of({ data: { count: '5' } }));
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: { count: '3' } }));

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  // Test 1: Création
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: ngOnInit appelle toutes les méthodes
  it('should call all data methods on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    
    expect(userServiceSpy.getDashboardData).toHaveBeenCalled();
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('TODO');
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('IN_PROGRESS');
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('DONE');
  }));

  // Test 3: getProjects met à jour projectCount
  it('should update projectCount on getProjects', fakeAsync(() => {
    userServiceSpy.getDashboardData.and.returnValue(of({ data: { count: '10' } }));
    
    component.getProjects();
    tick();
    
    expect(component.projectCount).toBe('10');
  }));

  // Test 4: getTasktodo met à jour todoCount
  it('should update todoCount on getTasktodo', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: { count: '7' } }));
    
    component.getTasktodo();
    tick();
    
    expect(component.todoCount).toBe('7');
  }));

  // Test 5: getTaskProg met à jour progCount
  it('should update progCount on getTaskProg', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: { count: '4' } }));
    
    component.getTaskProg();
    tick();
    
    expect(component.progCount).toBe('4');
  }));

  // Test 6: getTaskDone met à jour doneCount
  it('should update doneCount on getTaskDone', fakeAsync(() => {
    userServiceSpy.getDashboardTasks.and.returnValue(of({ data: { count: '12' } }));
    
    component.getTaskDone();
    tick();
    
    expect(component.doneCount).toBe('12');
  }));

  // Test 7: Appel avec le bon paramètre pour TODO
  it('should call getDashboardTasks with TODO parameter', fakeAsync(() => {
    component.getTasktodo();
    tick();
    
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('TODO');
  }));

  // Test 8: Appel avec le bon paramètre pour IN_PROGRESS
  it('should call getDashboardTasks with IN_PROGRESS parameter', fakeAsync(() => {
    component.getTaskProg();
    tick();
    
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('IN_PROGRESS');
  }));

  // Test 9: Appel avec le bon paramètre pour DONE
  it('should call getDashboardTasks with DONE parameter', fakeAsync(() => {
    component.getTaskDone();
    tick();
    
    expect(userServiceSpy.getDashboardTasks).toHaveBeenCalledWith('DONE');
  }));
});