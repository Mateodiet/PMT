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
    
    service.createProject(mockProject).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/create'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call getAllProjects endpoint', () => {
    service.getAllProjects().subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getAllProjects'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call getProject endpoint', () => {
    service.getProject('Test Project').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getProject'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call inviteUser endpoint', () => {
    service.inviteUser('test@test.com', 'Test Project').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/invite'));
    expect(req.request.method).toBe('GET');
    req.flush({ success: true });
  });
});