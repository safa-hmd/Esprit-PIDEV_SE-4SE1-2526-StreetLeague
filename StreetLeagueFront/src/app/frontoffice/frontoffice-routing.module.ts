import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { HomeComponent } from './home/home.component';

// Community
import { CommunauteListComponent } from './communaute/communaute-list.component';
import { CommunauteFormComponent } from './communaute/communaute-form.component';
import { CommunauteDetailComponent } from './communaute/communaute-detail.component';
import { CommunauteEditComponent } from './communaute/communaute-edit.component';

// Event
import { EvenementListComponent } from './evenement/evenement-list.component';
import { EvenementFormComponent } from './evenement/evenement-form.component';
import { EvenementDetailComponent } from './evenement/evenement-detail.component';
import { EvenementEditComponent } from './evenement/evenement-edit.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,
    children: [
      { path: '', component: HomeComponent },

      // Community routes
      { path: 'communaute', component: CommunauteListComponent },
      { path: 'communaute/new', component: CommunauteFormComponent },
      { path: 'communaute/:id', component: CommunauteDetailComponent },
      { path: 'communaute/:id/edit', component: CommunauteEditComponent },

      // Event routes
      { path: 'evenement', component: EvenementListComponent },
      { path: 'evenement/new', component: EvenementFormComponent },
      { path: 'evenement/:id', component: EvenementDetailComponent },
      { path: 'evenement/:id/edit', component: EvenementEditComponent },

      // ML Segmentation — lazy loaded
      {
        path: 'ml-segmentation',
        loadChildren: () =>
          import('./ml-segmentation/ml-segmentation.module').then(
            m => m.MlSegmentationModule
          )
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }
