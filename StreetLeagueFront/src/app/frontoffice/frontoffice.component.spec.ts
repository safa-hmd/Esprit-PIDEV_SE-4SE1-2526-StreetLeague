import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FrontofficeComponent } from './frontoffice.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

// Composants fictifs pour éviter les erreurs "not known element"
@Component({ selector: 'app-navbar', template: '' })
class MockNavbarComponent {}

@Component({ selector: 'app-footer', template: '' })
class MockFooterComponent {}

describe('FrontofficeComponent', () => {
  let component: FrontofficeComponent;
  let fixture: ComponentFixture<FrontofficeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        FrontofficeComponent,
        MockNavbarComponent,   // ← mock navbar
        MockFooterComponent,   // ← mock footer si utilisé
      ],
      imports: [
        RouterTestingModule,           // ← gère les routes
        HttpClientTestingModule,       // ← gère les appels HTTP
      ]
    });
    fixture = TestBed.createComponent(FrontofficeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});