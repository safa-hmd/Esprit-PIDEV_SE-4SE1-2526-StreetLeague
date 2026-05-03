import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ContratListComponent } from './contrat-list.component';
import { ContratFormComponent } from './contrat-form.component';
import { ContratEditComponent } from './contrat-edit.component';

@NgModule({
  declarations: [
    ContratListComponent,
    ContratFormComponent,
    ContratEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class ContratSponsorModule { }
