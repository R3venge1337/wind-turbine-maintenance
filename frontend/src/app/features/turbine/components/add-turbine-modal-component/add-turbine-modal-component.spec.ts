import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTurbineModalComponent } from './add-turbine-modal-component';

describe('AddTurbineModalComponent', () => {
  let component: AddTurbineModalComponent;
  let fixture: ComponentFixture<AddTurbineModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddTurbineModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddTurbineModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
