// Imports (vous avez dit que vous les géreriez de votre côté, mais ReactiveFormsModule est crucial ici)
import { ComponentFixture, TestBed, fakeAsync, flush } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms'; // ReactiveFormsModule est nécessaire pour FormBuilder
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';




describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  // Mocks des services
  let mockActivatedRoute: any;
  let mockSessionService: jest.Mocked<SessionService>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockTeacherService: jest.Mocked<TeacherService>;
  let mockMatSnackBar: jest.Mocked<MatSnackBar>;
  let mockRouter: jest.Mocked<Router>;

  // Données de test
  const mockSessionId = '1';
  const mockUserId = 1; // Correspond à sessionInformation.id
  const mockAdminInfo = { // Utilisez la structure attendue par SessionService.sessionInformation
    token: 'admin_token',
    type: 'Bearer',
    id: mockUserId,
    admin: true,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User'
  };
  const mockNonAdminInfo = { // Utilisez la structure attendue par SessionService.sessionInformation
    token: 'user_token',
    type: 'Bearer',
    id: mockUserId,
    admin: false,
    username: 'user',
    firstName: 'Normal',
    lastName: 'User'
  };

  const mockSession = { // Utilisez la structure attendue par Session
    id: parseInt(mockSessionId, 10), // Convertir en nombre
    name: 'Test Session',
    description: 'Description of test session',
    date: new Date('2025-01-01'),
    teacher_id: 10,
    users: [], // Initialement sans utilisateurs participants
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockSessionWithUser = { // Utilisez la structure attendue par Session
    ...mockSession,
    users: [mockUserId], // Utilisateur mocké participe
  };

  // Nouveau mock Teacher pour correspondre à l'interface fournie
  const mockTeacher = { // Utilisez la structure attendue par Teacher
    id: 10,
    firstName: 'Yoga',
    lastName: 'Teacher',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01'),
  };

  // Espionner window.history.back
  let historyBackSpy: jest.SpyInstance;

  beforeEach(async () => {
    // Configuration des mocks
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue(mockSessionId) // Retourne toujours mockSessionId pour 'id'
        }
      }
    };

    mockSessionService = {
      sessionInformation: mockAdminInfo // Par défaut, l'utilisateur est admin pour la plupart des tests
    } as any;

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(mockSession)), // Mock par défaut pour detail
      delete: jest.fn().mockReturnValue(of({})), // Retourne un observable vide pour les actions de suppression
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({})),
    } as any;

    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    } as any;

    mockMatSnackBar = {
      open: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    // Espionner la méthode window.history.back pour ne pas interférer avec l'environnement de test
    historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [
        ReactiveFormsModule, MatIconModule, MatCardModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter },
        // FormBuilder n'a pas besoin d'être explicitement fourni s'il est couvert par ReactiveFormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    // Réinitialise les mocks après chaque test pour éviter les interférences
    jest.clearAllMocks();
    // Restaurer window.history.back
    historyBackSpy.mockRestore();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Constructor initialization', () => {
    it('should initialize sessionId from route parameters', () => {
      expect(component.sessionId).toBe(mockSessionId);
      expect(mockActivatedRoute.snapshot.paramMap.get).toHaveBeenCalledWith('id');
    });

    it('should initialize isAdmin based on sessionService information', () => {
      // Cas admin (par défaut dans mockSessionService)
      expect(component.isAdmin).toBe(true);

      // Cas non-admin : on doit recréer le composant avec un mock différent
      const originalSessionInfo = mockSessionService.sessionInformation;
      mockSessionService.sessionInformation = mockNonAdminInfo;

      // On recrée le composant pour que le constructeur s'exécute avec le nouveau mock
      const nonAdminFixture = TestBed.createComponent(DetailComponent);
      const nonAdminComponent = nonAdminFixture.componentInstance;

      expect(nonAdminComponent.isAdmin).toBe(false);

      mockSessionService.sessionInformation = originalSessionInfo; // Restaure l'original
    });

    it('should initialize userId based on sessionService information', () => {
      expect(component.userId).toBe(mockUserId.toString());
    });
  });

  describe('ngOnInit', () => {
    it('should call fetchSession on ngOnInit', fakeAsync(() => {
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
      component.ngOnInit();
      flush();
      expect(fetchSessionSpy).toHaveBeenCalled();
    }));
  });

  describe('back()', () => {
    it('should call window.history.back()', () => {
      component.back();
      expect(historyBackSpy).toHaveBeenCalled();
    });
  });

  describe('delete()', () => {
    it('should call sessionApiService.delete, show snackbar, and navigate to sessions', fakeAsync(() => {
      component.delete();
      flush();
      expect(mockSessionApiService.delete).toHaveBeenCalledWith(mockSessionId);
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    }));
  });

  describe('participate()', () => {
    it('should call sessionApiService.participate and then fetchSession', fakeAsync(() => {
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
      component.participate();
      flush();
      expect(mockSessionApiService.participate).toHaveBeenCalledWith(mockSessionId, mockUserId.toString());
      expect(fetchSessionSpy).toHaveBeenCalled();
    }));
  });

  describe('unParticipate()', () => {
    it('should call sessionApiService.unParticipate and then fetchSession', fakeAsync(() => {
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
      component.unParticipate();
      flush();
      expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith(mockSessionId, mockUserId.toString());
      expect(fetchSessionSpy).toHaveBeenCalled();
    }));
  });

  describe('fetchSession (private method, tested via ngOnInit)', () => {
    it('should fetch session details and assign to component.session', fakeAsync(() => {
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      mockTeacherService.detail.mockReturnValue(of(mockTeacher));

      fixture.detectChanges();
      flush();

      expect(mockSessionApiService.detail).toHaveBeenCalledWith(mockSessionId);
      expect(component.session).toEqual(mockSession);
      expect(mockTeacherService.detail).toHaveBeenCalledWith(mockSession.teacher_id.toString());
      expect(component.teacher).toEqual(mockTeacher);
    }));

    it('should set isParticipate to false if user is not in session.users', fakeAsync(() => {
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      fixture.detectChanges();
      flush();
      expect(component.isParticipate).toBe(false);
    }));

    it('should set isParticipate to true if user is in session.users', fakeAsync(() => {
      mockSessionApiService.detail.mockReturnValue(of(mockSessionWithUser));
      fixture.detectChanges();
      flush();
      expect(component.isParticipate).toBe(true);
    }));

    it('should fetch teacher details and assign to component.teacher', fakeAsync(() => {
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      mockTeacherService.detail.mockReturnValue(of(mockTeacher));

      fixture.detectChanges();
      flush();
      expect(mockTeacherService.detail).toHaveBeenCalledWith(mockSession.teacher_id.toString());
      expect(component.teacher).toEqual(mockTeacher);
    }));
  });
});