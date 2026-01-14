import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { catchError, firstValueFrom, Observable, tap, throwError } from 'rxjs';
import { CreateTurbineForm, FilterTurbineForm, TurbineView } from '../models/turbine';
import { PageableRequest } from '../../../shared/models/pageRequest';
import { PageDto } from '../../../shared/models/pageDto';
import { MeasurementDto } from '../../wind-turbine-map/models/measurementDto';

@Injectable({
  providedIn: 'root',
})
export class TurbineService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/turbines';

  turbines = signal<TurbineView[]>([]);

   findAllActiveTurbines(filters: FilterTurbineForm, pageableRequest:PageableRequest): Observable<PageDto<TurbineView>> {
    let params = new HttpParams();
    params = params.set('page', pageableRequest.page.toString());
    params = params.set('size', pageableRequest.size.toString())

    if (pageableRequest.sortField) {
        params = params.set('sortField', pageableRequest.sortField);
        params = params.set('sortDirection', pageableRequest.sortDirection || 'ASC');
    }
    
     return this.http.post<PageDto<TurbineView>>(`${this.API_URL}/search`, filters ,{params: params})
  }

  createTurbine(formData: CreateTurbineForm): Observable<TurbineView> {
  return this.http.post<TurbineView>(this.API_URL, formData).pipe(
    tap((newTurbine) => {
      // Aktualizujemy sygnał turbines o nową turbinę
      this.turbines.update(current => [...current, newTurbine]);
    }),
    catchError((error) => {
      console.error('Błąd podczas tworzenia turbiny:', error);
      // Przekazujemy błąd dalej do komponentu
      return throwError(() => error);
    })
  );
}
/**
   * Pobiera całkowitą liczbę turbin (użyjemy tego np. w Twoim nowym footerze)
   */
  async countTurbines(): Promise<number> {
    try {
      return await firstValueFrom(
        this.http.get<number>(`${this.API_URL}/count`)
      );
    } catch (error) {
      console.error('Błąd podczas pobierania liczby turbin:', error);
      return 0;
    }
  }

  /**
   * Pobiera szczegółowe dane jednej turbiny na podstawie productId
   * Przyda się, gdy klikniesz w turbinę na mapie, aby otworzyć panel boczny
   */
   findTurbineByProductId(productId: string): Observable<TurbineView> {
    try {
      return this.http.get<TurbineView>(`${this.API_URL}/${productId}`)
    } catch (error) {
      console.error(`Błąd podczas pobierania turbiny ${productId}:`, error);
      throw error;
    }
  }

  /**
   * Usuwa turbinę i aktualizuje lokalny stan (Signals)
   */
  async deleteTurbine(productId: string): Promise<void> {
    try {
      await firstValueFrom(
        this.http.delete<void>(`${this.API_URL}/${productId}`)
      );
      
      // Po udanym usunięciu na backendzie, usuwamy ją też z mapy (lokalnego sygnału)
      this.turbines.update(current => 
        current.filter(t => t.productId !== productId)
      );
      
      console.log(`Turbina ${productId} została usunięta.`);
    } catch (error) {
      console.error(`Błąd podczas usuwania turbiny ${productId}:`, error);
      throw error;
    }
  }

  updateTurbineFromSocket(updatedTurbine: TurbineView) {
    this.turbines.update(current => {
      const index = current.findIndex(t => t.id === updatedTurbine.id);
      if (index !== -1) {
        const newList = [...current];
        newList[index] = updatedTurbine;
        return newList;
      }
      return [...current, updatedTurbine];
    });
  }

  getLatestMeasurements(productId: string): Observable<MeasurementDto[]> {
  return this.http.get<MeasurementDto[]>(`${this.API_URL}/${productId}/measurements`);
  }

  resetTurbine(productId: string): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${productId}/reset-maintenance`, {});
  }
}

