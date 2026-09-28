import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../services/auth';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {

  loginForm: FormGroup;

  isLoading = false;

  errorMessage = '';


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {

    this.loginForm = this.fb.group({

      usernameOrEmail: [
        '',
        Validators.required
      ],

      password: [
        '',
        Validators.required
      ]

    });

  }


  onSubmit(): void {

    if (this.loginForm.invalid) {

      this.loginForm.markAllAsTouched();

      return;
    }


    this.isLoading = true;
    this.errorMessage = '';


    this.authService
      .login(this.loginForm.value)
      .subscribe({

        next: (res) => {

          this.isLoading = false;


          // Show success toast
          this.notificationService.success(
            'Logged in successfully!'
          );


          // Go to accounts page
          this.router.navigate(['/accounts']);

        },


        error: (err: any) => {

          this.isLoading = false;


          if (err.error?.message) {

            this.errorMessage =
              err.error.message;

          } else {

            this.errorMessage =
              'Invalid username/email or password.';

          }

        }

      });

  }

}

export { LoginComponent as Login };