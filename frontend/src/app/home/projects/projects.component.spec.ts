import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectsComponent } from './projects.component';
import { ProjectService } from '../../services/project.service';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('ProjectsComponent', () => {
  let component: ProjectsComponent;
  let fixture: ComponentFixture<ProjectsComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let modalServiceSpy: jasmine.SpyObj<NgbModal>;

  const mockProjects = [
    { projectId: 1, projectName: 'Project 1', projectDescription: 'Desc 1', creatorEmail: 'test@test.com', projectStatus: 'Started' },
    { projectId: 2, projectName: 'Project 2', projectDescription: 'Desc 2', creatorEmail: 'test@test.com', projectStatus: 'Started' }
  ];

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProject', 'createProject', 'updateProject', 'deleteProject']);
    modalServiceSpy = jasmine.createSpyObj('NgbModal', ['open', 'dismissAll']);

    projectServiceSpy.getAllProject.and.returnValue(of({ data: mockProjects }));

    await TestBed.configureTestingModule({
      imports: [ProjectsComponent, ReactiveFormsModule],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: NgbModal, useValue: modalServiceSpy },
        NgbModalConfig
      ]
    }).compileComponents();

    spyOn(localStorage, 'getItem').and.returnValue('test@test.com');

    fixture = TestBed.createComponent(ProjectsComponent);
    component = fixture.componentInstance;
  });

  // Test 1: Création
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test 2: Initialisation des projets
  it('should initialize projects array as empty', () => {
    expect(component.projects).toEqual([]);
  });

  // Test 3: ngOnInit appelle getAllProjects
  it('should call getAllProjects on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    
    expect(projectServiceSpy.getAllProject).toHaveBeenCalled();
    expect(component.projects).toEqual(mockProjects);
  }));

  // Test 4: Formulaire invalide si vide
  it('should have invalid form when empty', () => {
    expect(component.projectForm.valid).toBeFalse();
  });

  // Test 5: Formulaire valide avec données
  it('should have valid form when filled', () => {
    component.projectForm.get('projectName')?.setValue('Test Project');
    component.projectForm.get('projectDescription')?.setValue('Description');
    component.projectForm.get('creatorEmail')?.setValue('test@test.com');
    
    expect(component.projectForm.valid).toBeTrue();
  });

  // Test 6: open() réinitialise le formulaire
  it('should reset form when opening modal for new project', () => {
    component.projectForm.get('projectName')?.setValue('Old Value');
    
    component.open({}, false);
    
    expect(component.isEdit).toBeFalse();
    expect(modalServiceSpy.open).toHaveBeenCalled();
  });

  // Test 7: open() en mode édition remplit le formulaire
  it('should fill form when opening modal for edit', () => {
    const project = { projectName: 'Test', projectDescription: 'Desc', creatorEmail: 'test@test.com', projectStatus: 'Started' };
    
    component.open({}, true, project);
    
    expect(component.isEdit).toBeTrue();
    expect(component.projectForm.get('projectName')?.value).toBe('Test');
    expect(component.projectForm.get('projectDescription')?.value).toBe('Desc');
  });

  // Test 8: saveForm() appelle createProject si pas en mode édition
  it('should call createProject when saveForm and not editing', fakeAsync(() => {
    projectServiceSpy.createProject.and.returnValue(of({ data: {} }));
    component.isEdit = false;
    
    component.projectForm.get('projectName')?.setValue('New Project');
    component.projectForm.get('projectDescription')?.setValue('Description');
    component.projectForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveForm();
    tick();
    
    expect(projectServiceSpy.createProject).toHaveBeenCalled();
  }));

  // Test 9: saveForm() appelle updateProject si en mode édition
  it('should call updateProject when saveForm and editing', fakeAsync(() => {
    projectServiceSpy.updateProject.and.returnValue(of({ data: {} }));
    component.isEdit = true;
    
    component.projectForm.get('projectName')?.setValue('Updated Project');
    component.projectForm.get('projectDescription')?.setValue('Updated Desc');
    component.projectForm.get('creatorEmail')?.setValue('test@test.com');
    
    component.saveForm();
    tick();
    
    expect(projectServiceSpy.updateProject).toHaveBeenCalled();
  }));

  // Test 10: createProject() ne fait rien si formulaire invalide
  it('should not create project if form is invalid', () => {
    component.createProject();
    expect(projectServiceSpy.createProject).not.toHaveBeenCalled();
  });

  // Test 11: deleteProject() appelle le service
  it('should call deleteProject service', fakeAsync(() => {
    projectServiceSpy.deleteProject.and.returnValue(of({ data: {} }));
    
    component.deleteProject('Project1');
    tick();
    
    expect(projectServiceSpy.deleteProject).toHaveBeenCalledWith('Project1');
  }));

  // Test 12: getAllProjects() met à jour la liste
  it('should update projects list on getAllProjects', fakeAsync(() => {
    component.getAllProjects();
    tick();
    
    expect(component.projects).toEqual(mockProjects);
  }));
});