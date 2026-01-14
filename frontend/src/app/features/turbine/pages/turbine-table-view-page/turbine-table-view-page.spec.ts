import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurbineTableViewPage } from './turbine-table-view-page';

describe('TurbineTableViewPage', () => {
  let component: TurbineTableViewPage;
  let fixture: ComponentFixture<TurbineTableViewPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurbineTableViewPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurbineTableViewPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
