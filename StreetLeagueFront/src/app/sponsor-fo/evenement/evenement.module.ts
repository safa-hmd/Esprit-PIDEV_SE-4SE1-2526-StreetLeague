import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { SponsorEvenementListComponent } from './evenement-list.component';
import { SponsorEvenementDetailComponent } from './evenement-detail.component';
import { EvenementRoutingModule } from './evenement-routing.module';
import { SponsorFOSharedModule } from '../shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    SponsorEvenementListComponent,
    SponsorEvenementDetailComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    EvenementRoutingModule,
    SponsorFOSharedModule
  ]
})
export class EvenementModule { }
