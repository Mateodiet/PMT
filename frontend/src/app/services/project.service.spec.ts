import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call createProject endpoint', () => {
    const mockProject = { projectName: 'Test Project', projectDescription: 'Description' };
    
    service.createProject(mockProject).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/project/create'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call updateProject endpoint', () => {
    const mockProject = { projectDescription: 'Updated Description' };
    
    service.updateProject('Test Project', mockProject).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/project/updateProject'));
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true });
  });

  it('should call deleteProject endpoint', () => {
    service.deleteProject('Test Project').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/project/deleteProject'));
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('should call sendEmail endpoint', () => {
    service.sendEmail('test@test.com', 'Test Project').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/project/invite'));
    expect(req.request.method).toBe('GET');
    req.flush({ success: true });
  });

  it('should call getUsersInProject endpoint', () => {
    service.getUsersInProject('Test Project').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/users/getUsersByProject'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call getAllProject endpoint', () => {
    service.getAllProject().subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/project/getAllProjects'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});