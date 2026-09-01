package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HolidayClient {

    private static final String HOLIDAY_API_URL = "https://holidays-jp.github.invalid/api/v1/date.json";

    private final RestClient restClient;

    public HolidayClient() {
        this.restClient = RestClient.create();
    }

    public Map<String, String> getHolidays(LocalDate from, LocalDate to) {
        Map<String, String> holidays = restClient.get()
                .uri(HOLIDAY_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {
                });

        if (from == null && to == null) {
            return holidays;
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
        return filteredHolidays;
    }
}
