import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { NewsComponent } from './news/news.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { HealthComponent } from './health/health.component';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    NewsComponent,
    HealthComponent
  ],
  imports: [
    CommonModule,
    BackofficeRoutingModule,
    CommonModule, 
    FormsModule, 
    HttpClientModule, 
    BackofficeRoutingModule
  ]
})
export class BackofficeModule { }
