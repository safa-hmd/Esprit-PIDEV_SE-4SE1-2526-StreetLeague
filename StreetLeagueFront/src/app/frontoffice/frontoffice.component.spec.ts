import { ComponentFixture, TestBed } from '@angular/core/testing';
<<<<<<< HEAD
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

=======
import { NO_ERRORS_SCHEMA } from '@angular/core'; // ← زيد
>>>>>>> newsHealth
import { FrontofficeComponent } from './frontoffice.component';

describe('FrontofficeComponent', () => {
  let component: FrontofficeComponent;
  let fixture: ComponentFixture<FrontofficeComponent>;

<<<<<<< HEAD
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FrontofficeComponent],
      imports: [RouterTestingModule], // pour <router-outlet>
      schemas: [NO_ERRORS_SCHEMA]     // ignore navbar/footer
    }).compileComponents();

=======
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FrontofficeComponent],
      schemas: [NO_ERRORS_SCHEMA] // ← زيد
    });
>>>>>>> newsHealth
    fixture = TestBed.createComponent(FrontofficeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});