import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  message: string;
  type: ToastType;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private toastSubject =
    new BehaviorSubject<Toast | null>(null);

  toast$ = this.toastSubject.asObservable();

  private timeoutId: any;

  show(
    message: string,
    type: ToastType = 'info'
  ): void {

    this.toastSubject.next({
      message,
      type
    });

    clearTimeout(this.timeoutId);

    this.timeoutId = setTimeout(() => {
      this.clear();
    }, 3000);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  warning(message: string): void {
    this.show(message, 'warning');
  }

  info(message: string): void {
    this.show(message, 'info');
  }

  clear(): void {
    this.toastSubject.next(null);
  }
}