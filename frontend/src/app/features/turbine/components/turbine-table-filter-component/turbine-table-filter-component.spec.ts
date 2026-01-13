import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineTableFilterComponent } from './turbine-table-filter-component';

describe('TurbineTableFilterComponent', () => {
  let component: TurbineTableFilterComponent;
  let fixture: ComponentFixture<TurbineTableFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineTableFilterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineTableFilterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
