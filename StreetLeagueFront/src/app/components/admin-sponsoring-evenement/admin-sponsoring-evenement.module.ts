import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AdminSponsoringEvenementListComponent } from './admin-sponsoring-evenement-list.component';
import { AdminSponsoringEvenementDetailComponent } from './admin-sponsoring-evenement-detail.component';
import { AdminSponsoringEvenementFormComponent } from './admin-sponsoring-evenement-form.component';
import { AdminSponsoringEvenementEditComponent } from './admin-sponsoring-evenement-edit.component';

@NgModule({
  declarations: [
    AdminSponsoringEvenementListComponent,
    AdminSponsoringEvenementDetailComponent,
    AdminSponsoringEvenementFormComponent,
    AdminSponsoringEvenementEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class AdminSponsoringEvenementModule { }
