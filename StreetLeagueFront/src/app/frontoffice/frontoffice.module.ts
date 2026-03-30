import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { FooterComponent } from './footer/footer.component';
import { NavbarComponent } from './navbar/navbar.component';

import { CommunauteModule } from '../components/communaute/communaute.module';
import { EvenementModule } from '../components/evenement/evenement.module';
import { SponsorModule } from '../components/sponsor/sponsor.module';
import { SponsoringEvenementModule } from '../components/sponsoring-evenement/sponsoring-evenement.module';
import { ContratSponsorModule } from '../components/contrat-sponsor/contrat-sponsor.module';

@NgModule({
  declarations: [
    FrontofficeComponent,
    NavbarComponent,
    FooterComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FrontofficeRoutingModule,
    CommunauteModule,
    EvenementModule,
    SponsorModule,
    SponsoringEvenementModule,
    ContratSponsorModule,
  ]
})
export class FrontofficeModule { }
