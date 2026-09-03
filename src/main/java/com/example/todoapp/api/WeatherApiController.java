package com.example.todoapp.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherApiController {

    private final WeatherClient weatherClient;

    public WeatherApiController(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/api/weather")
    public ResponseEntity<Map<String, Object>> weather() {
        WeatherClient.WeatherResult result = weatherClient.getWeather();
        ResponseEntity.BodyBuilder response = ResponseEntity.ok();
        if (result.unavailable()) {
            response.header("X-Weather-Unavailable", "true");
            return response.body(Map.of());
        }
        return response.body(Map.of(
                "city", "東京",
                "temperature", result.temperature(),
                "weatherCode", result.weatherCode(),
                "description", describe(result.weatherCode())));
    }

    private String describe(int code) {
        if (code == 0) return "快晴";
        if (code <= 3) return "晴れ・くもり";
        if (code == 45 || code == 48) return "霧";
        if (code >= 51 && code <= 57) return "霧雨";
        if (code >= 61 && code <= 67) return "雨";
        if (code >= 71 && code <= 77) return "雪";
        if (code >= 80 && code <= 82) return "にわか雨";
        if (code >= 85 && code <= 86) return "にわか雪";
        if (code == 95 || code == 96 || code == 99) return "雷雨";
        return "天気情報";
    }
}
