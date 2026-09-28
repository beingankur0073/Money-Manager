import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, Toast } from '../../services/notification';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.scss'
})
export class ToastComponent {

  toast$: Observable<Toast | null>;

  constructor(
    private notificationService: NotificationService
  ) {
    this.toast$ = this.notificationService.toast$;
  }

  close(): void {
    this.notificationService.clear();
  }
}