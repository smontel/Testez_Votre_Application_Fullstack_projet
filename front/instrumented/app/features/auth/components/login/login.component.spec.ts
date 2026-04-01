import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  
  // Mocks des services
  let mockAuthService: jest.Mocked<AuthService>;
  let mockSessionService: jest.Mocked<SessionService>;
  let mockRouter: jest.Mocked<Router>;

  // Données de test
  const mockLoginRequest: LoginRequest = {
    email: 'test@example.com',
    password: 'password123'
  };

  const mockSessionInformation: SessionInformation = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  beforeEach(async () => {
    // Création des mocks
    mockAuthService = {
      login: jest.fn()
    } as any;

    mockSessionService = {
      logIn: jest.fn()
    } as any;

    mockRouter = {
      navigate: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [RouterTestingModule,
                BrowserAnimationsModule,
                HttpClientModule,
                MatCardModule,
                MatIconModule,
                MatFormFieldModule,
                MatInputModule,
                ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  describe('Component initialization', () => {
    it('should initialize with default values', () => {
      expect(component.hide).toBe(true);
      expect(component.onError).toBe(false);
      expect(component.form).toBeDefined();
    });

    it('should initialize form with empty values', () => {
      expect(component.form.get('email')?.value).toBe('');
      expect(component.form.get('password')?.value).toBe('');
    });

    it('should have invalid form initially', () => {
      expect(component.form.valid).toBe(false);
    });
  });

  describe('Form validation', () => {
    describe('Email field', () => {
      it('should be required', () => {
        const emailControl = component.form.get('email');
        
        emailControl?.setValue('');
        emailControl?.updateValueAndValidity();
        expect(emailControl?.hasError('required')).toBe(true);
      });

      it('should validate email format', () => {
        const emailControl = component.form.get('email');
        
        emailControl?.setValue('invalid-email');
        emailControl?.updateValueAndValidity();
        expect(emailControl?.hasError('email')).toBeTruthy();
        
        emailControl?.setValue('valid@example.com');
        expect(emailControl?.hasError('email')).toBe(false);
      });

      it('should be valid with correct email', () => {
        const emailControl = component.form.get('email');
        
        emailControl?.setValue('test@example.com');
        expect(emailControl?.valid).toBe(true);
      });
    });

    describe('Password field', () => {
      it('should be required', () => {
        const passwordControl = component.form.get('password');
        
        passwordControl?.setValue('');
        expect(passwordControl?.hasError('required')).toBe(true);
      });

      it('should validate minimum length', () => {
        const passwordControl = component.form.get('password');
        
        passwordControl?.setValue('12'); // Moins de 3 caractères
        passwordControl?.markAsTouched();
        passwordControl?.updateValueAndValidity();

        expect(passwordControl?.hasError('minlength')).toBe(true);
        
        passwordControl?.setValue('123'); // Exactement 3 caractères
        
        expect(passwordControl?.hasError('minlength')).toBe(false);
      });

      it('should be valid with correct password', () => {
        const passwordControl = component.form.get('password');
        
        passwordControl?.setValue('password123');
        expect(passwordControl?.valid).toBe(true);
      });
    });

    describe('Complete form validation', () => {
      it('should be valid with correct email and password', () => {
        component.form.patchValue({
          email: 'test@example.com',
          password: 'password123'
        });
        
        expect(component.form.valid).toBe(true);
      });

      it('should be invalid with missing fields', () => {
        component.form.patchValue({
          email: 'test@example.com',
          password: ''
        });
        
        expect(component.form.valid).toBe(false);
      });
    });
  });

  describe('submit()', () => {
    beforeEach(() => {
      // Formulaire valide pour les tests
      component.form.patchValue({
        email: mockLoginRequest.email,
        password: mockLoginRequest.password
      });
    });

    it('should call authService.login with form values', () => {
      // Arrange
      mockAuthService.login.mockReturnValue(of(mockSessionInformation));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: mockLoginRequest.email,
        password: mockLoginRequest.password
      });
    });

    it('should handle successful login', () => {
      // Arrange
      mockAuthService.login.mockReturnValue(of(mockSessionInformation));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockSessionService.logIn).toHaveBeenCalledWith(mockSessionInformation);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
      expect(component.onError).toBe(false);
    });

    it('should handle login error', () => {
      // Arrange
      const error = { status: 401, message: 'Unauthorized' };
      mockAuthService.login.mockReturnValue(throwError(() => error));
      
      // Act
      component.submit();
      
      // Assert
      expect(component.onError).toBe(true);
      expect(mockSessionService.logIn).not.toHaveBeenCalled();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should not call sessionService.logIn on error', () => {
      // Arrange
      const error = { status: 500, message: 'Server error' };
      mockAuthService.login.mockReturnValue(throwError(() => error));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockSessionService.logIn).not.toHaveBeenCalled();
    });

    it('should not navigate on error', () => {
      // Arrange
      const error = { status: 401, message: 'Invalid credentials' };
      mockAuthService.login.mockReturnValue(throwError(() => error));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });
  });

  describe('Error handling scenarios', () => {
    beforeEach(() => {
      component.form.patchValue({
        email: 'test@example.com',
        password: 'password123'
      });
    });

    it('should handle network errors', () => {
      // Arrange
      const networkError = { status: 0, message: 'Network error' };
      mockAuthService.login.mockReturnValue(throwError(() => networkError));
      
      // Act
      component.submit();
      
      // Assert
      expect(component.onError).toBe(true);
    });

    it('should handle 401 unauthorized errors', () => {
      // Arrange
      const unauthorizedError = { status: 401, message: 'Invalid credentials' };
      mockAuthService.login.mockReturnValue(throwError(() => unauthorizedError));
      
      // Act
      component.submit();
      
      // Assert
      expect(component.onError).toBe(true);
    });

    it('should handle 500 server errors', () => {
      // Arrange
      const serverError = { status: 500, message: 'Internal server error' };
      mockAuthService.login.mockReturnValue(throwError(() => serverError));
      
      // Act
      component.submit();
      
      // Assert
      expect(component.onError).toBe(true);
    });
  });

  describe('Integration tests', () => {
    it('should complete full login flow with valid data', () => {
      // Arrange
      component.form.patchValue({
        email: 'admin@test.com',
        password: 'password123'
      });
      mockAuthService.login.mockReturnValue(of({
        ...mockSessionInformation,
        admin: true
      }));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: 'admin@test.com',
        password: 'password123'
      });
      expect(mockSessionService.logIn).toHaveBeenCalled();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
      expect(component.onError).toBe(false);
    });
  });

  describe('Component properties', () => {
    it('should toggle hide property', () => {
      // Test que la propriété hide peut être modifiée (pour le toggle du mot de passe)
      expect(component.hide).toBe(true);
      
      component.hide = false;
      expect(component.hide).toBe(false);
      
      component.hide = true;
      expect(component.hide).toBe(true);
    });

    it('should reset onError property', () => {
      component.onError = true;
      expect(component.onError).toBe(true);
      
      component.onError = false;
      expect(component.onError).toBe(false);
    });
  });


  
});
