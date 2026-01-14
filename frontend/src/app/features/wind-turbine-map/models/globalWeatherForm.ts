export interface GlobalWeatherForm {
  windSpeed: number; // Odpowiada @DecimalMin(0.0) / @DecimalMax(150.0)
  airTemp: number;   // Odpowiada @DecimalMin(-30.0) / @DecimalMax(50.0) 
}