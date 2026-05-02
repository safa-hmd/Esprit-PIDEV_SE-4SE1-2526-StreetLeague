import { NgModule } from '@angular/core';

import { SponsorFORoutingModule } from './sponsor-fo-routing.module';
import { SponsorHomeComponent } from './home/home.component';
import { SponsorFOSharedModule } from './shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    SponsorHomeComponent
  ],
  imports: [
    SponsorFOSharedModule,
    SponsorFORoutingModule
  ]
})
export class SponsorFOModule { }
