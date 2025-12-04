import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

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
    const mockTask = { taskName: 'Test Task', taskDescription: 'Description' };
    
    service.createTask(mockTask).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/createtask'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call getAllTasks endpoint', () => {
    service.getAllTasks().subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getAllTasks'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call getTask endpoint', () => {
    service.getTask('Test Task').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getTask'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call getTaskByProject endpoint', () => {
    service.getTaskByProject('Test Project').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getTaskByProject'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call assignTask endpoint', () => {
    const mockAssign = { taskName: 'Test Task', userEmail: 'test@test.com' };
    
    service.assignTask(mockAssign).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/assignTask'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });
});