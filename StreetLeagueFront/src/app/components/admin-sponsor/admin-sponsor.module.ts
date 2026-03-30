import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AdminSponsorListComponent } from './admin-sponsor-list.component';
import { AdminSponsorDetailComponent } from './admin-sponsor-detail.component';
import { AdminSponsorFormComponent } from './admin-sponsor-form.component';
import { AdminSponsorEditComponent } from './admin-sponsor-edit.component';

@NgModule({
  declarations: [
    AdminSponsorListComponent,
    AdminSponsorDetailComponent,
    AdminSponsorFormComponent,
    AdminSponsorEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class AdminSponsorModule { }

