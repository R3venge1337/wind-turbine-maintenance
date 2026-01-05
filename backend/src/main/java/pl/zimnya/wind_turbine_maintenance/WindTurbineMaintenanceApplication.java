package pl.zimnya.wind_turbine_maintenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WindTurbineMaintenanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WindTurbineMaintenanceApplication.class, args);
	}

}
