import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserComponent } from './user.component';
import { UserService } from '../../services/user.service';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let modalServiceSpy: jasmine.SpyObj<NgbModal>;

  const mockUsers = [
    { userId: 1, name: 'User 1', email: 'user1@test.com', password: '****', role: 'admin', contactNumber: '0601020304' },
    { userId: 2, name: 'User 2', email: 'user2@test.com', password: '****', role: 'developer', contactNumber: '0605060708' }
  ];

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getAllUsers', 'updateUser', 'deleteUser']);
    modalServiceSpy = jasmine.createSpyObj('NgbModal', ['open', 'dismissAll']);

    userServiceSpy.getAllUsers.and.returnValue(of({ data: mockUsers }));

    await TestBed.configureTestingModule({
      imports: [UserComponent, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: NgbModal, useValue: modalServiceSpy },
        NgbModalConfig
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
  });

  // Test 1: Création
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: Initialisation des utilisateurs vide
  it('should initialize users array as empty', () => {
    expect(component.users).toEqual([]);
  });

  // Test 3: ngOnInit appelle getAllUsers
  it('should call getAllUsers on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    
    expect(userServiceSpy.getAllUsers).toHaveBeenCalled();
    expect(component.users).toEqual(mockUsers);
  }));

  // Test 4: Formulaire invalide si vide
  it('should have invalid form when empty', () => {
    expect(component.userForm.valid).toBeFalse();
  });

  // Test 5: Formulaire valide avec données requises
  it('should have valid form when required fields are filled', () => {
    component.userForm.get('name')?.setValue('Test User');
    component.userForm.get('email')?.setValue('test@test.com');
    component.userForm.get('password')?.setValue('password123');
    
    expect(component.userForm.valid).toBeTrue();
  });

  // Test 6: open() remplit le formulaire avec les données utilisateur
  it('should fill form with user data when opening modal', () => {
    const user = { name: 'John', email: 'john@test.com', password: '****', role: 'admin', contactNumber: '0601020304' };
    
    component.open({}, user);
    
    expect(component.userForm.get('name')?.value).toBe('John');
    expect(component.userForm.get('email')?.value).toBe('john@test.com');
    expect(component.userForm.get('password')?.value).toBe('');
    expect(component.userForm.get('role')?.value).toBe('admin');
    expect(modalServiceSpy.open).toHaveBeenCalled();
  });

  // Test 7: saveUser() ne fait rien si formulaire invalide
  it('should not update user if form is invalid', () => {
    component.saveUser();
    expect(userServiceSpy.updateUser).not.toHaveBeenCalled();
  });

  // Test 8: saveUser() appelle le service avec les bonnes données
  it('should call updateUser with correct data', fakeAsync(() => {
    userServiceSpy.updateUser.and.returnValue(of({ data: {} }));
    
    component.userForm.get('name')?.setValue('Updated Name');
    component.userForm.get('email')?.setValue('updated@test.com');
    component.userForm.get('password')?.setValue('newpass123');
    component.userForm.get('role')?.setValue('developer');
    component.userForm.get('contactNumber')?.setValue('0609101112');
    
    component.saveUser();
    tick();
    
    expect(userServiceSpy.updateUser).toHaveBeenCalledWith('updated@test.com', jasmine.objectContaining({
      name: 'Updated Name',
      email: 'updated@test.com'
    }));
  }));

  // Test 9: deleteUserById() appelle le service
  it('should call deleteUser service', fakeAsync(() => {
    userServiceSpy.deleteUser.and.returnValue(of({ data: {} }));
    
    component.deleteUserById('user1@test.com');
    tick();
    
    expect(userServiceSpy.deleteUser).toHaveBeenCalledWith('user1@test.com');
  }));

  // Test 10: getAllUsers() met à jour la liste
  it('should update users list on getAllUsers', fakeAsync(() => {
    component.getAllUsers();
    tick();
    
    expect(component.users).toEqual(mockUsers);
  }));

  // Test 11: Le champ name a le validateur required
  it('should have required validator on name field', () => {
    expect(component.userForm.get('name')?.hasError('required')).toBeTrue();
  });

  // Test 12: Le champ email a le validateur required
  it('should have required validator on email field', () => {
    expect(component.userForm.get('email')?.hasError('required')).toBeTrue();
  });
});