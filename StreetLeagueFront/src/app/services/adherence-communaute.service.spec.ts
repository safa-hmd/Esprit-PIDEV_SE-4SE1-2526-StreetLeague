import { TestBed } from '@angular/core/testing';
import { AdherenceCommunauteService } from './adherence-communaute.service';

describe('AdherenceCommunauteService', () => {
  let service: AdherenceCommunauteService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AdherenceCommunauteService],
    });
    service = TestBed.inject(AdherenceCommunauteService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should add membership in localStorage when join is called', () => {
    service.join(1, 100).subscribe((res) => expect(res.success).toBeTrue());

    const saved = JSON.parse(localStorage.getItem('MesCommunautes') || '[]');
    expect(saved.length).toBe(1);
    expect(saved[0].userId).toBe(1);
    expect(saved[0].communauteId).toBe(100);
  });

  it('should return true for estMembre after join', () => {
    service.join(2, 200).subscribe();

    service.estMembre(2, 200).subscribe((isMember) => {
      expect(isMember).toBeTrue();
    });
  });

  it('should remove membership when leave is called', () => {
    service.join(3, 300).subscribe();

    service.leave(3, 300).subscribe((res) => expect(res).toBeUndefined());

    const saved = JSON.parse(localStorage.getItem('MesCommunautes') || '[]');
    expect(saved.length).toBe(0);
  });
});
