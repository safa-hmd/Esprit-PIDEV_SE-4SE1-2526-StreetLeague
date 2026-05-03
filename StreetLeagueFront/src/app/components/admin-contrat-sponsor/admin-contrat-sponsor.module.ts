import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminContratSponsorListComponent } from './admin-contrat-sponsor-list.component';
import { AdminContratSponsorFormComponent } from './admin-contrat-sponsor-form.component';
import { AdminContratSponsorEditComponent } from './admin-contrat-sponsor-edit.component';

@NgModule({
  declarations: [
    AdminContratSponsorListComponent,
    AdminContratSponsorFormComponent,
    AdminContratSponsorEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class AdminContratSponsorModule { }
