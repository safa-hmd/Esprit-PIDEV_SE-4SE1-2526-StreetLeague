import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailMatchComponent } from './detail-match.component';
import { MatchService } from '../../services/match.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('DetailMatchComponent', () => {
  let component: DetailMatchComponent;
  let fixture: ComponentFixture<DetailMatchComponent>;
  let matchServiceSpy: jasmine.SpyObj<MatchService>;
  let router: Router;

  const mockMatch = {
    idMatch: 1, teamAName: 'Thunder FC', teamBName: 'Lions FC',
    matchDate: '2026-05-01T18:00:00', location: 'Park',
    status: 'SCHEDULED', scoreTeamA: 0, scoreTeamB: 0, captainName: 'John'
  };

  beforeEach(async () => {
    matchServiceSpy = jasmine.createSpyObj('MatchService', ['getMatchById']);
    matchServiceSpy.getMatchById.and.returnValue(of(mockMatch as any));

    await TestBed.configureTestingModule({
      declarations: [DetailMatchComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: MatchService, useValue: matchServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } }
        }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(DetailMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load match on init', () => {
    expect(matchServiceSpy.getMatchById).toHaveBeenCalledWith(1);
    expect(component.match).toEqual(mockMatch as any);
    expect(component.isLoading).toBeFalse();
  });

  it('ngOnInitTest — should set errorMsg when match not found', () => {
    matchServiceSpy.getMatchById.and.returnValue(throwError(() => ({})));
    component.ngOnInit();
    expect(component.errorMsg).toBe('Match not found.');
    expect(component.isLoading).toBeFalse();
  });

  it('ngOnInitTest — should navigate when no id', () => {
    const navigateSpy = spyOn(router, 'navigate');
    TestBed.inject(ActivatedRoute).snapshot.paramMap.get = () => null;
    component.ngOnInit();
    expect(navigateSpy).toHaveBeenCalledWith(['/frontoffice/team']);
  });

  it('goBackTest — should navigate to client/team with matches tab', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goBack();
    expect(navigateSpy).toHaveBeenCalledWith(
      ['/client/team'],
      { queryParams: { tab: 'matches' } }
    );
  });

  it('getStatusClassTest — SCHEDULED returns badge-blue', () => {
    expect(component.getStatusClass('SCHEDULED')).toBe('badge-blue');
  });

  it('getStatusClassTest — ONGOING returns badge-green', () => {
    expect(component.getStatusClass('ONGOING')).toBe('badge-green');
  });

  it('getStatusClassTest — FINISHED returns badge-gray', () => {
    expect(component.getStatusClass('FINISHED')).toBe('badge-gray');
  });

  it('getStatusClassTest — CANCELLED returns badge-red', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('badge-red');
  });

  it('getStatusClassTest — unknown returns empty string', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('');
  });
});