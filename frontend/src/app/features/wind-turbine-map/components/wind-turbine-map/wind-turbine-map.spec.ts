import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WindTurbineMap } from './wind-turbine-map';

describe('WindTurbineMap', () => {
  let component: WindTurbineMap;
  let fixture: ComponentFixture<WindTurbineMap>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WindTurbineMap]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WindTurbineMap);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
