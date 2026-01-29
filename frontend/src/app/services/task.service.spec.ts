import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8081/api';

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

  it('should create a task', () => {
    const mockTask = { 
      taskName: 'Task1', 
      taskDescription: 'Desc', 
      taskStatus: 'TODO',
      projectName: 'Project1',
      creatorEmail: 'test@test.com'
    };
    service.createTask(mockTask).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/createtask`);
    expect(req.request.method).toBe('POST');
    req.flush({ responseCode: 200 });
  });

  it('should update a task', () => {
    const mockTask = { 
      taskName: 'Task1', 
      taskDescription: 'Updated', 
      taskStatus: 'IN_PROGRESS',
      projectName: 'Project1',
      creatorEmail: 'test@test.com'
    };
    service.updateTask('Task1', mockTask).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/updateTask/Task1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ responseCode: 200 });
  });

  it('should delete a task', () => {
    service.deleteTask('Task1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/deleteTask/Task1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ responseCode: 200 });
  });

  it('should get task by name', () => {
    service.getTaskByName('Task1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getTask?taskName=Task1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: {} });
  });

  it('should get all tasks', () => {
    service.getTasks().subscribe(res => {
      expect(res.data).toEqual([]);
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getAllTasks`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should get tasks in project', () => {
    service.getTasksInProject('Project1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getTaskByProject?projectName=Project1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should assign a task', () => {
    service.assignTask('Task1', 'user@test.com', 'admin@test.com').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/assignTask`);
    expect(req.request.method).toBe('POST');
    req.flush({ responseCode: 200 });
  });

  it('should get assigned tasks', () => {
    service.getAssignedTasks().subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getAssignedTasks`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should get tasks assigned to user', () => {
    service.getTasksAssignedToUser('user@test.com').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getAssignedToUser?email=user@test.com`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should get task history', () => {
    service.getTaskHistory('Task1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/tasks/getTaskHistory?taskName=Task1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: { history: [] } });
  });
});