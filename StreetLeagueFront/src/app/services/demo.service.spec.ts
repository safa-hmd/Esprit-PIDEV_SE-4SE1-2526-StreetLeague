import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DemoService } from './demo.service';
import { API_BASE_URL } from 'src/environments/api-url';

describe('DemoService', () => {
  let service: DemoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DemoService],
    });
    service = TestBed.inject(DemoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call studentHello endpoint', () => {
    service.studentHello().subscribe((res) => expect(res).toBe('hello student'));

    const req = httpMock.expectOne(`${API_BASE_URL}/student/hello`);
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('text');
    req.flush('hello student');
  });

  it('should call teacherHello endpoint', () => {
    service.teacherHello().subscribe((res) => expect(res).toBe('hello teacher'));

    const req = httpMock.expectOne(`${API_BASE_URL}/teacher/hello`);
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('text');
    req.flush('hello teacher');
  });
});
