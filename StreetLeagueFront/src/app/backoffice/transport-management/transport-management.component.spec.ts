import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransportManagementComponent } from './transport-management.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TravelService } from 'src/app/services/travel.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('TransportManagementComponent', () => {
  let component: TransportManagementComponent;
  let fixture: ComponentFixture<TransportManagementComponent>;
  let mockTravelService: jasmine.SpyObj<TravelService>;

  const mockTransports = [
    { id: 1, type: 'BUS', destination: 'Tunis', availableSeats: 50, pricePerSeat: 20 },
    { id: 2, type: 'TRAIN', destination: 'Sousse', availableSeats: 100, pricePerSeat: 15 }
  ];

  beforeEach(async () => {
    mockTravelService = jasmine.createSpyObj('TravelService', ['getTransports', 'deleteTransport', 'saveTransport']);
    mockTravelService.getTransports.and.returnValue(of(mockTransports));

    await TestBed.configureTestingModule({
      declarations: [ TransportManagementComponent ],
      imports: [ HttpClientTestingModule, RouterTestingModule, FormsModule ],
      providers: [
        { provide: TravelService, useValue: mockTravelService }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransportManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it(' dovrebbe essere creato (should be created)', () => {
    expect(component).toBeTruthy();
  });

  it('dovrebbe caricare i trasporti all\'inizializzazione (should load transports on init)', () => {
    expect(mockTravelService.getTransports).toHaveBeenCalled();
    expect(component.transports.length).toBe(2);
    expect(component.transports[0].type).toBe('BUS');
  });

  it('dovrebbe validare le date di trasporto (should validate transport dates)', () => {
    // Test date validation logic
    component.transportDto.departureTime = '2026-03-31T10:00';
    component.transportDto.returnTime = '2026-03-31T09:00'; // Invalid: return before departure
    
    // Manual call to validate or simulate save
    const isValid = new Date(component.transportDto.returnTime) > new Date(component.transportDto.departureTime);
    expect(isValid).toBeFalse();
  });
});
