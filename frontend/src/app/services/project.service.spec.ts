import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8081/api';

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

  it('should create a project', () => {
    const mockProject = { projectName: 'Test', projectDescription: 'Desc', creatorEmail: 'test@test.com' };
    service.createProject(mockProject).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/create`);
    expect(req.request.method).toBe('POST');
    req.flush({ responseCode: 200 });
  });

  it('should update a project', () => {
    const mockProject = { projectName: 'Test', projectDescription: 'Updated' };
    service.updateProject('Test', mockProject).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/updateProject/Test`);
    expect(req.request.method).toBe('PUT');
    req.flush({ responseCode: 200 });
  });

  it('should delete a project', () => {
    service.deleteProject('Test').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/deleteProject/Test`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ responseCode: 200 });
  });

  it('should get all projects', () => {
    service.getAllProject().subscribe(res => {
      expect(res.data).toEqual([]);
    });
    const req = httpMock.expectOne(`${baseUrl}/project/getAllProjects`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should get a single project', () => {
    service.getProject('Test').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/getProject?projectName=Test`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: {} });
  });

  it('should invite a member', () => {
    service.inviteMember('user@test.com', 'Project1', 'MEMBER', 'admin@test.com').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/inviteWithRole`);
    expect(req.request.method).toBe('POST');
    req.flush({ responseCode: 200 });
  });

  it('should accept an invitation', () => {
    service.acceptInvitation('user@test.com', 'Project1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/projectInviteAccept/user@test.com/Project1`);
    expect(req.request.method).toBe('GET');
    req.flush({ responseCode: 200 });
  });

  it('should get project members', () => {
    service.getProjectMembers('Project1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/getProjectMembers/Project1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: [] });
  });

  it('should update member role', () => {
    service.updateMemberRole('user@test.com', 'Project1', 'ADMIN').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/updateMemberRole/user@test.com/Project1/ADMIN`);
    expect(req.request.method).toBe('PUT');
    req.flush({ responseCode: 200 });
  });

  it('should remove a member', () => {
    service.removeMember('user@test.com', 'Project1').subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${baseUrl}/project/removeMember/user@test.com/Project1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ responseCode: 200 });
  });
});