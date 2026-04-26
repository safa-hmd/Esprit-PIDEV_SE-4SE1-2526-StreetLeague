import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldRecommenderComponent } from './field-recommender.component';

describe('FieldRecommenderComponent', () => {
  let component: FieldRecommenderComponent;
  let fixture: ComponentFixture<FieldRecommenderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FieldRecommenderComponent]
    });
    fixture = TestBed.createComponent(FieldRecommenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
