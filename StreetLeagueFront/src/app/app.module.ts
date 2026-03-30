import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {  HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { NavbarComponent } from './coach-fo/navbar/navbar.component';
import { DeliveryFoModule } from './delivery-fo/delivery-fo.module';
import { NotFoundComponent } from './shared/not-found/not-found.component';


@NgModule({
  declarations: [
    AppComponent,
    NotFoundComponent,
],
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
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
