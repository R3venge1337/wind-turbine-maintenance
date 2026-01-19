import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, AfterViewInit, inject, PLATFORM_ID, OnInit, computed, signal, ApplicationRef, EnvironmentInjector, createComponent, OnDestroy } from '@angular/core';
import { WebsocketService } from '../../../../core/services/websocket-service';
import { TurbineService } from '../../../turbine/services/turbine-service';
import { CreateTurbineForm, FilterTurbineForm, TurbineLiveViewDto, TurbineView } from '../../../turbine/models/turbine';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PageableRequest, SortDirection } from '../../../../shared/models/pageRequest';
import { AddTurbineModalComponent } from '../../../turbine/components/add-turbine-modal-component/add-turbine-modal-component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WeatherPanelComponent } from '../weather-panel-component/weather-panel-component';
import { EnvironmentService } from '../../services/environment-service';
import { GlobalWeatherForm } from '../../models/globalWeatherForm';
import { debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { TurbineCardComponent } from "../turbine-card-component/turbine-card-component";
import * as L from 'leaflet';
import 'leaflet.markercluster';
import { TranslateModule } from '@ngx-translate/core';
import { TurbineMapper } from '../../../../shared/mappers/turbineMapper';


@Component({
  selector: 'app-wind-turbine-map',
  imports: [CommonModule, ReactiveFormsModule, WeatherPanelComponent, TranslateModule],
  templateUrl: './wind-turbine-map.html',
  styleUrl: './wind-turbine-map.scss',
})
export class WindTurbineMap implements AfterViewInit, OnInit, OnDestroy {
  private markersMap: Map<string, L.Marker> = new Map();
  private turbineSubs: Map<string, Subscription> = new Map();
 private map!: L.Map;
  private platformId = inject(PLATFORM_ID);
  private turbineService = inject(TurbineService);
  private wsService = inject(WebsocketService);
  private envService = inject(EnvironmentService);
  private snackBar = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private injector = inject(EnvironmentInjector);
private appRef = inject(ApplicationRef);
 private markersLayer = (L as any).markerClusterGroup({
  chunkedLoading: true,
  spiderfyOnMaxZoom: true,
  showCoverageOnHover: false,
  // Ta funkcja decyduje o wyglądzie kółka klastra
  iconCreateFunction: (cluster: any) => {
    const count = cluster.getChildCount();
    let sizeClass = 'cluster-small';

    if (count > 50) {
      sizeClass = 'cluster-large';
    } else if (count > 10) {
      sizeClass = 'cluster-medium';
    }

    return L.divIcon({
      html: `<div class="cluster-inner ${sizeClass}"><span>${count}</span></div>`,
      className: 'turbine-cluster-container',
      iconSize: L.point(40, 40)
    });
  }
});
  private isInitialLoad = true;
  private readonly INITIAL_CENTER: L.LatLngExpression = [52.23, 19.27];
  private readonly INITIAL_ZOOM = 7;
  selectedTurbine = signal<TurbineView | null>(null);

  private dialog = inject(MatDialog);
  isSidebarVisible = signal(false);

  toggleSidebar() {
  this.isSidebarVisible.update(visible => !visible);
  
  setTimeout(() => {
    this.map.invalidateSize();
  }, 300);
}
  turbines = signal<TurbineView[]>([]);
  totalElements = signal(0);

totalPages = computed(() => {
  return Math.ceil(this.totalElements() / this.pagination().size) || 1;
});

isFiltering = computed(() => {
  const f = this.currentFilters();
  // Sprawdzamy czy którekolwiek pole ma wartość (pomijamy puste stringi i null)
  return !!(f.productId || f.typeCode || f.city || f.minToolWear !== null || f.maxToolWear !== null);
});
  
  pagination = signal<PageableRequest>({
    page: 1, // Zgodnie z Twoim @Min(1)
    size: 10,
    sortField: 'productId',
    sortDirection: SortDirection.ASC
  });

filterForm: FormGroup = this.fb.group({
    productId: [''],
    typeCode: [''],
    severity: [''],
    city: [''],
    minToolWear: [null],
    maxToolWear: [null]
  });
currentFilters = signal(this.filterForm.value);

  ngOnInit(): void {
  this.clearFilters();

  this.filterForm.valueChanges.pipe(
    debounceTime(400),      // czekaj 400ms aż użytkownik przestanie pisać
    distinctUntilChanged()  // wyślij tylko jeśli wartość faktycznie się zmieniła
  ).subscribe(value => {
    this.currentFilters.set(value);
    
    // Ważne: przy zmianie filtrów zawsze wracamy na 1. stronę
    this.pagination.update(p => ({ ...p, page: 1 }));
    
    this.applyFilters();
  });
  }

  ngAfterViewInit(): void {
    // Sprawdzamy, czy kod uruchamia się w przeglądarce (ważne przy SSR/Hydracji)
    if (isPlatformBrowser(this.platformId)) {
      this.initMap();
    }
  }

  private initMap(): void {
   const polandBounds = L.latLngBounds(
    L.latLng(48.9, 14.0),
    L.latLng(55.0, 24.1)
  );

  // 2. Naprawa ikon (można zrobić przed mapą)
  const iconDefault = L.icon({
    iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
    iconUrl: 'assets/leaflet/marker-icon.png',
    shadowUrl: 'assets/leaflet/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    tooltipAnchor: [16, -28],
    shadowSize: [41, 41]
  });
  L.Marker.prototype.options.icon = iconDefault;

  // 3. KLUCZOWE: Inicjalizacja obiektu mapy musi być PIERWSZA
  this.map = L.map('map', {
    center: this.INITIAL_CENTER,
    zoom: this.INITIAL_ZOOM,
    minZoom: 7,
    maxBounds: polandBounds,
    maxBoundsViscosity: 1.0
  });

  this.map.on('click', () => {
    if (this.isSidebarVisible()) {
      this.isSidebarVisible.set(false);
      // Opcjonalnie: this.clearFilters(); // Jeśli naprawdę chcesz resetu przy każdym zamknięciu
    }
  });

  // 4. Dopiero teraz dodajemy kafelki do zainicjalizowanej mapy
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap contributors'
  }).addTo(this.map);


  this.markersLayer.addTo(this.map);

  // 6. Odświeżenie rozmiaru
  setTimeout(() => {
    this.map.invalidateSize();
  }, 100);
 }

  private createTurbineIcon(severity: string, rpm: number = 0): L.DivIcon {
  // Mapowanie kolorów na Twoje nowe statusy
  const colors: { [key: string]: string } = {
    'FAILURE': '#ff1a1a', // Intensywny czerwony
    'CAUTION': '#ffa500', // Pomarańczowy
    'GOOD': '#2eb82e',    // Zielony
    'NONE': '#808080'     // Szary
  };

  const color = colors[severity] || colors['NONE'];
  
  // Obliczamy prędkość animacji: 60 sekund / RPM = czas jednego pełnego obrotu
  // Jeśli RPM jest wysoki, czas (duration) jest krótki -> łopaty kręcą się szybciej.
  const duration = rpm > 0 ? (60 / rpm).toFixed(2) : 0;

 return L.divIcon({
    className: 'custom-turbine-icon',
    // Zmniejszone rozmiary kontenera i przesunięcie punktu zakotwiczenia
    html: `
      <div class="icon-wrapper" style="color: ${color}; display: flex; justify-content: center; align-items: center;">
        <svg viewBox="0 0 102.562 102.562" width="40" height="40" xmlns="http://www.w3.org/2000/svg" 
             style="filter: drop-shadow(0px 2px 2px rgba(0,0,0,0.4));">
          <g fill="currentColor" stroke="currentColor" stroke-width="0.8">
            <path d="M88.734,51.517c-0.63-0.575-1.279-1.095-1.93-1.602c-1.314-0.999-2.663-1.896-4.045-2.695
                c-2.752-1.605-5.63-2.881-8.59-3.905c-2.964-1.02-6.01-1.79-9.172-2.228c-1.581-0.225-3.192-0.351-4.835-0.38
                c-0.582-0.004-1.167,0.008-1.756,0.029c-0.157-3.066-2.368-5.597-5.289-6.233c0.211-0.544,0.407-1.083,0.589-1.624
                c0.512-1.566,0.922-3.133,1.227-4.695c0.62-3.133,0.883-6.266,0.89-9.396c0.004-3.133-0.263-6.266-0.879-9.394
                c-0.305-1.567-0.712-3.133-1.226-4.7c-0.271-0.782-0.547-1.566-0.885-2.348C52.503,1.565,52.145,0.782,51.68,0
                c-0.464,0.782-0.828,1.565-1.157,2.348c-0.333,0.782-0.614,1.565-0.879,2.348c-0.515,1.567-0.925,3.133-1.23,4.7
                c-0.618,3.131-0.88,6.261-0.876,9.394c0.003,3.129,0.273,6.262,0.893,9.396c0.301,1.565,0.711,3.133,1.223,4.695
                c0.182,0.541,0.382,1.083,0.589,1.624c-2.926,0.636-5.146,3.181-5.292,6.264c-0.017,0-0.031,0-0.048-0.003
                c-0.849-0.06-1.682-0.07-2.51-0.063c-1.646,0.025-3.261,0.15-4.839,0.376c-3.16,0.441-6.206,1.215-9.166,2.238
                c-2.958,1.03-5.835,2.303-8.586,3.915c-1.384,0.803-2.729,1.699-4.04,2.695c-0.655,0.51-1.307,1.026-1.935,1.595
                c-0.638,0.571-1.254,1.167-1.843,1.851c0.89,0.179,1.749,0.268,2.596,0.322c0.849,0.058,1.679,0.068,2.503,0.062
                c1.649-0.031,3.265-0.154,4.839-0.377c3.164-0.438,6.209-1.215,9.171-2.23c2.96-1.021,5.835-2.295,8.591-3.903
                c1.384-0.799,2.731-1.696,4.042-2.695c0.532-0.414,1.058-0.84,1.576-1.293c0.602,1.774,1.928,3.213,3.624,3.978l-5.146,55.328
                h15.806l-5.346-55.237c1.622-0.666,2.93-1.949,3.627-3.558c0.322,0.264,0.644,0.52,0.973,0.772
                c1.313,0.996,2.658,1.896,4.038,2.695c2.758,1.608,5.629,2.879,8.589,3.914c2.964,1.026,6.003,1.8,9.168,2.237
                c1.582,0.227,3.193,0.353,4.843,0.38c0.825,0.004,1.656-0.003,2.505-0.064c0.849-0.055,1.708-0.144,2.598-0.329
                C89.987,52.68,89.368,52.079,88.734,51.517z M51.683,43.691c-1.434,0-2.601-1.167-2.601-2.6c0-1.434,1.167-2.597,2.601-2.597
                c1.433,0,2.598,1.164,2.598,2.597C54.28,42.52,53.115,43.691,51.683,43.691z"/>
          </g>
        </svg>
      </div>
    `,
    iconSize: [40, 40],
    iconAnchor: [20, 38] // Punkt zakotwiczenia na samym dole (środek podstawy)
  });
}

// 2. Uzupełnij updateMapMarkers, aby wywoływało refreshMarkers
private updateMapMarkers(data: TurbineView[]) {
  this.refreshMarkers(data); // Wywołujemy istniejącą u Ciebie metodę
}

// 3. Popraw refreshMarkers, aby automatycznie centrował mapę na wynikach (opcjonalnie)
private refreshMarkers(turbines: TurbineView[]) {
  this.markersLayer.clearLayers(); 
  this.markersMap.clear();

  if (turbines.length === 0) return;

  const markers: L.Marker[] = [];

  turbines.forEach(t => {
  if (t.latitude && t.longitude) {
    const icon = this.createTurbineIcon(t.mainSeverity);
    const marker = L.marker([t.latitude, t.longitude], { icon });
    const componentRef = createComponent(TurbineCardComponent, {
  environmentInjector: this.injector
});
componentRef.setInput('turbine', t);
this.appRef.attachView(componentRef.hostView);
const popupContent = componentRef.location.nativeElement;
marker.bindPopup(popupContent, {
  maxWidth: 350,
  minWidth: 300,
  className: 'turbine-popup-container',
  autoPanPadding: L.point(50, 50), // Wymusza zachowanie odstępu od krawędzi okna
  keepInView: true // własna klasa do stylizacji
});

marker.on('popupclose', () => {
  // Jeśli chcesz niszczyć komponent przy każdym zamknięciu:
  // this.appRef.detachView(componentRef.hostView);
  // componentRef.destroy();
});
this.markersMap.set(t.productId, marker);
    // PO KLIKNIĘCIU: Ustawiamy wybraną turbinę
    marker.on('click', () => {
      this.selectedTurbine.set(t);
      // Opcjonalnie: wycentruj mapę na tym punkcie
      this.map.panTo([t.latitude, t.longitude]);
    });
    
    this.markersLayer.addLayer(marker);
  }
})
}

applyFilters() {
  const fValues = this.currentFilters();
  const isFiltering = !!(fValues.productId || fValues.typeCode || fValues.city || fValues.minToolWear !== null || fValues.maxToolWear !== null);

  // Aktualizujemy rozmiar w sygnale paginacji, żeby totalPages się zgadzało
  const targetSize = isFiltering ? 10 : 10000;
  if (this.pagination().size !== targetSize) {
    this.pagination.update(p => ({ ...p, size: targetSize }));
  }

  const filters: FilterTurbineForm = {
    productId: fValues.productId,
    typeCode: fValues.typeCode,
    severity: fValues.severity,
    city: fValues.city,
    minToolWear: fValues.minToolWear,
    maxToolWear: fValues.maxToolWear
  };

  // Używamy aktualnego stanu paginacji (zawiera poprawną stronę i rozmiar)
  const pageable = this.pagination();

  this.turbineService.findAllActiveTurbines(filters, pageable).subscribe({
    next: (response) => {
      this.markersLayer.clearLayers();
      this.updateMapMarkers(response.content);
      
      this.turbines.set(response.content);
      this.totalElements.set(response.totalElements);

      this.setupLiveUpdates(response.content);

      if (!this.isInitialLoad && response.content.length > 0 && isFiltering) {
        this.zoomToFit(response.content);
      }
      this.isInitialLoad = false;
    },
    error: (err) => console.error('Błąd wyszukiwania turbin:', err)
  });
}

  changePage(delta: number) {
  const nextPage = this.pagination().page + delta;
  const max = this.totalPages();

  if (nextPage >= 1 && nextPage <= max) {
    this.pagination.update(p => ({ ...p, page: nextPage }));
    this.applyFilters(); 
  }
}

  nextPage() {
    const totalPages = Math.ceil(this.totalElements() / this.pagination().size);
    if (this.pagination().page < totalPages) {
      this.pagination.update(p => ({ ...p, page: p.page + 1 }));
      this.applyFilters();
    }
  }

  // Paginacja: Powrót
  prevPage() {
    if (this.pagination().page > 1) {
      this.pagination.update(p => ({ ...p, page: p.page - 1 }));
      this.applyFilters();
    }
  }

clearFilters() {
  this.filterForm.reset({
    productId: '',
    typeCode: '',
    severity: '',
    city: '',
    minToolWear: null,
    maxToolWear: null
  });

  this.currentFilters.set(this.filterForm.value);
  this.pagination.update(p => ({ ...p, page: 1, size: 10000 }));

  if (this.map) {
    this.map.stop();
    this.map.flyTo(this.INITIAL_CENTER, this.INITIAL_ZOOM);
  }

  this.applyFilters();
}

pageNumbers = computed(() => {
  const pages = [];
  for (let i = 1; i <= this.totalPages(); i++) {
    pages.push(i);
  }
  return pages;
});

// Metoda do skoku bezpośrednio do konkretnej strony
goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages()) {
    this.pagination.update(p => ({ ...p, page: page }));
    this.applyFilters();
  }
}

onWeatherUpdate(weatherData: GlobalWeatherForm) {
  // Teraz weatherData ma pola windSpeed i airTemp
  console.log('Zaktualizowano pogodę:', weatherData);

  this.envService.updateWeather(weatherData).subscribe({
    next: () => {
      this.snackBar.open('Warunki pogodowe zostały zaktualizowane', 'OK', {
        panelClass: ['success-snackbar']
      });
      this.applyFilters(); // Odśwież mapę, by uwzględnić nowe warunki
    },
    error: () => {
      this.snackBar.open('Błąd aktualizacji pogody', 'Zamknij', {
        panelClass: ['error-snackbar']
      });
    }
  });
}

openAddTurbineModal(currentValues?: any) {
  const dialogRef = this.dialog.open(AddTurbineModalComponent, {
    width: '450px',
    data: currentValues // Przekazujemy to, co już było wpisane
  });

  dialogRef.afterClosed().subscribe(result => {
    if (!result) return;

    if (result.pickFromMap) {
      // Wchodzimy w tryb klikania na mapę
      this.map.once('click', (e: any) => {
        const coords = { 
          ...result.payload, 
          latitude: e.latlng.lat, 
          longitude: e.latlng.lng 
        };
        this.openAddTurbineModal(coords); // Ponownie otwieramy z nowymi współrzędnymi
      });
    } else if (result.save) {
      this.sendToBackend(result.payload);
    }
  });
}

 sendToBackend(createForm: CreateTurbineForm) {
  // Wywołujemy serwis (założenie: turbineService jest wstrzyknięty)
  this.turbineService.createTurbine(createForm).subscribe({
    next: (response) => {
      // 1. Wyświetlamy powiadomienie
      this.snackBar.open('Turbina została pomyślnie dodana!', 'OK', {
       duration: 3000,
      horizontalPosition: 'right', // Możesz dać w prawym rogu jak toast
      verticalPosition: 'top',
      panelClass: ['success-snackbar']// W SCSS możesz nadać mu zielony kolor
    });

      // 2. Resetujemy paginację do 1 strony, aby zobaczyć nową turbinę (jeśli pasuje do filtrów)
      this.pagination.update(p => ({ ...p, page: 1 }));

      // 3. Odświeżamy dane (ponownie pobieramy listę i markery)
      this.applyFilters();
    },
    error: (err) => {
      console.error('Błąd podczas dodawania turbiny:', err);
      this.snackBar.open('Błąd serwera. Nie udało się dodać turbiny.', 'Zamknij', {
        duration: 5000,
            horizontalPosition: 'right', // Możesz dać w prawym rogu jak toast
            verticalPosition: 'top',
            panelClass: ['error-snackbar']
      });
    }
  });
}

private zoomToFit(turbines: TurbineView[]): void {
  if (turbines.length === 0 || !this.map) return;

  // Tworzymy granice (bounds) na podstawie współrzędnych wszystkich turbin
  const bounds = L.latLngBounds(turbines.map(t => [t.latitude, t.longitude]));

  // Przesuwamy widok mapy, aby pasował do granic z małym paddingiem
  this.map.fitBounds(bounds, {
    padding: [70, 70],
    maxZoom: 12
  });
}

zoomToTurbine(turbine: TurbineView) {
  if (turbine.latitude && turbine.longitude) {
    const coords: L.LatLngExpression = [turbine.latitude, turbine.longitude];

    // 1. Centrowanie mapy z animacją (flyTo jest bardzo efektowne)
    this.map.flyTo(coords, 16, {
      duration: 1.5, // czas trwania w sekundach
      easeLinearity: 0.25
    });

    // 2. Automatyczne otwarcie popupa
    // Musimy znaleźć marker wewnątrz markersLayer (markerClusterGroup)
    this.markersLayer.eachLayer((layer: any) => {
      if (layer instanceof L.Marker) {
        const markerCoords = layer.getLatLng();
        if (markerCoords.lat === turbine.latitude && markerCoords.lng === turbine.longitude) {
          // Leaflet MarkerCluster wymaga, aby najpierw "rozbić" klaster, jeśli marker jest ukryty
          this.markersLayer.zoomToShowLayer(layer, () => {
            layer.openPopup();
          });
        }
      }
    });
  }
}

private updateSingleMarker(liveUpdate: TurbineLiveViewDto) {
  const marker = this.markersMap.get(liveUpdate.productId);
  
  if (marker) {
    // 1. Aktualizacja ikony (nowy kolor i prędkość obrotów)
    const newIcon = this.createTurbineIcon(liveUpdate.mainSeverity);
    marker.setIcon(newIcon);

    // 2. Aktualizacja danych w Popupie
    // Pobieramy instancję komponentu z popupa (jeśli jest otwarty)
    const popup = marker.getPopup();
    if (popup && marker.isPopupOpen()) {
       // Tutaj najprościej jest odświeżyć treść popupa nowym DTO
       // lub pozwolić Angularowi obsłużyć to przez sygnały w TurbineCardComponent
    }

    console.log(`Marker ${liveUpdate.productId} zaktualizowany płynnie.`);
  }
}

private setupLiveUpdates(turbines: TurbineView[]) {
  this.turbineSubs.forEach(sub => sub.unsubscribe());
  this.turbineSubs.clear();

  turbines.forEach(t => {
    const sub = this.wsService.subscribeToAlerts(t.productId).subscribe(liveUpdate => {
      
      // 1. Aktualizujemy tylko jeden marker na mapie (SZYBKIE)
      this.updateSingleMarker(liveUpdate.aiData);

      // 2. Aktualizujemy stan w sygnale (dla reszty UI, np. paska bocznego)
      this.turbines.update(currentList => 
        currentList.map(item => 
          item.productId === liveUpdate.aiData.productId 
            ? TurbineMapper.toView(liveUpdate.aiData, item) 
            : item
        )
      );
    });
    this.turbineSubs.set(t.productId, sub);
  });
}

 ngOnDestroy(): void {
    this.turbineSubs.forEach(sub => sub.unsubscribe());
    this.turbineSubs.clear();
    this.markersMap.clear();
  }

}
