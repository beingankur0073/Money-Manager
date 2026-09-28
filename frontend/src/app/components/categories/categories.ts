import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  TransactionService,
  Category
} from '../../services/transaction';

import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './categories.html',
  styleUrl: './categories.scss'
})
export class CategoriesComponent implements OnInit {

  categories: Category[] = [];

  categoryForm: FormGroup;


  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {

    // Category form
    this.categoryForm = this.fb.group({

      name: [
        '',
        [
          Validators.required,
          Validators.maxLength(50)
        ]
      ],

      type: [
        'EXPENSE',
        Validators.required
      ]

    });

  }


  ngOnInit(): void {

    this.loadCategories();

  }


  // ================= LOAD CATEGORIES =================

  loadCategories(): void {

    this.transactionService
      .getCategories()
      .subscribe({

        next: (data) => {

          this.categories = data;

          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });

  }


  // ================= ADD CATEGORY =================

  onAddCategory(): void {

    if (this.categoryForm.invalid) {

      this.categoryForm.markAllAsTouched();

      return;
    }


    this.transactionService
      .createCategory(
        this.categoryForm.value
      )
      .subscribe({

        next: (newCategory) => {

          this.categories.push(newCategory);


          this.categoryForm.reset({

            type: 'EXPENSE'

          });


          this.notificationService.success(
            'Custom category created successfully!'
          );


          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });

  }


  // ================= DELETE CATEGORY =================

  onDeleteCategory(categoryId: number): void {

    if (
      !confirm(
        'Are you sure you want to delete this category?'
      )
    ) {

      return;
    }


    this.transactionService
      .deleteCategory(categoryId)
      .subscribe({

        next: () => {

          this.categories =
            this.categories.filter(
              category => category.id !== categoryId
            );


          this.notificationService.success(
            'Category deleted successfully!'
          );


          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });

  }


  // ================= ERROR HANDLING =================

  private handleError(err: any): void {

    const message =
      err?.error?.message ||
      'An unexpected error occurred.';

    this.notificationService.error(message);

  }

}