import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineTableComponent } from './turbine-table-component';

describe('TurbineTableComponent', () => {
  let component: TurbineTableComponent;
  let fixture: ComponentFixture<TurbineTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
