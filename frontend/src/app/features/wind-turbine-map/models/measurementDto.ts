export interface MeasurementDto {
  measurementId: number;
  turbineId: number;
  productId: string;
  turbineType: string;
  timestamp: string;

  // Dane pogodowe i fizyczne (Environment & Operational)
  windSpeedKmH: number;
  airTempCelsius: number;
  rpm: number;
  torque: number;
  powerGenerated: number;
  powerNetto: number;
  processTempCelsius: number;
  toolWear: number;

  // Flagi diagnostyczne
  deIcingActive: boolean;
  severity: 'HEALTHY' | 'CAUTION' | 'FAILURE' | string;
  targetLabel: string;
  predictionAI: string;
}