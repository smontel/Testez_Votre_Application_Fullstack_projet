/// <reference types="jest" />
import 'jest-preset-angular/setup-jest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;
  
  // Données de test réutilisables
  const mockSession: Session = {
    id: 1,
    name: 'Yoga débutant',
    description: 'Session pour débutants',
    date: new Date('2024-01-15'),
    teacher_id: 1,
    users: [10, 20],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  };

  const mockSessions: Session[] = [
    mockSession,
    {
      id: 2,
      name: 'Yoga avancé',
      description: 'Session pour experts',
      date: new Date('2024-01-20'),
      teacher_id: 2,
      users: [30],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Vérifie qu'aucune requête HTTP n'est en attente
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeDefined();
  });

  describe('all()', () => {
    it('should return all sessions', () => {
      // Act - Appel de la méthode
      service.all().subscribe(sessions => {
        // Assert - Vérification du résultat
        expect(sessions).toEqual(mockSessions);
        expect(sessions.length).toBe(2);
      });

      // Arrange - Configuration de la requête HTTP mockée
      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      
      // Simulation de la réponse
      req.flush(mockSessions);
    });
  });

  describe('detail()', () => {
    it('should return a specific session', () => {
      const sessionId = '1';

      service.detail(sessionId).subscribe(session => {
        expect(session).toEqual(mockSession);
        expect(session.id).toBe(1);
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);
    });
  });

  describe('delete()', () => {
    it('should delete a session', () => {
      const sessionId = '1';

      service.delete(sessionId).subscribe(response => {
        expect(response).toBeDefined();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });

  describe('create()', () => {
    it('should create a new session', () => {
      service.create(mockSession).subscribe(session => {
        expect(session).toEqual(mockSession);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockSession);
      req.flush(mockSession);
    });
  });

  describe('update()', () => {
    it('should update an existing session', () => {
      const sessionId = '1';
      const updatedSession = { ...mockSession, name: 'Yoga modifié' };

      service.update(sessionId, updatedSession).subscribe(session => {
        expect(session).toEqual(updatedSession);
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedSession);
      req.flush(updatedSession);
    });
  });

  describe('participate()', () => {
    it('should add user participation to a session', () => {
      const sessionId = '1';
      const userId = '123';

      service.participate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined(); // void return type
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeNull();
      req.flush(null);
    });
  });

  describe('unParticipate()', () => {
    it('should remove user participation from a session', () => {
      const sessionId = '1';
      const userId = '123';

      service.unParticipate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined(); // void return type
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('DELETE')
      req.flush(null);
    });
  });

  describe('Error handling', () => {
    it('should handle HTTP errors gracefully', () => {
      const errorMessage = 'Session not found';
      
      service.detail('999').subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne('api/session/999');
      req.flush(errorMessage, { status: 404, statusText: 'Not Found' });
    });
  });
});