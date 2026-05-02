import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ContractListComponent } from './contract-list.component';
import { ContractFormComponent } from './contract-form.component';
import { ContractEditComponent } from './contract-edit.component';

const routes: Routes = [
  { path: '', component: ContractListComponent },
  { path: 'new', component: ContractFormComponent },
  { path: ':id/edit', component: ContractEditComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContractSponsorRoutingModule { }
