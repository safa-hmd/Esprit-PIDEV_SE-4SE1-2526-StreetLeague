import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailTeamComponent } from './detail-team.component';
import { TeamService } from 'src/app/services/team.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('DetailTeamComponent', () => {
  let component: DetailTeamComponent;
  let fixture: ComponentFixture<DetailTeamComponent>;
  let teamServiceSpy: jasmine.SpyObj<TeamService>;
  let router: Router;

  const mockTeam = {
    idTeam: 1, name: 'Thunder FC', sport: 'Soccer',
    captainFullName: 'John', playerCount: 5,
    level: 'BEGINNER', description: 'A great team'
  };

  beforeEach(async () => {
    teamServiceSpy = jasmine.createSpyObj('TeamService', ['getTeamById']);
    teamServiceSpy.getTeamById.and.returnValue(of(mockTeam as any));

    await TestBed.configureTestingModule({
      declarations: [DetailTeamComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: TeamService, useValue: teamServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } }
        }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(DetailTeamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load team on init', () => {
    expect(teamServiceSpy.getTeamById).toHaveBeenCalledWith(1);
    expect(component.team).toEqual(mockTeam as any);
    expect(component.isLoading).toBeFalse();
  });

  it('ngOnInitTest — should set errorMsg when team not found', () => {
    teamServiceSpy.getTeamById.and.returnValue(
      throwError(() => ({ status: 404 }))
    );
    component.ngOnInit();
    expect(component.errorMsg).toContain('404');
    expect(component.isLoading).toBeFalse();
  });

  it('goBackTest — should navigate to client/team', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goBack();
    expect(navigateSpy).toHaveBeenCalledWith(['/client/team']);
  });

  it('goToTeamDetailTest — should navigate to detail-team', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToTeamDetail(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/client/detail-team', 1]);
  });
});