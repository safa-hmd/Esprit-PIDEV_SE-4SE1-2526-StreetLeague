import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SponsorRoutingModule } from './sponsor-routing.module';
import { SponsorListComponent } from './sponsor-list.component';
import { SponsorFormComponent } from './sponsor-form.component';
import { SponsorDetailComponent } from './sponsor-detail.component';
import { SponsorEditComponent } from './sponsor-edit.component';
import { SponsorFOSharedModule } from '../shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    SponsorListComponent,
    SponsorFormComponent,
    SponsorDetailComponent,
    SponsorEditComponent,
  ],
  imports: [CommonModule, FormsModule, SponsorRoutingModule, SponsorFOSharedModule]
})
export class SponsorModule { }
