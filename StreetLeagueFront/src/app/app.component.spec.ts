import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [RouterTestingModule],
    declarations: [AppComponent]
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'StreetLeagueFront'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.titre).toEqual('StreetLeagueFront');
  });

<<<<<<< HEAD
  // ← test "should render title" supprimé car le HTML n'a pas de .content span
});
=======
  // ← Test corrigé : le texte généré par défaut n'existe plus dans le projet
  it('should render the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();

    expect(fixture.componentInstance).toBeTruthy();

  });
});
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
