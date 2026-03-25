import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BackofficeComponent } from './backoffice.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';

@Component({ selector: 'app-header', template: '' })
class MockHeaderComponent {}

@Component({ selector: 'app-menu', template: '' })    // ← ajouter
class MockMenuComponent {}

@Component({ selector: 'app-sidebar', template: '' })
class MockSidebarComponent {}

describe('BackofficeComponent', () => {
  let component: BackofficeComponent;
  let fixture: ComponentFixture<BackofficeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        BackofficeComponent,
        MockHeaderComponent,
        MockMenuComponent,      // ← ajouter
        MockSidebarComponent,
      ],
      imports: [RouterTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(BackofficeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});