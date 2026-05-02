import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { AdvancedDashboardComponent } from './dashboard/advanced-dashboard.component';

import { AdminCommunauteModule } from './communaute/admin-communaute.module';
import { AdminEvenementModule } from './evenement/admin-evenement.module';
import { AdminSponsorModule } from './sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from './sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContractSponsorModule } from './contract-sponsor/admin-contract-sponsor.module';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    AdvancedDashboardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    BackofficeRoutingModule,
    AdminCommunauteModule,
    AdminEvenementModule,
    AdminSponsorModule,
    AdminSponsoringEvenementModule,
    AdminContractSponsorModule,
  ]
})
export class BackofficeModule { }
