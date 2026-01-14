import { Component, inject, input, OnDestroy, OnInit, output, signal } from '@angular/core';
import { Subscription } from 'rxjs';
import { WebsocketService } from '../../../../core/services/websocket-service';
import { TurbineDashboardUpdate, TurbineView } from '../../../turbine/models/turbine';
import { DatePipe, DecimalPipe } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-card-component',
  standalone: true,
  imports: [DatePipe, DecimalPipe, TranslateModule],
  templateUrl: './turbine-card-component.html',
  styleUrl: './turbine-card-component.scss',
})
export class TurbineCardComponent implements OnInit, OnDestroy {
  turbine = input.required<TurbineView>();
  private wsService = inject(WebsocketService);
  cardClicked = output<TurbineView>();
  
 liveUpdate = signal<TurbineDashboardUpdate | null>(null);

  private sub?: Subscription;
  activeTab = signal<'ai' | 'measurements'>('ai');

  ngOnInit() {
    // Każda karta sama słucha swoich pomiarów
    this.sub = this.wsService.subscribeToAlerts(this.turbine().productId)
      .subscribe(data => {
        console.log(data)
        this.liveUpdate.set(data)
      });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  getSeverityClass() {
  const severity = this.liveUpdate()?.aiData.mainSeverity || this.turbine().mainSeverity;
  return `status-${severity?.toLowerCase()}`;
}

  onCardClick() {
    this.cardClicked.emit(this.turbine());
  }

  setTab(tab: 'ai' | 'measurements', event: MouseEvent) {
    event.stopPropagation(); // Nie chcemy centrować mapy przy zmianie zakładki
    this.activeTab.set(tab);
  }
}
