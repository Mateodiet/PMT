import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { task } from '../shared/enum';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  const mockTask: task = {
    taskName: 'Test Task',
    taskDescription: 'Description',
    taskStatus: 'TODO',
    projectName: 'Project1',
    creatorEmail: 'test@test.com'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call createTask endpoint', () => {
    service.createTask(mockTask).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/createtask'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call updateTask endpoint', () => {
    service.updateTask('Test Task', mockTask).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/updateTask'));
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true });
  });

  it('should call deleteTask endpoint', () => {
    service.deleteTask('Test Task').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/deleteTask'));
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('should call getTaskByName endpoint', () => {
    service.getTaskByName('Test Task').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/getTask'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call getTasks endpoint', () => {
    service.getTasks().subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/getAllTasks'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call getTasksInProject endpoint', () => {
    service.getTasksInProject('Test Project').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/tasks/getTaskByProject'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});