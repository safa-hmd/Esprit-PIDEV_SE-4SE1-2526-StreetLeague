import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { FooterComponent } from './footer/footer.component';
import { NavbarComponent } from './navbar/navbar.component';
import { HomeComponent } from './home/home.component';

import { CommunauteModule } from './communaute/communaute.module';
import { EvenementModule } from './evenement/evenement.module';

@NgModule({
  declarations: [
    FrontofficeComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FrontofficeRoutingModule,
    CommunauteModule,
    EvenementModule,
  ]
})
export class FrontofficeModule { }
