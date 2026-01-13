import { Component, computed, inject } from '@angular/core';
import { TurbineService } from '../../../features/turbine/services/turbine-service';
import { WebsocketService } from '../../services/websocket-service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-footer-component',
  imports: [TranslateModule],
  templateUrl: './footer-component.html',
  styleUrl: './footer-component.scss',
})
export class FooterComponent {
  private wsService = inject(WebsocketService);
  private turbineService = inject(TurbineService);
// Mapujemy sygnał z serwisu, aby był dostępny w HTML
  wsConnected = computed(() => this.wsService.connected());
  
  // Dodatkowo do footera: liczba turbin z TurbineService
  activeCount = computed(() => this.turbineService.turbines().length);
}
