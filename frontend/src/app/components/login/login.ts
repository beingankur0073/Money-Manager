import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      usernameOrEmail: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          this.isLoading = false;

          this.snackBar.open(
            `Welcome back, ${res?.username || 'User'}! Logged in successfully.`,
            'Close',
            {
              duration: 3000,
              panelClass: ['toast-success'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );

          this.router.navigate(['/dashboard']);
        },
        error: (err: any) => {
          this.isLoading = false;

          if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
          } else {
            this.errorMessage = 'Invalid username/email or password.';
          }

          this.snackBar.open(this.errorMessage, 'Close', {
            duration: 4000,
            panelClass: ['toast-error'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });

          this.cdr.markForCheck();
        }
      });
    }
  }
}

export { LoginComponent as Login };
