import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call getAllUsers endpoint', () => {
    service.getAllUsers().subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/getAllUsers'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call loginUser endpoint', () => {
    const mockUser = { email: 'test@test.com', password: 'password123' };
    
    service.loginUser(mockUser).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/login'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call signUpUser endpoint', () => {
    const mockUser = { email: 'test@test.com', password: 'password123', name: 'Test' };
    
    service.signUpUser(mockUser).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/signup'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call getUserById endpoint', () => {
    service.getUserById('test@test.com').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/getUserByEmail'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call deleteUser endpoint', () => {
    service.deleteUser('test@test.com').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/deleteUser'));
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('should call updateUser endpoint', () => {
    const mockUser = { name: 'Updated Name' };
    
    service.updateUser('test@test.com', mockUser).subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/user/updateUser'));
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true });
  });

  it('should call getDashboardData endpoint', () => {
    service.getDashboardData().subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/dashboard/getTotalProjects'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call getDashboardTasks endpoint', () => {
    service.getDashboardTasks('TODO').subscribe((response: any) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((r) => r.url.includes('/dashboard/getTaskByStatus'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});