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

  it('should call signup endpoint', () => {
    const mockUser = { email: 'test@test.com', password: 'password123', name: 'Test' };
    
    service.signUp(mockUser).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/signup'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call login endpoint', () => {
    const mockLogin = { email: 'test@test.com', password: 'password123' };
    
    service.signIn(mockLogin).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/login'));
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should call getAllUsers endpoint', () => {
    service.getAllUsers().subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getAllUsers'));
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call getUserByEmail endpoint', () => {
    service.getUserByEmail('test@test.com').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(req => req.url.includes('/getUserByEmail'));
    expect(req.request.method).toBe('GET');
    req.flush({});
  });
});