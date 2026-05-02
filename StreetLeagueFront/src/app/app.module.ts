
import { BrowserModule } from '@angular/platform-browser';
import {  HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { NavbarComponent } from './coach-fo/navbar/navbar.component';
import { DeliveryFoModule } from './delivery-fo/delivery-fo.module';
import { NotFoundComponent } from './shared/not-found/not-found.component';

import { NgModule, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';


registerLocaleData(localeFr);  // ← ajoute avant @NgModule



@NgModule({
  declarations: [
    AppComponent,
<<<<<<< HEAD
    NotFoundComponent,
  ],
=======

    NotFoundComponent,
  
],

>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    DeliveryFoModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
      
    },
    { provide: LOCALE_ID, useValue: 'fr' }  
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
