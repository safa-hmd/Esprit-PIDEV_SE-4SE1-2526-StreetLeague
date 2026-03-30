import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { SponsoringListComponent } from './sponsoring-list.component';
import { SponsoringFormComponent } from './sponsoring-form.component';
import { SponsoringDetailComponent } from './sponsoring-detail.component';
import { SponsoringEditComponent } from './sponsoring-edit.component';

@NgModule({
  declarations: [
    SponsoringListComponent,
    SponsoringFormComponent,
    SponsoringDetailComponent,
    SponsoringEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class SponsoringEvenementModule { }
