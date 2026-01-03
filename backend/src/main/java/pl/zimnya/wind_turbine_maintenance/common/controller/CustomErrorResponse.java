package pl.zimnya.wind_turbine_maintenance.common.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record CustomErrorResponse(HttpStatus status, String errorMessage, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
LocalDateTime timestamp) {
}
