import { Routes } from '@angular/router';

import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { DashboardComponent } from './components/dashboard/dashboard';

import { AccountsComponent } from './components/accounts/accounts';
import { TransactionsComponent } from './components/transactions/transactions';
import { CategoriesComponent } from './components/categories/categories';

import { authGuard } from './guards/auth-guard';

export const routes: Routes = [

  // Authentication
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'register',
    component: RegisterComponent
  },


  // Dashboard
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },


  // Accounts
  {
    path: 'accounts',
    component: AccountsComponent,
    canActivate: [authGuard]
  },


  // Transactions
  {
    path: 'transactions',
    component: TransactionsComponent,
    canActivate: [authGuard]
  },


  // Categories
  {
    path: 'categories',
    component: CategoriesComponent,
    canActivate: [authGuard]
  },


  // Default
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },


  // Unknown route
  {
    path: '**',
    redirectTo: '/login'
  }

];