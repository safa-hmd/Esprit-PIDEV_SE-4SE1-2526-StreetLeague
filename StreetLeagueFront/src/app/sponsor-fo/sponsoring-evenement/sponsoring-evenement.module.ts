import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SponsoringEvenementRoutingModule } from './sponsoring-evenement-routing.module';
import { SponsoringListComponent } from './sponsoring-list.component';
import { SponsoringFormComponent } from './sponsoring-form.component';
import { SponsoringDetailComponent } from './sponsoring-detail.component';
import { SponsoringEditComponent } from './sponsoring-edit.component';
import { SponsorFOSharedModule } from '../shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    SponsoringListComponent,
    SponsoringFormComponent,
    SponsoringDetailComponent,
    SponsoringEditComponent,
  ],
  imports: [CommonModule, FormsModule, SponsoringEvenementRoutingModule, SponsorFOSharedModule]
})
export class SponsoringEvenementModule { }
