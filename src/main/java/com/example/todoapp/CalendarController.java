package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month, Model model) {
        LocalDate today = LocalDate.now();
        YearMonth targetMonth = YearMonth.of(
                year != null ? year : today.getYear(),
                month != null ? month : today.getMonthValue());

        LocalDate firstDate = targetMonth.atDay(1);
        LocalDate lastDate = targetMonth.atEndOfMonth();
        List<List<LocalDate>> calendarWeeks = createCalendarWeeks(targetMonth, firstDate);

        YearMonth previousMonth = targetMonth.minusMonths(1);
        YearMonth nextMonth = targetMonth.plusMonths(1);

        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("firstDate", firstDate);
        model.addAttribute("lastDate", lastDate);
        model.addAttribute("calendarWeeks", calendarWeeks);
        model.addAttribute("previousMonth", previousMonth);
        model.addAttribute("nextMonth", nextMonth);
        return "calendar";
    }

    private List<List<LocalDate>> createCalendarWeeks(YearMonth targetMonth, LocalDate firstDate) {
        int leadingEmptyCells = firstDate.getDayOfWeek().getValue() % 7;
        int totalCells = leadingEmptyCells + targetMonth.lengthOfMonth();
        int trailingEmptyCells = (7 - totalCells % 7) % 7;

        List<LocalDate> calendarDates = new ArrayList<>();
        for (int i = 0; i < leadingEmptyCells; i++) {
            calendarDates.add(null);
        }
        for (int day = 1; day <= targetMonth.lengthOfMonth(); day++) {
            calendarDates.add(targetMonth.atDay(day));
        }
        for (int i = 0; i < trailingEmptyCells; i++) {
            calendarDates.add(null);
        }
        List<List<LocalDate>> calendarWeeks = new ArrayList<>();
        for (int weekStart = 0; weekStart < calendarDates.size(); weekStart += 7) {
            calendarWeeks.add(calendarDates.subList(weekStart, weekStart + 7));
        }
        return calendarWeeks;
    }
}
