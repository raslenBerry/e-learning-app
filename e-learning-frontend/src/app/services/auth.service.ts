import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080'; // Update with your backend API URL
  private token: string | null = null;
  private roleSubject = new BehaviorSubject<string | null>(null);
  public role$ = this.roleSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  // Modify login to return Observable instead of subscribing
  login(email: string, password: string): Observable<{ token: string; role: string }> {
    return this.http.post<{ token: string; role: string }>(
      `${this.apiUrl}/api/auth/login`,
      { email, password }
    );
  }

  register(email: string, password: string): void {
    this.http
      .post(`${this.apiUrl}/api/auth/register`, { email, password })
      .subscribe(
        (response) => {
          console.log('Registration successful', response);
          this.router.navigate(['/login']); // Redirect to login on success
        },
        (error) => {
          console.error('Registration failed', error);
        }
      );
  }

  logout(): void {
    this.token = null;
    localStorage.removeItem('token'); // Remove token from storage
    this.roleSubject.next(null);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('token') !== null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Method to navigate the user based on their role
  navigateByRole(role: string): void {
    if (role === 'ROLE_ADMIN') {
      this.router.navigate(['/dashboard']);
    } else if (role === 'ROLE_USER') {
      this.router.navigate(['/home']);
    } else {
      this.router.navigate(['/profile']);
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
    });
  }
}
