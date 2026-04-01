import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { ActivatedRoute, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { By } from "@angular/platform-browser";
import { of, throwError } from "rxjs";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";

import { DetailComponent } from "./detail.component";
import { SessionService } from "../../../../services/session.service";
import { TeacherService } from "../../../../services/teacher.service";
import { SessionApiService } from "../../services/session-api.service";
import { Teacher } from "../../../../interfaces/teacher.interface";
import { Session } from "../../interfaces/session.interface";
import { SessionInformation } from "../../../../interfaces/sessionInformation.interface";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

describe("Detail Component Integration suites", () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  
  // Variables pour tests UI (avec mocks)
  let mockSessionService: jest.Mocked<SessionService>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockTeacherService: jest.Mocked<TeacherService>;
  let mockMatSnackBar: jest.Mocked<MatSnackBar>;
  let mockRouter: jest.Mocked<Router>;
  let mockActivatedRoute: any;
  
  // Variables pour tests Service Integration (services réels)
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let matSnackBar: MatSnackBar;
  let mockRouterForServiceTests: jest.Mocked<Router>;
  let httpTestingController: HttpTestingController;

  const mockSessionInformation: SessionInformation = {
    token: 'fake-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'A relaxing yoga session',
    date: new Date('2024-12-01'),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Smith',
    firstName: 'Jane',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  describe('Service Integration Tests (with real services)', () => {
    beforeEach(async () => {
      // Mock Router pour éviter erreurs de navigation
      mockRouterForServiceTests = {
        navigate: jest.fn().mockResolvedValue(true)
      } as any;

      // Mock ActivatedRoute pour fournir l'ID de session
      const mockActivatedRouteForService = {
        snapshot: {
          paramMap: {
            get: jest.fn().mockReturnValue('1')
          }
        }
      };

      // Mock SessionService pour fournir les informations utilisateur
      const mockSessionServiceForService = {
        sessionInformation: mockSessionInformation
      };

       // AJOUTEZ CECI : Mock MatSnackBar pour éviter les problèmes d'animation
    const mockMatSnackBarForService = {
      open: jest.fn()
    };

      await TestBed.configureTestingModule({
        declarations: [DetailComponent],
        imports: [
          RouterTestingModule.withRoutes([]),
          BrowserAnimationsModule,
          HttpClientTestingModule,
          MatSnackBarModule,
          ReactiveFormsModule
        ],
        providers: [
          SessionApiService,
          TeacherService,
          { provide: MatSnackBar, useValue: mockMatSnackBarForService }, 
          { provide: Router, useValue: mockRouterForServiceTests },
          { provide: ActivatedRoute, useValue: mockActivatedRouteForService },
          { provide: SessionService, useValue: mockSessionServiceForService }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(DetailComponent);
      component = fixture.componentInstance;
      sessionApiService = TestBed.inject(SessionApiService);
      teacherService = TestBed.inject(TeacherService);
      matSnackBar = TestBed.inject(MatSnackBar);
      httpTestingController = TestBed.inject(HttpTestingController);
      
      fixture.detectChanges();
    });

    afterEach(() => {
      // Vérifier qu'aucune requête HTTP n'est en attente
      httpTestingController.verify();
    });

    describe('Details Component Integration', () => {
      it('should call real SessionApiService.detail and load session data', () => {
        // Arrange - Préparer l'ID de session et espionner les mises à jour de propriétés
        const sessionId = '1';
        
        // Act - Déclencher fetchSession() via ngOnInit (déjà déclenché dans beforeEach via fixture.detectChanges())
        
        // Assert - Vérifier l'intégration complète :
        // 1. Requête HTTP GET envoyée vers /api/session/{id}
        const sessionReq = httpTestingController.expectOne('api/session/1');
        expect(sessionReq.request.method).toBe('GET');
        
        // 2. Réponse HTTP simulée avec mockSession
        sessionReq.flush(mockSession);
        
        // 3. Requête HTTP GET pour le teacher
        const teacherReq = httpTestingController.expectOne('api/teacher/1');
        expect(teacherReq.request.method).toBe('GET');
        teacherReq.flush(mockTeacher);
        
        // 4. component.session mis à jour avec les données reçues
        expect(component.session).toEqual(mockSession);
        
        // 5. component.isParticipate calculé correctement selon les users de la session
        // mockSessionInformation.id = 1 et mockSession.users = [1, 2]
        expect(component.isParticipate).toBe(true);
        
        // 6. component.teacher mis à jour
        expect(component.teacher).toEqual(mockTeacher);
      });

     
      it('should call real SessionApiService.delete and handle successful deletion', () => {
        // Arrange - Préparer une session existante et espionner router et snackBar
        const snackBarSpy = jest.spyOn(matSnackBar, 'open');
        const routerSpy = jest.spyOn(mockRouterForServiceTests, 'navigate');
        
        // Simuler les requêtes initiales de ngOnInit qui sont déjà en attente
        const initialSessionReq = httpTestingController.expectOne('api/session/1');
        initialSessionReq.flush(mockSession);
        const initialTeacherReq = httpTestingController.expectOne('api/teacher/1');
        initialTeacherReq.flush(mockTeacher);
        
        // S'assurer que la session est chargée
        component.session = mockSession;
        
        // Act - Appeler component.delete()
        component.delete();
        
        // Assert - Vérifier le flow de suppression :
        // 1. Requête HTTP DELETE envoyée vers /api/session/{id}
        const deleteReq = httpTestingController.expectOne('api/session/1');
        expect(deleteReq.request.method).toBe('DELETE');
        
        // 2. Réponse HTTP 200 simulée
        deleteReq.flush({});
        
        // 3. MatSnackBar.open appelé avec le message de succès
        expect(snackBarSpy).toHaveBeenCalledWith(
          'Session deleted !',
          'Close',
          { duration: 3000 }
        );
        
        // 4. Navigation vers 'sessions'
        expect(routerSpy).toHaveBeenCalledWith(['sessions']);
      });


      it('should call real SessionApiService.participate and refresh session data', () => {
        // Arrange - Préparer un utilisateur non-participant
        const sessionWithoutUser = {
          ...mockSession,
          users: [2] // L'utilisateur 1 n'est pas participant
        };
        
        // Simuler les requêtes initiales de ngOnInit
        const initialSessionReq = httpTestingController.expectOne('api/session/1');
        initialSessionReq.flush(sessionWithoutUser);
        const initialTeacherReq = httpTestingController.expectOne('api/teacher/1');
        initialTeacherReq.flush(mockTeacher);
        
        // S'assurer que isParticipate est false au départ
        expect(component.isParticipate).toBe(false);
        
        // Act - Appeler component.participate()
        component.participate();
        
        // Assert - Vérifier le flow de participation :
        // 1. Requête HTTP POST envoyée vers /api/session/{sessionId}/participate/{userId}
        const participateReq = httpTestingController.expectOne(
          'api/session/1/participate/1'
        );
        expect(participateReq.request.method).toBe('POST');
        
        // 2. Réponse HTTP 200 simulée
        participateReq.flush({});
        
        // 3. fetchSession() appelée pour rafraîchir les données
        // 4. Nouvelle requête GET pour récupérer la session mise à jour
        const refreshSessionReq = httpTestingController.expectOne('api/session/1');
        expect(refreshSessionReq.request.method).toBe('GET');
        
        // Session mise à jour avec l'utilisateur ajouté
        const updatedSession = {
          ...mockSession,
          users: [1, 2] // L'utilisateur 1 est maintenant participant
        };
        refreshSessionReq.flush(updatedSession);
        
        // Requête teacher suite au refresh
        const refreshTeacherReq = httpTestingController.expectOne('api/teacher/1');
        refreshTeacherReq.flush(mockTeacher);
        
        // Vérifier que la session a été mise à jour
        expect(component.session).toEqual(updatedSession);
        expect(component.isParticipate).toBe(true);
      });
    });

    describe('TeacherService Integration', () => {
  it('should call real TeacherService.detail after session is loaded', () => {
    // Arrange - Préparer le chargement initial de session
    // Les requêtes sont déjà déclenchées par ngOnInit dans beforeEach
    
    // Act - Déclencher le chargement complet (session puis teacher)
    // Gérer la première requête pour la session
    const sessionReq = httpTestingController.expectOne('api/session/1');
    expect(sessionReq.request.method).toBe('GET');
    
    // Assert - Vérifier l'intégration en cascade :
    // 1. Première requête HTTP GET vers /api/session/{id}
    expect(sessionReq.request.url).toBe('api/session/1');
    
    // 2. Session reçue et component.session mis à jour
    sessionReq.flush(mockSession);
    expect(component.session).toEqual(mockSession);
    
    // 3. Deuxième requête HTTP GET vers /api/teacher/{teacher_id}
    const teacherReq = httpTestingController.expectOne('api/teacher/1');
    expect(teacherReq.request.method).toBe('GET');
    expect(teacherReq.request.url).toBe('api/teacher/1');
    
    // 4. Teacher reçu et component.teacher mis à jour
    teacherReq.flush(mockTeacher);
    expect(component.teacher).toEqual(mockTeacher);
    
    // 5. L'ordre d'exécution est respecté (session avant teacher)
    // Ceci est implicitement vérifié par expectOne qui attend les requêtes dans l'ordre
  });

  it('should call TeacherService with correct teacher_id from session', () => {
    // Arrange - Préparer une session avec un teacher_id spécifique
    const customSession = {
      ...mockSession,
      teacher_id: 5 // ID différent pour tester
    };
    
    const customTeacher: Teacher = {
      id: 5,
      lastName: 'Johnson',
      firstName: 'Michael',
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    // Act - Déclencher le chargement
    // Gérer la requête session
    const sessionReq = httpTestingController.expectOne('api/session/1');
    sessionReq.flush(customSession);
    
    // Assert - Vérifier que :
    // 1. TeacherService.detail est appelé avec le bon teacher_id
    const teacherReq = httpTestingController.expectOne('api/teacher/5');
    expect(teacherReq.request.method).toBe('GET');
    expect(teacherReq.request.url).toBe('api/teacher/5');
    
    // 2. teacher_id provient bien de session.teacher_id
    teacherReq.flush(customTeacher);
    expect(component.teacher).toEqual(customTeacher);
    expect(component.teacher?.id).toBe(customSession.teacher_id);
  });
});
   
  });
});