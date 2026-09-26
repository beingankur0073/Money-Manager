import { Component } from '@angular/core';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from './services/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterModule,
    MatSnackBarModule
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  title = 'frontend';

  constructor(
    public authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onLogout(): void {
    this.authService.logout();

    this.snackBar.open('Logged out successfully!', 'Close', {
      duration: 3000,
      panelClass: ['toast-logout'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });

    this.router.navigate(['/login']);
  }
}

export { App as AppComponent };
