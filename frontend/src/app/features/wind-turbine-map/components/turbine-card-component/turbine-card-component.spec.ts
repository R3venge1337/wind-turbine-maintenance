import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineCardComponent } from './turbine-card-component';

describe('TurbineCardComponent', () => {
  let component: TurbineCardComponent;
  let fixture: ComponentFixture<TurbineCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
