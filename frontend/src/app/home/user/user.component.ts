import { Component } from '@angular/core';
import { users } from '../../shared/enum';
import { NgbModal, NgbModalConfig ,NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [NgbDropdownModule,ReactiveFormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent {
users : users[] = [];
userForm = new FormGroup({
  name: new FormControl('', Validators.required),
  email: new FormControl('', Validators.required),
  password: new FormControl('', Validators.required),
  role: new FormControl(''),
  contactNumber: new FormControl(''),
})
constructor(
  config: NgbModalConfig,
  private modalService: NgbModal,
  private userService : UserService
) {
  config.backdrop = 'static';
  config.keyboard = false;
}

ngOnInit(){
  this.getAllUsers();
}

open(content: any,user : users) {
  this.userForm.reset();
  this.userForm.get('name')?.setValue(user.name!);
  this.userForm.get('email')?.setValue(user.email!);
  this.userForm.get('password')?.setValue('');
  this.userForm.get('role')?.setValue(user.role!);
  this.userForm.get('contactNumber')?.setValue(user.contactNumber!);
  this.modalService.open(content);
}
saveUser(){
  if(this.userForm.invalid) return;
  const form: users = {
    name: this.userForm.value.name ?? '',
    email: this.userForm.value.email ?? '',
    password: this.userForm.value.password ?? '',
    role: this.userForm.value.role ?? 'admin',
    contactNumber: this.userForm.value.contactNumber ?? '',
  };
  this.userService.updateUser(form.email!,form).subscribe({
    next : res => {
      this.getAllUsers();
      this.modalService.dismissAll('');
    }
  })
}

getAllUsers(){
  this.userService.getAllUsers().subscribe({
    next: res => {
      this.users = res.data;
    }
  })
}

deleteUserById(id : string){
  this.userService.deleteUser(id).subscribe({
    next: res => {
      this.getAllUsers;
    }
  })
}
}
