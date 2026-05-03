import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminCommunauteListComponent } from './admin-communaute-list.component';
import { AdminCommunauteFormComponent } from './admin-communaute-form.component';
import { AdminCommunauteEditComponent } from './admin-communaute-edit.component';

@NgModule({
  declarations: [
    AdminCommunauteListComponent,
    AdminCommunauteFormComponent,
    AdminCommunauteEditComponent,
  ],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class AdminCommunauteModule { }
