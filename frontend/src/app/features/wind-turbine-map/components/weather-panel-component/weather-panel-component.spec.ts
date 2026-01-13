import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeatherPanelComponent } from './weather-panel-component';

describe('WeatherPanelComponent', () => {
  let component: WeatherPanelComponent;
  let fixture: ComponentFixture<WeatherPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WeatherPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
