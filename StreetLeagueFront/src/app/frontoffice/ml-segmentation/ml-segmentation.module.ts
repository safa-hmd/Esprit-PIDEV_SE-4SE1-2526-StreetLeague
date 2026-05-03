import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { MlSegmentationComponent } from './ml-segmentation.component';

const routes: Routes = [
  { path: '', component: MlSegmentationComponent }
];

@NgModule({
  declarations: [MlSegmentationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes)
  ]
})
export class MlSegmentationModule { }
