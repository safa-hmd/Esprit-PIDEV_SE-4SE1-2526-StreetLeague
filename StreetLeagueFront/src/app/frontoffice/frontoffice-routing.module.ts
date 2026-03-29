import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';

const routes: Routes = [
  { 
    path: '', 
    component: FrontofficeComponent,
    children: [
      { path: 'news', component: NewsComponent },
      { path: 'health', component: HealthComponent },

      { path: '', redirectTo: 'news', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }