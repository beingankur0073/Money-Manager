import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading: boolean = false;
  errorMessage: string = '';
  backendErrors: { [key: string]: string } = {};

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', Validators.required],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.backendErrors = {};

      this.authService.register(this.registerForm.value).subscribe({
        next: (res) => {
          this.isLoading = false;
          this.snackBar.open(`Account created successfully! Welcome, ${res.username}.`, 'Close', {
            duration: 3000,
            panelClass: ['toast-success'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
          this.router.navigate(['/dashboard']);
        },
        error: (err: any) => {
          this.isLoading = false;

          if (err.error && err.error.validationErrors) {
            this.backendErrors = err.error.validationErrors;
          } else if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
            this.snackBar.open(this.errorMessage, 'Close', {
              duration: 4000,
              panelClass: ['toast-error'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
            this.snackBar.open(this.errorMessage, 'Close', {
              duration: 4000,
              panelClass: ['toast-error'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
          this.cdr.markForCheck();
        }
      });
    }
  }
}
