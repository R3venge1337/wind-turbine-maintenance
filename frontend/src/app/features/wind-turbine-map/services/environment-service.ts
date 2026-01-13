import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GlobalWeatherDto } from '../models/globalWeatherDto';
import { GlobalWeatherForm } from '../models/globalWeatherForm';

@Injectable({
  providedIn: 'root',
})
export class EnvironmentService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/weather'; 

 
  getCurrentWeather(): Observable<GlobalWeatherDto> {
    return this.http.get<GlobalWeatherDto>(this.API_URL);
  }

  updateWeather(form: GlobalWeatherForm): Observable<void> {
    return this.http.post<void>(this.API_URL, form);
  }
}
