import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CoachFORoutingModule } from './coach-fo-routing.module';
import { CoachFOComponent } from './coach-fo.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';


@NgModule({
  declarations: [
    CoachFOComponent,
    NavbarComponent,
    FooterComponent
  ],
  imports: [
    CommonModule,
    CoachFORoutingModule
  ]
})
export class CoachFOModule { }
