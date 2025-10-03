import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  

  beforeEach(async () => {
    
 const authServiceMock = {
      register: jest.fn(),
      login: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  afterEach(() => {
    // Nettoyage des mocks après chaque test
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Initialization', () => {
    it('should initialize form with empty values', () => {
      expect(component.form.get('email')?.value).toBe('');
      expect(component.form.get('firstName')?.value).toBe('');
      expect(component.form.get('lastName')?.value).toBe('');
      expect(component.form.get('password')?.value).toBe('');
    });

    it('should initialize onError to false', () => {
      expect(component.onError).toBe(false);
    });

    it('should have invalid form initially', () => {
      expect(component.form.valid).toBe(false);
    });
  });

  describe('Form Validation', () => {
    it('should require email', () => {
      const emailControl = component.form.get('email');
      expect(emailControl?.hasError('required')).toBe(true);
      
      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('required')).toBe(false);
    });

    it('should validate email format', () => {
      const emailControl = component.form.get('email');
      
      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBe(true);
      
      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('email')).toBe(false);
    });

    it('should require firstName', () => {
      const firstNameControl = component.form.get('firstName');
      expect(firstNameControl?.hasError('required')).toBe(true);
      
      firstNameControl?.setValue('John');
      expect(firstNameControl?.hasError('required')).toBe(false);
    });

    it('should validate firstName minimum length', () => {
      const firstNameControl = component.form.get('firstName');
      
      firstNameControl?.setValue('J');
      firstNameControl?.updateValueAndValidity();
      expect(firstNameControl?.hasError('minlength')).toBe(true);
      
      firstNameControl?.setValue('John');
      expect(firstNameControl?.hasError('minlength')).toBe(false);
    });

    it('should validate firstName maximum length', () => {
      const firstNameControl = component.form.get('firstName');
      const longName = 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa';
      
      firstNameControl?.setValue(longName);
      firstNameControl?.updateValueAndValidity();
      expect(firstNameControl?.hasError('maxlength')).toBe(true);
      
      firstNameControl?.setValue('John');
      expect(firstNameControl?.hasError('maxlength')).toBe(false);
    });

    it('should require lastName', () => {
      const lastNameControl = component.form.get('lastName');
      expect(lastNameControl?.hasError('required')).toBe(true);
      
      lastNameControl?.setValue('Doe');
      expect(lastNameControl?.hasError('required')).toBe(false);
    });

    it('should validate lastName minimum and maximum length', () => {
      const lastNameControl = component.form.get('lastName');
      
      // Test minimum length
      lastNameControl?.setValue('o');
      expect(lastNameControl?.hasError('minlength')).toBe(true);
      
      // Test maximum length
      
      lastNameControl?.setValue('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa');
      expect(lastNameControl?.hasError('maxlength')).toBe(true);
      
      // Test valid length
      lastNameControl?.setValue('Doe');
      expect(lastNameControl?.hasError('minlength')).toBe(false);
      expect(lastNameControl?.hasError('maxlength')).toBe(false);
    });

    it('should require password', () => {
      const passwordControl = component.form.get('password');
      expect(passwordControl?.hasError('required')).toBe(true);
      
      passwordControl?.setValue('password123');
      expect(passwordControl?.hasError('required')).toBe(false);
    });

    it('should validate password minimum and maximum length', () => {
      const passwordControl = component.form.get('password');
      
      // Test minimum length
      passwordControl?.setValue('p');
      expect(passwordControl?.hasError('minlength')).toBe(true);
      
      // Test maximum length
      const longPassword = 'a'.repeat(41);
      passwordControl?.setValue(longPassword);
      expect(passwordControl?.hasError('maxlength')).toBe(true);
      
      // Test valid length
      passwordControl?.setValue('password123');
      expect(passwordControl?.hasError('minlength')).toBe(false);
      expect(passwordControl?.hasError('maxlength')).toBe(false);
    });

    it('should be valid when all fields are correctly filled', () => {
      component.form.patchValue({
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      });

      expect(component.form.valid).toBe(true);
    });
  });

  describe('Submit Method', () => {
    beforeEach(() => {
      // Setup valid form data
      component.form.patchValue({
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      });
    });

    it('should call authService.register with form values', () => {
      authService.register.mockReturnValue(of(void 0));
      
      component.submit();
      
      const expectedRequest: RegisterRequest = {
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      };
      
      expect(authService.register).toHaveBeenCalledWith(expectedRequest);
    });

    
    it('should navigate to login on successful registration', () => {
      authService.register.mockReturnValue(of(void 0));
      
      component.submit();
      
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });


    it('should set onError to true on registration failure', () => {
      authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));
      
      component.submit();
      
      expect(component.onError).toBe(true);
    });

    it('should not navigate to login on registration failure', () => {
      authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));
      
      component.submit();
      
      expect(router.navigate).not.toHaveBeenCalled();
    });
   
  });

  });
