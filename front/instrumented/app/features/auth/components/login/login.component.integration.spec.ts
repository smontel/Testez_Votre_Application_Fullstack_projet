import { HttpClientModule } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { By } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { Router, Routes } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { SessionService } from "src/app/services/session.service";
import { AuthService } from "../../services/auth.service";
import { LoginComponent } from "./login.component";
import { SessionInformation } from "src/app/interfaces/sessionInformation.interface";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { Component } from "@angular/core";

// Crée un composant vide pour servir de cible pour le routage
@Component({ template: '' })
class DummyComponent { }

// Définit les routes de test
const routes: Routes = [
  { path: 'sessions', component: DummyComponent }
];


describe("Login Component Integration suites", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  
  // Variables pour tests UI (avec mocks)
  let mockAuthService: jest.Mocked<AuthService>;
  let mockSessionService: jest.Mocked<SessionService>;
  let mockRouter: jest.Mocked<Router>;
  
  // Variables pour tests Service Integration (services réels)
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;
  let httpTestingController: HttpTestingController;

  const mockSessionInformation: SessionInformation = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  describe('UI Integration Tests (with mocked services)', () => {
    beforeEach(async () => {
      // Création des mocks pour les tests UI
      mockAuthService = {
        login: jest.fn()
      } as any;

      mockSessionService = {
        logIn: jest.fn()
      } as any;

      mockRouter = {
        navigate: jest.fn().mockResolvedValue(true)
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

    describe('Template Integration', () => {
      it('should show error message when onError is true', () => {
        // Simuler une erreur
        component.onError = true;
        fixture.detectChanges();
        
        // Vérifier que le message apparaît dans le DOM
        const errorElement = fixture.debugElement.query(By.css('.error'));
        expect(errorElement?.nativeElement.textContent).toContain('An error occurred');
      });
    });

    describe('User Interaction', () => {
      beforeEach(() => {
        component.form.patchValue({
          email: 'test@example.com',
          password: 'password123'
        });
        mockAuthService.login.mockReturnValue(of(mockSessionInformation));
        fixture.detectChanges();
      });

      it('should submit form when user clicks on submit button', () => {
        const submitSpy = jest.spyOn(component, 'submit');
        
        // Act - Simuler le clic utilisateur
        const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
        submitButton.nativeElement.click();
        
        // Assert - Vérifier que le clic a déclenché submit()
        expect(submitSpy).toHaveBeenCalled();
        expect(mockAuthService.login).toHaveBeenCalled();
      });

      it('should prevent submit to have been called when form is invalid', () => {
        // Arrange - Formulaire invalide
        component.form.patchValue({ email: '', password: '' });
        fixture.detectChanges();
        const submitSpy = jest.spyOn(component, 'submit');
        // Act - Tenter de cliquer
        const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
        submitButton.nativeElement.click();
        
        // Assert - submit() ne doit PAS être appelée
        expect(submitSpy).not.toHaveBeenCalled();
      });
    });

    describe('Form Integration - Real user input', () => {
      it('Should update formControl when email input the input is filled', () => {
        // Arrange
        const emailInput = fixture.debugElement.query(By.css('input[formControlName="email"]')).nativeElement;
        
        // Act
        emailInput.value = "bob@bob.com";
        emailInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        
        // Assert
        expect(component?.form.get('email')?.value).toBe('bob@bob.com');
      });

      it('Should update formcontrol when password input is filled', () => {
        // Arrange
        const passwordInput = fixture.debugElement.query(By.css('input[formControlName="password"]')).nativeElement;
        
        // Act
        passwordInput.value = "password123";
        passwordInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        
        // Assert
        expect(component?.form.get('password')?.value).toBe('password123');
      });

      it('devrait déclencher la validation en temps réel lors de la saisie', () => {
        // Arrange
        const emailInput = fixture.debugElement.query(By.css('input[formControlName="email"]')).nativeElement;
        const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
        // Act
        emailInput.value = "blob"
        emailInput.dispatchEvent(new Event('input'));
        emailInput.dispatchEvent(new Event('blur'));
        fixture.detectChanges();
        
        // Assert
        expect(component.form.get('email')?.hasError('email')).toBe(true);
        expect(submitButton.nativeElement.disabled).toBe(true);
      });
    });
  });

  describe('Service Integration Tests (with real services)', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [LoginComponent, DummyComponent],
        imports: [
          RouterTestingModule.withRoutes(routes),
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
          SessionService
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      authService = TestBed.inject(AuthService);
      sessionService = TestBed.inject(SessionService);
      router = TestBed.inject(Router);
      httpTestingController = TestBed.inject(HttpTestingController);
      
      fixture.detectChanges();
    });

    afterEach(() => {
      // Vérifier qu'aucune requête HTTP n'est en attente
      httpTestingController.verify();
    });

    describe('Login Component Integration', () => {
      it('should call real AuthService.login and handle successful response', () => {
        // Arrange - Préparer les données de login et espionner la navigation
        const loginData = {
          email: 'test@example.com',
          password: 'Password123!'
        };
        
        component.form.patchValue(loginData);
        
        const sessionServiceSpy = jest.spyOn(sessionService, 'logIn');
        const routerSpy = jest.spyOn(router, 'navigate');
        
        // Act - Appeler component.submit() qui utilise le vrai AuthService
        component.submit();
        
        // Assert - Vérifier l'intégration complète :
        // - Requête HTTP POST envoyée avec les bonnes données
        const req = httpTestingController.expectOne('api/auth/login');
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(loginData);
        
        // - Réponse HTTP simulée
        req.flush(mockSessionInformation);
        
        // - SessionService.logIn appelé avec la réponse
        expect(sessionServiceSpy).toHaveBeenCalledWith(mockSessionInformation);
        
        // - Navigation déclenchée
        expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
        
        // - Pas d'erreur dans le composant
        expect(component.onError).toBe(false);
      });


      it('should pass correct LoginRequest format to AuthService', () => {
        // Arrange - Remplir le formulaire avec des données spécifiques
        const loginData = {
          email:'test@example.com',
          password:'Password123!'
        }
        component.form.patchValue(loginData);
        // Act - Soumettre le formulaire
        component.submit();
        // Assert - Vérifier que les données envoyées au service correspondent exactement
        // au format LoginRequest attendu
        const req = httpTestingController.expectOne('api/auth/login');
        expect(req.request.body).toEqual(loginData);
        
        
      });
    });

    describe('Login Component Integration', () => {
      it('should call real SessionService.logIn with AuthService response', () => {
        // Arrange - Préparer un login réussi et espionner SessionService
        const loginData = {
          email:'test@example.com',
          password:'Password123!'
        }
        const serviceSpy = jest.spyOn(sessionService, 'logIn');
        component.form.patchValue(loginData);
        
        // Act - Effectuer le login complet
        component.submit();
        const req = httpTestingController.expectOne('api/auth/login');
        req.flush(mockSessionInformation);
        // Assert - Vérifier que :
        // - SessionService.logIn est appelé avec l'objet SessionInformation exact 
        expect(serviceSpy).toBeCalledWith(mockSessionInformation);       
      });

      it('should not call SessionService.logIn when AuthService fails', () => {
        // Arrange - Préparer un échec d'authentification et espionner SessionService
        const loginData={
          email:'',
          password:''
        }
        const serviceSpy = jest.spyOn(sessionService, 'logIn');
        // Act - Tenter un login qui échoue
        component.submit();
        const req = httpTestingController.expectOne('api/auth/login');
        req.flush('Error message', { status: 401, statusText: 'Unauthorized' });

        
        // Assert - Vérifier que SessionService.logIn n'est jamais appelé
        expect(serviceSpy).not.toHaveBeenCalled();
      });
    });

    describe('Router Integration', () => {
      it('should navigate to /sessions after successful login flow', () => {
        // Arrange - Préparer un login réussi et espionner router.navigate
        const loginData={
          email:'test@example.com',
          password:'Password123!'
        }
        const routerSpy = jest.spyOn(router, 'navigate');
         component.form.patchValue(loginData);
        // Act - Effectuer le flow de login complet
        component.submit();
        const req = httpTestingController.expectOne('api/auth/login');
        req.flush(mockSessionInformation);

        // Assert - Vérifier que :
        // - router.navigate est appelé avec ['/sessions']
        expect(routerSpy).toHaveBeenCalledWith(['/sessions'])
        
        
      });

      it('should not navigate when login fails', () => {
        // Arrange - Préparer un échec de login et espionner router.navigate
        const loginData={
          email:'',
          password:''
        }
        const routerSpy = jest.spyOn(router, 'navigate');
        
        // Act - Tenter un login qui échoue
        component.submit();
        const req = httpTestingController.expectOne('api/auth/login');
        req.flush('Error message', { status: 401, statusText: 'Unauthorized' });

        // Assert - Vérifier que router.navigate n'est jamais appelé
        expect(routerSpy).not.toHaveBeenCalled();
      });
    });

  });
});