import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router'; // <-- Add this import

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router // <-- Make sure Router is injected here
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      // Call login service and subscribe to the Observable
      this.authService.login(email, password).subscribe(
        (response) => {
          console.log('Login successful', response);
          const token = response.token; // Assuming response contains token
          localStorage.setItem('token', token); // Store the token in localStorage
          this.router.navigate(['/home']); // Redirect to home page
        },
        (error) => {
          console.error('Login failed', error);
        }
      );
    }
  }
}
