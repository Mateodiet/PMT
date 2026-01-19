import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SignUpComponent } from './sign-up.component';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['signUpUser']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [SignUpComponent, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: Création du composant
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: Initialisation du formulaire
  it('should initialize form with default values', () => {
    expect(component.signupForm.get('name')?.value).toBe('');
    expect(component.signupForm.get('email')?.value).toBe('');
    expect(component.signupForm.get('password')?.value).toBe('');
    expect(component.signupForm.get('role')?.value).toBe('admin');
    expect(component.signupForm.get('contactNumber')?.value).toBe('');
  });

  // Test 3: Formulaire invalide si champs requis vides
  it('should be invalid when required fields are empty', () => {
    expect(component.signupForm.valid).toBeFalse();
  });

  // Test 4: Formulaire valide avec les champs requis remplis
  it('should be valid when required fields are filled', () => {
    component.signupForm.get('name')?.setValue('John Doe');
    component.signupForm.get('email')?.setValue('john@test.com');
    component.signupForm.get('password')?.setValue('password123');
    expect(component.signupForm.valid).toBeTrue();
  });

  // Test 5: signUpUser() ne fait rien si formulaire invalide
  it('should not call service when form is invalid', () => {
    component.signUpUser();
    expect(userServiceSpy.signUpUser).not.toHaveBeenCalled();
  });

  // Test 6: signUpUser() appelle le service avec les bonnes données
  it('should call signUpUser with correct data when form is valid', fakeAsync(() => {
    userServiceSpy.signUpUser.and.returnValue(of({ responseCode: 'OK' }));
    
    component.signupForm.get('name')?.setValue('John Doe');
    component.signupForm.get('email')?.setValue('john@test.com');
    component.signupForm.get('password')?.setValue('password123');
    component.signupForm.get('role')?.setValue('developer');
    component.signupForm.get('contactNumber')?.setValue('0601020304');
    
    component.signUpUser();
    tick();
    
    expect(userServiceSpy.signUpUser).toHaveBeenCalledWith(jasmine.objectContaining({
      name: 'John Doe',
      email: 'john@test.com',
      password: 'password123',
      role: 'developer',
      contactNumber: '0601020304',
      isActive: true
    }));
  }));

  // Test 7: Redirection vers signin après succès
  it('should navigate to signin after successful signup', fakeAsync(() => {
    userServiceSpy.signUpUser.and.returnValue(of({ responseCode: 'OK' }));
    
    component.signupForm.get('name')?.setValue('John Doe');
    component.signupForm.get('email')?.setValue('john@test.com');
    component.signupForm.get('password')?.setValue('password123');
    
    component.signUpUser();
    tick();
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(['../signin']);
  }));

  // Test 8: Le champ name a le validateur required
  it('should have required validator on name field', () => {
    const nameControl = component.signupForm.get('name');
    nameControl?.setValue('');
    expect(nameControl?.hasError('required')).toBeTrue();
  });

  // Test 9: Le rôle par défaut est admin
  it('should have admin as default role', () => {
    expect(component.signupForm.get('role')?.value).toBe('admin');
  });
});