import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';

import { AdminCommunauteModule } from '../components/admin-communaute/admin-communaute.module';
import { AdminEvenementModule } from '../components/admin-evenement/admin-evenement.module';
import { AdminSponsorModule } from '../components/admin-sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContratSponsorModule } from '../components/admin-contrat-sponsor/admin-contrat-sponsor.module';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    BackofficeRoutingModule,
    AdminCommunauteModule,
    AdminEvenementModule,
    AdminSponsorModule,
    AdminSponsoringEvenementModule,
    AdminContratSponsorModule,
  ]
})
export class BackofficeModule { }
