import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SignInComponent } from './sign-in.component';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('SignInComponent', () => {
  let component: SignInComponent;
  let fixture: ComponentFixture<SignInComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // Créer les mocks
    userServiceSpy = jasmine.createSpyObj('UserService', ['loginUser']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [SignInComponent, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SignInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: Le composant se crée
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: Le formulaire est initialisé avec des valeurs vides
  it('should initialize form with empty values', () => {
    expect(component.loginForm.get('email')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  // Test 3: Le formulaire est invalide quand vide
  it('should be invalid when form is empty', () => {
    expect(component.loginForm.valid).toBeFalse();
  });

  // Test 4: Le formulaire est valide avec email et mot de passe
  it('should be valid when email and password are filled', () => {
    component.loginForm.get('email')?.setValue('test@test.com');
    component.loginForm.get('password')?.setValue('password123');
    expect(component.loginForm.valid).toBeTrue();
  });

  // Test 5: login() ne fait rien si formulaire invalide
  it('should not call service when form is invalid', () => {
    component.login();
    expect(userServiceSpy.loginUser).not.toHaveBeenCalled();
  });

  // Test 6: login() appelle le service avec les bonnes données
  it('should call loginUser with correct data when form is valid', fakeAsync(() => {
    userServiceSpy.loginUser.and.returnValue(of({ responseCode: 'OK' }));
    
    component.loginForm.get('email')?.setValue('test@test.com');
    component.loginForm.get('password')?.setValue('password123');
    
    component.login();
    tick();
    
    expect(userServiceSpy.loginUser).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123'
    });
  }));

  // Test 7: login() redirige vers home après succès
  it('should navigate to home after successful login', fakeAsync(() => {
    userServiceSpy.loginUser.and.returnValue(of({ responseCode: 'OK' }));
    spyOn(localStorage, 'setItem');
    
    component.loginForm.get('email')?.setValue('test@test.com');
    component.loginForm.get('password')?.setValue('password123');
    
    component.login();
    tick();
    
    expect(localStorage.setItem).toHaveBeenCalledWith('loggedIn', 'true');
    expect(localStorage.setItem).toHaveBeenCalledWith('user', 'test@test.com');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['../home']);
  }));

  // Test 8: Le champ email a le validateur required
  it('should have required validator on email field', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('');
    expect(emailControl?.hasError('required')).toBeTrue();
  });

  // Test 9: Le champ password a le validateur required
  it('should have required validator on password field', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.hasError('required')).toBeTrue();
  });
});