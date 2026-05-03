import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccommodationRequestsComponent } from './accommodation-requests.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TravelService } from 'src/app/services/travel.service';
import { of } from 'rxjs';

describe('AccommodationRequestsComponent', () => {
  let component: AccommodationRequestsComponent;
  let fixture: ComponentFixture<AccommodationRequestsComponent>;
  let mockTravelService: jasmine.SpyObj<TravelService>;

  const mockAccommodationRequests = [
    { id: 1, status: 'PENDING', totalAmount: 150, accommodation: { type: 'HOTEL' } },
    { id: 2, status: 'APPROVED', totalAmount: 300, accommodation: { type: 'HOSTEL' } }
  ];

  beforeEach(async () => {
    mockTravelService = jasmine.createSpyObj('TravelService', ['getAccommodationRequests', 'approveAccommodationRequest', 'rejectAccommodationRequest']);
    mockTravelService.getAccommodationRequests.and.returnValue(of(mockAccommodationRequests));

    await TestBed.configureTestingModule({
      declarations: [ AccommodationRequestsComponent ],
      imports: [ HttpClientTestingModule, RouterTestingModule ],
      providers: [
        { provide: TravelService, useValue: mockTravelService }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccommodationRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('dovrebbe essere creato (should be created)', () => {
    expect(component).toBeTruthy();
  });

  it('dovrebbe caricare le richieste all\'inizializzazione (should load requests on init)', () => {
    expect(mockTravelService.getAccommodationRequests).toHaveBeenCalled();
    expect(component.accommodationRequests.length).toBe(2);
  });
});
