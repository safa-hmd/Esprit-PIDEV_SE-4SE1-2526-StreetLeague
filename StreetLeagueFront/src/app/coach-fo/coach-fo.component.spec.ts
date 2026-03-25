import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CoachFOComponent } from './coach-fo.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';

@Component({ selector: 'app-navbar', template: '' })
class MockNavbarComponent {}

@Component({ selector: 'app-footer', template: '' })   // ← ajouter
class MockFooterComponent {}

describe('CoachFOComponent', () => {
  let component: CoachFOComponent;
  let fixture: ComponentFixture<CoachFOComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CoachFOComponent,
        MockNavbarComponent,
        MockFooterComponent,    // ← ajouter
      ],
      imports: [RouterTestingModule, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CoachFOComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});