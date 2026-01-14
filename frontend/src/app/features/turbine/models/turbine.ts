
export interface TurbineView {
  id: number;
  productId: string;
  typeCode: string;
  osfLimit: number;
  wearIncrement: number;
  ratedPower: number;
  deicingCost: number;
  latitude: number;
  longitude: number;
  city: string;
  currentToolWear: number;
  mainSeverity: string;      // Tu trafi Twój status (np. HDF, HEALTHY)
  statusLabel: string;
  statusDescription: string;
  isActive: boolean;
  lastUpdate: string | Date; // LocalDateTime przychodzi jako string ISO
}

export interface CreateTurbineForm {
  productId: string;
  typeCode: 'L' | 'M' | 'H'; // Typy ograniczone zgodnie z Twoim @Pattern
  latitude: number;
  longitude: number;
  city?: string;
}


export interface UpdateTurbineForm {
  productId: string;
  typeCode: 'L' | 'M' | 'H';
  latitude?: number;
  longitude?: number;
  city?: string;
  currentToolWear?: number;
}


export interface FilterTurbineForm {
  productId?: string;
  typeCode?: string;
  severity?: string;
  city?: string;
  minToolWear?: number | null;
  maxToolWear?: number | null;
}

export interface TurbineDashboardUpdate {
  aiData: TurbineLiveViewDto;
  measurements: MeasurementLiveViewDto;
}

export interface TurbineLiveViewDto {
  turbineId: number;
  measurementId: number;
  productId: string;
  turbineType: string;
  city: string;
  mainSeverity: string;
  targetLabel: string;
  predictionAi: string;
  aiDescription: string;
  lastUpdate: string | Date;
  latitude: number;
  longitude: number;
  isActive: boolean;
  isAnomaly: boolean;
}

export interface MeasurementLiveViewDto {
  windSpeedKmH: number;
  airTempCelsius: number;
  rpm: number;
  torque: number;
  powerGenerated: number;
  powerNetto: number;
  processTempCelsius: number;
  toolWear: number;
  deIcingActive: boolean;
  severity: string;
  targetLabel: number;
}