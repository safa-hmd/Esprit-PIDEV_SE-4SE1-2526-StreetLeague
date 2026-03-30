import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { SponsorListComponent } from './sponsor-list.component';
import { SponsorFormComponent } from './sponsor-form.component';
import { SponsorDetailComponent } from './sponsor-detail.component';
import { SponsorEditComponent } from './sponsor-edit.component';

@NgModule({
  declarations: [
    SponsorListComponent,
    SponsorFormComponent,
    SponsorDetailComponent,
    SponsorEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class SponsorModule { }
