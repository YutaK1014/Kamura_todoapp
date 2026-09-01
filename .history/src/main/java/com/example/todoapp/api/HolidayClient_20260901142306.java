package com.example.todoapp.api;

import java.time.LocalDate;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HolidayClient {

    private static final String HOLIDAY_API_URL = "https://holidays-jp.github.io/api/v1/date.json";

    private final RestClient restClient;

    public HolidayClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public HolidayResult getHolidays(LocalDate from, LocalDate to) {
        Map<String, String> holidays;
        try {
            holidays = restClient.get()
                    .uri(HOLIDAY_API_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {
                    });
        } catch (RestClientException e) {
            return new HolidayResult(Map.of(), true);
        }

        if (holidays == null) {
            holidays = Map.of();
        }

        if (from == null && to == null) {
            return new HolidayResult(holidays, false);
        }

        Map<String, String> filteredHolidays = new LinkedHashMap<>();
        holidays.forEach((dateText, name) -> {
            LocalDate date = LocalDate.parse(dateText);
            boolean afterFrom = from == null || !date.isBefore(from);
            boolean beforeTo = to == null || !date.isAfter(to);
            if (afterFrom && beforeTo) {
                filteredHolidays.put(dateText, name);
            }
        });
        return new HolidayResult(filteredHolidays, false);
    }

    public record HolidayResult(Map<String, String> holidays, boolean unavailable) {
    }
}
