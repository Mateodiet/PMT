import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../services/user.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { users } from '../shared/enum';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [RouterLink,ReactiveFormsModule],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.css'
})
export class SignInComponent {
  loginForm = new FormGroup({
    email : new FormControl('', Validators.required),
    password : new FormControl('', Validators.required),
  })
  constructor(private userService : UserService,private router : Router){}
  login(){
    if(this.loginForm.invalid) return;
      const form: users = {
        email: this.loginForm.value.email ?? '',
        password: this.loginForm.value.password ?? ''
      };
      this.userService.loginUser(form).subscribe({
        next : res => {
          if(res) {
          localStorage.setItem('loggedIn', 'true')
          localStorage.setItem('user', this.loginForm.value.email!)
          this.router.navigate(['../home']);
        }
      }
    })
  }
}
