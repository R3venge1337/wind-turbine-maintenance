package pl.zimnya.wind_turbine_maintenance.common;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic windMeasurementsTopic() {
        return TopicBuilder.name("wind-measurements")
                .partitions(3)    // Lepiej dla wydajności
                .replicas(1)      // Jeśli masz jeden broker
                .build();
    }
}
