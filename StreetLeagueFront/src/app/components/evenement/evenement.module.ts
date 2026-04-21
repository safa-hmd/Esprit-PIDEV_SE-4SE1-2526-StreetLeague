import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { EvenementFormComponent } from './evenement-form.component';
import { EvenementListComponent } from './evenement-list.component';
import { EvenementDetailComponent } from './evenement-detail.component';
import { EvenementEditComponent } from './evenement-edit.component';

@NgModule({
  declarations: [
    EvenementFormComponent,
    EvenementListComponent,
    EvenementDetailComponent,
    EvenementEditComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
  ]
})
export class EvenementModule { }
