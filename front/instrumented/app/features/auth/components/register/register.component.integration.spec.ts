import { HttpClientModule } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { By } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { AuthService } from "../../services/auth.service";
import { RegisterComponent } from "./register.component";
import { RegisterRequest } from "../../interfaces/registerRequest.interface";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";

describe("Register Component Integration suites", () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  
  // Variables pour tests UI (avec mocks)
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;
  
  // Variables pour tests Service Integration (services réels)
  let authService: AuthService;
  let mockRouterForServiceTests: jest.Mocked<Router>;
  let httpTestingController: HttpTestingController;

  const mockRegisterRequest: RegisterRequest = {
    email: 'test@example.com',
    firstName: 'John',
    lastName: 'Doe',
    password: 'password123'
  };

  describe('UI Unit Tests (with mocked services)', () => {
    beforeEach(async () => {
      // Création des mocks pour les tests UI
      mockAuthService = {
        register: jest.fn()
      } as any;

      mockRouter = {
        navigate: jest.fn()
      } as any;

      await TestBed.configureTestingModule({
        declarations: [RegisterComponent],
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
          { provide: Router, useValue: mockRouter }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(RegisterComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    
    describe('Form Integration - Real user input', () => {
      it('should update formControl when email input is filled', () => {
        // Arrange - Localiser l'input email
        const emailInput = fixture.debugElement.query(By.css('input[formControlName="email"]')).nativeElement;
  
        // Act - Simuler la saisie utilisateur
        emailInput.value = "test@example.com";
        emailInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
  
        // Assert - Vérifier que le FormControl email est mis à jour
        expect(component.form.get('email')?.value).toBe('test@example.com');
        
      });

      it('should update formControl when firstName input is filled', () => {
        // Arrange - Localiser l'input firstName
        const firstNameInput = fixture.debugElement.query(By.css('input[formControlName="firstName"]')).nativeElement;
        // Act - Simuler la saisie utilisateur
        
        firstNameInput.value="George";
        firstNameInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        // Assert - Vérifier que le FormControl firstName est mis à jour
        expect(component.form.get('firstName')?.value).toBe('George');
        
      });

      it('should update formControl when lastName input is filled', () => {
        // Arrange - Localiser l'input lastName
        const lastNameInput = fixture.debugElement.query(By.css('input[formControlName="lastName"]')).nativeElement;
        // Act - Simuler la saisie utilisateur
        
        lastNameInput.value="George";
        lastNameInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        // Assert - Vérifier que le FormControl lastName est mis à jour
        expect(component.form.get('lastName')?.value).toBe('George');
        // Act - Simuler la saisie utilisateur
       
      });

      it('should update formControl when password input is filled', () => {
        // Arrange - Localiser l'input password
        const passwordInput = fixture.debugElement.query(By.css('input[formControlName="password"]')).nativeElement;
        // Act - Simuler la saisie utilisateur
        passwordInput.value="Password123!";
        passwordInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        // Assert - Vérifier que le FormControl password est mis à jour
        expect(component.form.get('password')?.value).toBe('Password123!');
      });

    });
  });

  describe('Service Integration Tests (with real services)', () => {
    beforeEach(async () => {
      // Création du mock Router pour éviter les erreurs de navigation
      mockRouterForServiceTests = {
        navigate: jest.fn().mockResolvedValue(true)
      } as any;

      await TestBed.configureTestingModule({
        declarations: [RegisterComponent],
        imports: [
          RouterTestingModule.withRoutes([]),
          BrowserAnimationsModule,
          HttpClientTestingModule,
          MatCardModule,
          MatIconModule,
          MatFormFieldModule,
          MatInputModule,
          ReactiveFormsModule
        ],
        providers: [
          AuthService,
          { provide: Router, useValue: mockRouterForServiceTests }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(RegisterComponent);
      component = fixture.componentInstance;
      authService = TestBed.inject(AuthService);
      httpTestingController = TestBed.inject(HttpTestingController);
      
      fixture.detectChanges();
    });

    afterEach(() => {
      // Vérifier qu'aucune requête HTTP n'est en attente
      httpTestingController.verify();
    });

    describe('Register Component Integration', () => {
      it('should call real AuthService.register and handle successful response', () => {
    // Arrange
    const registerData = {
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
    }
    component.form.patchValue(registerData);
    
    // Act
    component.submit();
    
    // Assert
    const req = httpTestingController.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerData);

    req.flush(null, { status: 204, statusText: 'No Content' });

    expect(mockRouterForServiceTests.navigate).toHaveBeenCalledWith(['/login']);
});

    });

    describe('Router Integration', () => {
      it('should navigate to /login after successful registration', () => {
        // Arrange - Préparer un register réussi et espionner router.navigate
        const registerData = {
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe',
            password: 'password123'
        }
        component.form.patchValue(registerData);
        const routerSpy = jest.spyOn(mockRouterForServiceTests, 'navigate');
        // Act - Effectuer le flow de registration complet
        component.submit();
        const req = httpTestingController.expectOne('api/auth/register');
        req.flush(null, { status: 204, statusText: 'No Content' });
  
        // Assert - Vérifier que :
        // - router.navigate est appelé avec ['/login']
        expect(routerSpy).toHaveBeenCalledWith(['/login']);
      });

      it('should not navigate when registration fails', () => {
       const registerData = {
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe', 
            password: 'password123'
        }
        component.form.patchValue(registerData);
        const routerSpy = jest.spyOn(mockRouterForServiceTests, 'navigate');
        // Act - Effectuer le flow de registration complet
        component.submit();
        const req = httpTestingController.expectOne('api/auth/register');
        req.flush(null, { status: 409, statusText: 'Conflict' });
        
        // Assert - Vérifier que router.navigate n'est jamais appelé
        expect(routerSpy).not.toHaveBeenCalledWith(['/login']);
        
      });
    });
   
  });
});