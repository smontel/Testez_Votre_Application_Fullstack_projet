import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

import { FormComponent } from './form.component';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  
  // Mocks des services
  let mockSessionService: jest.Mocked<SessionService>;
  let mockTeacherService: jest.Mocked<TeacherService>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockMatSnackBar: jest.Mocked<MatSnackBar>;
  let mockRouter: jest.Mocked<Router>;
  let mockActivatedRoute: any;

  // Données de test
  const mockSession: Session = {
    id: 1,
    name: 'Yoga Test',
    description: 'Description test',
    date: new Date('2024-02-15'),
    teacher_id: 1,
    users: [10, 20],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  };

  const mockTeachers = [
    { id: 1, firstName: 'John', lastName: 'Doe' },
    { id: 2, firstName: 'Jane', lastName: 'Smith' }
  ];

  const mockSessionInformation = {
    token:"bob",
    type:"bob",
    admin: true,
    id: 1,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User'
  };

  beforeEach(async () => {
    // Création des mocks
    mockSessionService = {
      sessionInformation: mockSessionInformation
    } as any;

    mockTeacherService = {
      all: jest.fn().mockReturnValue(of(mockTeachers))
    } as any;

    mockSessionApiService = {
      detail: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    } as any;

    mockMatSnackBar = {
      open: jest.fn()
    } as any;

    mockRouter = {
      navigate: jest.fn()
    } as any;
    
    // Mock de la propriété url avec Object.defineProperty
    Object.defineProperty(mockRouter, 'url', {
      writable: true,
      value: '/sessions/create'
    });

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn()
        }
      }
    };

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule, MatIconModule, MatCardModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  describe('ngOnInit', () => {
    it('should redirect non-admin users', () => {
      // Arrange
      mockSessionService.sessionInformation = { ...mockSessionInformation, admin: false };
      
      // Act
      component.ngOnInit();
      
      // Assert
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
    });

    it('should initialize form for creation mode', () => {
      // Arrange
      Object.defineProperty(mockRouter, 'url', { value: '/sessions/create' });
      
      // Act
      component.ngOnInit();
      
      // Assert
      expect(component.onUpdate).toBe(false);
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.get('name')?.value).toBe('');
    });

    it('should initialize form for update mode', () => {
      // Arrange
      Object.defineProperty(mockRouter, 'url', { value: '/sessions/update/1' });
      mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      
      // Act
      component.ngOnInit();
      
      // Assert
      expect(component.onUpdate).toBe(true);
      expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    });

    it('should populate form with session data in update mode', () => {
      // Arrange
      Object.defineProperty(mockRouter, 'url', { value: '/sessions/update/1' });
      mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      
      // Act
      component.ngOnInit();
      
      // Assert
      expect(component.sessionForm?.get('name')?.value).toBe(mockSession.name);
      expect(component.sessionForm?.get('description')?.value).toBe(mockSession.description);
      expect(component.sessionForm?.get('teacher_id')?.value).toBe(mockSession.teacher_id);
    });
  });

  describe('Form validation', () => {
    beforeEach(() => {
      component.ngOnInit();
    });

    it('should be invalid when empty', () => {
      expect(component.sessionForm?.valid).toBe(false);
    });

    it('should be valid with correct data', () => {
      // Arrange & Act
      component.sessionForm?.patchValue({
        name: 'Test Session',
        date: '2024-02-15',
        teacher_id: 1,
        description: 'Valid description'
      });
      
      // Assert
      expect(component.sessionForm?.valid).toBe(true);
    });

    it('should be invalid without required fields', () => {
      const requiredFields = ['name', 'date', 'teacher_id', 'description'];
      
      requiredFields.forEach(field => {
        component.sessionForm?.get(field)?.setValue('');
        expect(component.sessionForm?.get(field)?.hasError('required')).toBe(true);
      });
    });

  describe('submit()', () => {
    beforeEach(() => {
      component.ngOnInit();
      // Formulaire valide
      component.sessionForm?.patchValue({
        name: 'Test Session',
        date: '2024-02-15',
        teacher_id: 1,
        description: 'Test description'
      });
    });

    it('should create session when not in update mode', () => {
      // Arrange
      component.onUpdate = false;
      mockSessionApiService.create.mockReturnValue(of(mockSession));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockSessionApiService.create).toHaveBeenCalledWith(component.sessionForm?.value);
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should update session when in update mode', () => {
      // Arrange
      component.onUpdate = true;
      component['id'] = '1';
      mockSessionApiService.update.mockReturnValue(of(mockSession));
      
      // Act
      component.submit();
      
      // Assert
      expect(mockSessionApiService.update).toHaveBeenCalledWith('1', component.sessionForm?.value);
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('teachers$ observable', () => {
    it('should load teachers on init', () => {
      expect(mockTeacherService.all).toHaveBeenCalled();
      
      component.teachers$.subscribe(teachers => {
        expect(teachers).toEqual(mockTeachers);
      });
    });
  });

  describe('Date handling', () => {
    it('should format date correctly for form input', () => {
      // Arrange
      Object.defineProperty(mockRouter, 'url', { value: '/sessions/update/1' });
      mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      
      // Act
      component.ngOnInit();
      
      // Assert
      const expectedDate = new Date(mockSession.date).toISOString().split('T')[0];
      expect(component.sessionForm?.get('date')?.value).toBe(expectedDate);
    });
  });
})
});