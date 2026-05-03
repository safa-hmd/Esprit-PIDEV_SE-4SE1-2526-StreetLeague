import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SponsorNavbarComponent } from '../navbar/navbar.component';
import { SponsorFooterComponent } from '../footer/footer.component';

@NgModule({
  declarations: [
    SponsorNavbarComponent,
    SponsorFooterComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SponsorNavbarComponent,
    SponsorFooterComponent
  ]
})
export class SponsorFOSharedModule { }
