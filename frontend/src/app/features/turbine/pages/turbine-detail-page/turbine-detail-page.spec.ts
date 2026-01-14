import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineDetailPage } from './turbine-detail-page';

describe('TurbineDetailPage', () => {
  let component: TurbineDetailPage;
  let fixture: ComponentFixture<TurbineDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineDetailPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineDetailPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
