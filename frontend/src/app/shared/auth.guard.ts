import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isAuthenticated = !!localStorage.getItem('loggedIn');
  if (!isAuthenticated) {
    router.navigate(['/signin']);
    return false;
  }
  return true;
};