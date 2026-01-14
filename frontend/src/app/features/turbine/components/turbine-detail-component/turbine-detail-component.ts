import { Component, computed, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeasurementDto } from '../../../wind-turbine-map/models/measurementDto';
import { TurbineView } from '../../models/turbine';
import { RouterLink } from '@angular/router';
import { TurbineService } from '../../services/turbine-service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-detail-component',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule],
  templateUrl: './turbine-detail-component.html',
  styleUrl: './turbine-detail-component.scss',
})
export class TurbineDetailComponent {
  tService = inject(TurbineService)
  turbine = input.required<TurbineView>();
  measurements = input.required<MeasurementDto[]>();
  isResetting = signal(false);

  latestTorque = computed(() => {
    const m = this.measurements();
    return m.length > 0 ? m[0].torque : 0;
  });

  realToolUsage = computed(() => {
    return this.turbine().currentToolWear * this.latestTorque();
  });

  calculateWearPercentage = computed(() => {
    const limit = this.turbine().osfLimit;
    if (!limit || limit === 0) return 0;
    
    const percent = (this.realToolUsage() / limit) * 100;
    return Math.min(percent, 100); 
  });


  resetTool() {
    const productId = this.turbine().productId;

    // 1. Potwierdzenie operacji
    if (!confirm(`Czy na pewno chcesz zresetować zużycie narzędzia dla turbiny ${productId}?`)) {
      return;
    }

    // 2. Blokada przycisku (zapobieganie wielokrotnym kliknięciom)
    this.isResetting.set(true);

    // 3. Wywołanie PATCH do backendu
    this.tService.resetTurbine(productId).subscribe({
      next: () => {
        this.isResetting.set(false);
        alert('Narzędzie zostało pomyślnie zresetowane.');
        
        // 4. Odświeżenie danych - tutaj wywołaj metodę, która pobiera dane turbiny
        // Np. jeśli masz metodę loadTurbine() w tym komponencie:
        // this.loadTurbine(productId); 
        
        // LUB (jeśli używasz pollingu) dane same się zaktualizują przy następnym cyklu
      },
      error: (err) => {
        this.isResetting.set(false);
        console.error('Błąd podczas resetowania:', err);
        alert('Wystąpił błąd podczas komunikacji z serwerem.');
      }
    });
  }
}
