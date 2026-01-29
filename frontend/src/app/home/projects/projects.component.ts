import { Component } from '@angular/core';
import { Project } from '../../shared/enum';
import { NgbDropdownModule, NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from '../../services/project.service';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [NgbDropdownModule, ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.css'
})
export class ProjectsComponent {
  projects: Project[] = [];
  projectForm = new FormGroup({
    projectName: new FormControl('', Validators.required),
    projectDescription: new FormControl('', Validators.required),
    projectStartDate: new FormControl('', Validators.required),
    creatorEmail: new FormControl('', Validators.required),
  });
  isEdit: boolean = false;

  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private projectService: ProjectService
  ) {
    config.backdrop = 'static';
    config.keyboard = false;
  }

  ngOnInit() {
    this.getAllProjects();
  }

  open(content: any, isEdit: boolean, project?: Project) {
    this.projectForm.reset();
    this.projectForm.get('creatorEmail')?.setValue(localStorage.getItem('user')!);
    this.isEdit = false;
    if (isEdit && project) {
      this.projectForm.get('projectName')?.setValue(project.projectName!);
      this.projectForm.get('projectDescription')?.setValue(project.projectDescription!);
      this.projectForm.get('projectStartDate')?.setValue(project.projectStartDate!);
      this.projectForm.get('creatorEmail')?.setValue(localStorage.getItem('user')!);
      this.isEdit = true;
    }
    this.modalService.open(content);
  }

  saveForm() {
    if (this.isEdit) this.updateProject();
    else this.createProject();
  }

  createProject() {
    if (this.projectForm.invalid) return;
    const form: Project = {
      projectName: this.projectForm.value.projectName ?? '',
      projectDescription: this.projectForm.value.projectDescription ?? '',
      projectStartDate: this.projectForm.value.projectStartDate ?? '',
      creatorEmail: this.projectForm.value.creatorEmail ?? '',
      projectStatus: 'ACTIVE'
    };
    this.projectService.createProject(form).subscribe({
      next: res => {
        this.getAllProjects();
        this.modalService.dismissAll('');
      }
    });
  }

  updateProject() {
    if (this.projectForm.invalid) return;
    const form: Project = {
      projectName: this.projectForm.value.projectName ?? '',
      projectDescription: this.projectForm.value.projectDescription ?? '',
      projectStartDate: this.projectForm.value.projectStartDate ?? '',
      creatorEmail: this.projectForm.value.creatorEmail ?? '',
      projectStatus: 'ACTIVE'
    };
    this.projectService.updateProject(form.projectName!, form).subscribe({
      next: res => {
        this.isEdit = false;
        this.getAllProjects();
        this.modalService.dismissAll('');
      }
    });
  }

  deleteProject(projectName: string) {
    this.projectService.deleteProject(projectName).subscribe({
      next: res => {
        this.getAllProjects();
      }
    });
  }

  getAllProjects() {
    this.projectService.getAllProject().subscribe({
      next: res => {
        this.projects = res.data;
      }
    });
  }
}