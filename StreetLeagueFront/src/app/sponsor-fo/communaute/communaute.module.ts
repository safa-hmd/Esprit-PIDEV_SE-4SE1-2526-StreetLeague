import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { SponsorCommunauteListComponent } from './communaute-list.component';
import { SponsorCommunauteDetailComponent } from './communaute-detail.component';
import { CommunauteRoutingModule } from './communaute-routing.module';
import { SponsorFOSharedModule } from '../shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    SponsorCommunauteListComponent,
    SponsorCommunauteDetailComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    CommunauteRoutingModule,
    SponsorFOSharedModule
  ]
})
export class CommunityModule { }
