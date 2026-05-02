import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ContractSponsorRoutingModule } from './contract-sponsor-routing.module';
import { ContractListComponent } from './contract-list.component';
import { ContractFormComponent } from './contract-form.component';
import { ContractEditComponent } from './contract-edit.component';
import { ContractChatComponent } from './chat/contract-chat.component';
import { SponsorFOSharedModule } from '../shared/sponsor-fo-shared.module';

@NgModule({
  declarations: [
    ContractListComponent,
    ContractFormComponent,
    ContractEditComponent,
    ContractChatComponent
  ],
  imports: [CommonModule, FormsModule, ContractSponsorRoutingModule, SponsorFOSharedModule],
  exports: [ContractChatComponent]
})
export class ContractSponsorModule { }
