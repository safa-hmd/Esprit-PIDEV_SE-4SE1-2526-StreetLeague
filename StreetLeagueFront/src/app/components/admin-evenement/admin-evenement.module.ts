import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminEvenementListComponent } from './admin-evenement-list.component';
import { AdminEvenementFormComponent } from './admin-evenement-form.component';
import { AdminEvenementEditComponent } from './admin-evenement-edit.component';

@NgModule({
  declarations: [
    AdminEvenementListComponent,
    AdminEvenementFormComponent,
    AdminEvenementEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class AdminEvenementModule { }
