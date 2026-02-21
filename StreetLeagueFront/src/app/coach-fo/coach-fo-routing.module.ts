import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoachFOComponent } from './coach-fo.component';

const routes: Routes = [{ path: '', component: CoachFOComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoachFORoutingModule { }
