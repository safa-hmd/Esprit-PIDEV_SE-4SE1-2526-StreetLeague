import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommunauteFormComponent } from './communaute-form.component';
import { CommunauteListComponent } from './communaute-list.component';
import { CommunauteDetailComponent } from './communaute-detail.component';
import { CommunauteEditComponent } from './communaute-edit.component';

@NgModule({
  declarations: [
    CommunauteFormComponent,
    CommunauteListComponent,
    CommunauteDetailComponent,
    CommunauteEditComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ]
})
export class CommunauteModule { }

