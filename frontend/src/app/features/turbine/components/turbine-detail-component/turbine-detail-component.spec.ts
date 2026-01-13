import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineDetailComponent } from './turbine-detail-component';

describe('TurbineDetailComponent', () => {
  let component: TurbineDetailComponent;
  let fixture: ComponentFixture<TurbineDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
