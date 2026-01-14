import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, output, signal } from '@angular/core';
import { GlobalWeatherForm } from '../../models/globalWeatherForm';
import { EnvironmentService } from '../../services/environment-service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-weather-panel-component',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './weather-panel-component.html',
  styleUrl: './weather-panel-component.scss',
})
export class WeatherPanelComponent implements OnInit {
  private envService = inject(EnvironmentService);
  isVisible = signal(false);
  windSpeed = signal(0);
  airTemp = signal(0);
  weatherChanged = output<{windSpeed: number, airTemp: number}>();

  ngOnInit(): void {
  this.envService.getCurrentWeather().subscribe({
    next: (weather) => {
      // Ustawiamy sygnały na startowe wartości z bazy
      this.windSpeed.set(weather.globalWindSpeed);
      this.airTemp.set(weather.globalAirTemp);
    },
    error: (err) => console.error('Nie udało się pobrać pogody', err)
  });
}

  toggleVisibility() {
    this.isVisible.update(v => !v);
  }

  onWindSpeedChange(event: any) {
    this.windSpeed.set(parseFloat(event.target.value));
  }

  onAirTempChange(event: any) {
    this.airTemp.set(parseFloat(event.target.value));
  }

  onApply() {
  const form: GlobalWeatherForm = {
    windSpeed: this.windSpeed(),
    airTemp: this.airTemp()
  };

  this.envService.updateWeather(form).subscribe({
    next: () => {
      this.weatherChanged.emit(form); // Informujemy mapę
      // Tutaj możesz też wywołać SnackBar z informacją o sukcesie
    }
  });
}
}
