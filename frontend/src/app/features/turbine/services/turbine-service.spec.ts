import { TestBed } from '@angular/core/testing';

import { TurbineService } from './turbine-service';

describe('TurbineService', () => {
  let service: TurbineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TurbineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
