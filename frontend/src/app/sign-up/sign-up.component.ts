import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../services/user.service';
import { users } from '../shared/enum';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [RouterLink,ReactiveFormsModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {
signupForm = new FormGroup({
  name: new FormControl('', Validators.required),
  email: new FormControl('', Validators.required),
  password: new FormControl('', Validators.required),
  role: new FormControl('admin'),
  contactNumber: new FormControl(''),
})
constructor(private userService : UserService, private router : Router){}

signUpUser(){
  if(this.signupForm.invalid) return;
  const form: users = {
    name: this.signupForm.value.name ?? '',
    email: this.signupForm.value.email ?? '',
    password: this.signupForm.value.password ?? '',
    role: this.signupForm.value.role ?? 'admin',
    contactNumber: this.signupForm.value.contactNumber ?? '',
  };
  form.isActive = true;
  this.userService.registerUser(form).subscribe({
    next: res => {
      if(res) this.router.navigate(['../signin']);
    }
  })

}
}
