import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { CommandeListComponent } from './commande-list.component';

describe('CommandeListComponent', () => {
  let component: CommandeListComponent;
  let fixture: ComponentFixture<CommandeListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,  // ← pour CommandeService
        FormsModule,              // ← pour [(ngModel)] dans le template
      ],
      declarations: [CommandeListComponent],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(CommandeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});