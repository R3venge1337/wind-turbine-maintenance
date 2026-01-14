import { Component, inject, OnInit, signal } from '@angular/core';
import { EnvironmentService } from '../../../features/wind-turbine-map/services/environment-service';
import { GlobalWeatherDto } from '../../../features/wind-turbine-map/models/globalWeatherDto';
import { CommonModule } from '@angular/common';
import { Subscription, switchMap, timer } from 'rxjs';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { LanguageSelectorComponent } from "../language-selector-component/language-selector-component";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-navbar-component',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive,LanguageSelectorComponent, TranslateModule],
  templateUrl: './navbar-component.html',
  styleUrl: './navbar-component.scss',
})
export class NavbarComponent implements OnInit {
  private weatherService = inject(EnvironmentService);
  private weatherSub?: Subscription;

  globalTemp = signal<number | null>(null);
  globalWind = signal<number | null>(null);

  ngOnInit() {
    this.startWeatherPolling();
  }

  startWeatherPolling() {
    this.weatherSub = timer(0, 30000).pipe(
      switchMap(() => this.weatherService.getCurrentWeather())
    ).subscribe({
      next: (data: GlobalWeatherDto) => {
        console.log('Pogoda odświeżona:', data);
        this.globalTemp.set(data.globalAirTemp);
        this.globalWind.set(data.globalWindSpeed);
      },
      error: (err) => console.error('Błąd odświeżania pogody:', err)
    });
  }

  ngOnDestroy() {
    this.weatherSub?.unsubscribe();
  }

}
