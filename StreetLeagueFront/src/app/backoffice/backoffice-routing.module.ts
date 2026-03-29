import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';

const routes: Routes = [{ path: '', component: BackofficeComponent,   
    children: [                      
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {path: 'news', component: NewsComponent },
            {path: 'health', component: HealthComponent },

      
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }
