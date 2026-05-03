import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminContractSponsorListComponent } from './admin-contract-sponsor-list.component';
import { AdminContractSponsorFormComponent } from './admin-contract-sponsor-form.component';
import { AdminContractSponsorEditComponent } from './admin-contract-sponsor-edit.component';
import { ContractSponsorModule } from '../../sponsor-fo/contract-sponsor/contract-sponsor.module';
import { SponsorFOSharedModule } from '../../sponsor-fo/shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    AdminContractSponsorListComponent,
    AdminContractSponsorFormComponent,
    AdminContractSponsorEditComponent
  ],
  imports: [CommonModule, FormsModule, RouterModule, SponsorFOSharedModule, ContractSponsorModule]
})
export class AdminContractSponsorModule { }
