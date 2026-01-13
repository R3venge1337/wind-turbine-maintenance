import { Injectable, signal } from '@angular/core';
import { Observable, map } from 'rxjs';
import { MeasurementDto } from '../../features/wind-turbine-map/models/measurementDto';
import { RxStomp, RxStompConfig, RxStompState } from '@stomp/rx-stomp';
import { TurbineDashboardUpdate, TurbineLiveViewDto } from '../../features/turbine/models/turbine';


@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private rxStomp: RxStomp;
  readonly connected = signal<boolean>(false);

  constructor() {
    this.rxStomp = new RxStomp();
    
    const config: RxStompConfig = {
      brokerURL: 'ws://localhost:8080/api/v1/ws-energy',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (msg) => console.log(new Date(), msg)
    };

    this.rxStomp.configure(config);

    // Monitorowanie stanu połączenia za pomocą sygnału
    this.rxStomp.connectionState$.subscribe(state => {
      this.connected.set(state === RxStompState.OPEN);
      console.log('Stan połączenia STOMP:', RxStompState[state]);
    });

    this.rxStomp.activate();
  }

  subscribeToAlerts(productId: string): Observable<TurbineDashboardUpdate> {
    // Upewnij się, że ścieżka zgadza się z messagingTemplate.convertAndSend w Javie
    return this.rxStomp.watch(`/topic/turbine-updates/${productId}`).pipe(
      map(message => JSON.parse(message.body) as TurbineDashboardUpdate)
    );
  }
}

