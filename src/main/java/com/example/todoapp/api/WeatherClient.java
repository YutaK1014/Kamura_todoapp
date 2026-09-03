package com.example.todoapp.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherClient {

    private static final String WEATHER_API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=35.68&longitude=139.76&current=temperature_2m,weather_code";

    private final RestClient restClient;
    private LocalDate cachedDate;
    private WeatherResult cachedResult;

    public WeatherClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public synchronized WeatherResult getWeather() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        if (today.equals(cachedDate)) {
            return cachedResult;
        }

        WeatherResult result;
        try {
            Map<String, Object> data = restClient.get()
                    .uri(WEATHER_API_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            if (data == null || !(data.get("current") instanceof Map<?, ?> current)
                    || !(current.get("temperature_2m") instanceof Number temperature)
                    || !(current.get("weather_code") instanceof Number weatherCode)) {
                result = new WeatherResult(null, null, true);
            } else {
                result = new WeatherResult(temperature.doubleValue(), weatherCode.intValue(), false);
            }
        } catch (RuntimeException e) {
            result = new WeatherResult(null, null, true);
        }
        cachedDate = today;
        cachedResult = result;
        return result;
    }

    public record WeatherResult(Double temperature, Integer weatherCode, boolean unavailable) {
    }
}
