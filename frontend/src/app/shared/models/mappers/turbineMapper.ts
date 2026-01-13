import { TurbineLiveViewDto, TurbineView } from '../../../features/turbine/models/turbine';

export class TurbineMapper {
  
  /**
   * Mapuje dane statyczne z REST API (TurbineView) na format Live DTO.
   * Używane do inicjalizacji mapy/widoku przed odebraniem danych z WebSocketu.
   */
  public static toLiveView(turbine: TurbineView): TurbineLiveViewDto {
    return {
    turbineId: turbine.id,
      measurementId: 0, // Inicjalnie brak pomiaru
      productId: turbine.productId,
      turbineType: turbine.typeCode,
      city: turbine.city,
      // Mapujemy string z bazy na konkretny Unia Type
      mainSeverity: this.mapSeverity(turbine.mainSeverity),
      targetLabel: turbine.statusLabel || 'N/A',
      predictionAi: 'Oczekiwanie...',
      aiDescription: turbine.statusDescription || 'Brak aktywnych alertów',
      lastUpdate: turbine.lastUpdate,
      latitude: turbine.latitude,
      longitude: turbine.longitude,
      isActive: turbine.isActive,
      isAnomaly: false // Na starcie zakładamy brak anomalii
    };
  }

  public static toView(live: TurbineLiveViewDto, current: TurbineView): TurbineView {
    return {
      ...current, // Zachowujemy pola stałe: id, osfLimit, ratedPower, deicingCost itp.
      productId: live.productId,
      typeCode: live.turbineType,
      city: live.city,
      latitude: live.latitude,
      longitude: live.longitude,
      isActive: live.isActive,
      lastUpdate: live.lastUpdate,
      // Pola dynamiczne z analizy AI:
      mainSeverity: live.mainSeverity, 
      statusLabel: live.predictionAi, // Przechowujemy werdykt AI jako label
      statusDescription: live.aiDescription
    };
  }

  private static mapSeverity(severity: string): 'GOOD' | 'CAUTION' | 'FAILURE' | 'NONE' {
    const s = severity?.toUpperCase();
    if (s === 'GOOD' || s === 'HEALTHY' || s === 'OK') return 'GOOD';
    if (s === 'CAUTION' || s === 'MEDIUM') return 'CAUTION';
    if (s === 'FAILURE' || s === 'HIGH') return 'FAILURE';
    return 'NONE';
  }
}