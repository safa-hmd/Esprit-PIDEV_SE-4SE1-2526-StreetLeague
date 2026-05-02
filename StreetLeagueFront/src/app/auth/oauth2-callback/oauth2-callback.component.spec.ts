import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { OAuth2CallbackComponent } from './oauth2-callback.component';

describe('OAuth2CallbackComponent', () => {
  let component: OAuth2CallbackComponent;
  let fixture: ComponentFixture<OAuth2CallbackComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OAuth2CallbackComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ token: 'test-token' })
          }
        }
      ]
    });
    fixture = TestBed.createComponent(OAuth2CallbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
