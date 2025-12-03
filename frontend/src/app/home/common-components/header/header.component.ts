import { Component } from '@angular/core';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
userName: string = '';
constructor(private user : UserService){}
ngOnInit(){
  this.getUser()
}
getUser(){
  this.user.getUserById(localStorage.getItem('user')!).subscribe({
    next: res => {
      this.userName = res?.data?.name;
    }
  })
}
}
