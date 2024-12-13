package com.finexchange.finexchange.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateUtils {
    public static String getSystemDateAsDay() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return currentDateTime.toLocalDate().format(dateFormatter);
    }

    public static List<String> getLast10WeekdaysIncludingToday() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return IntStream.iterate(0, i -> i + 1)
                .mapToObj(i -> LocalDate.now().minusDays(i))
                .filter(date -> !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY))
                .limit(10)
                .sorted((d1, d2) -> d1.compareTo(d2))
                .map(date -> date.format(dateFormatter))
                .collect(Collectors.toList());
    }

    public static List<String> getLast7WeekdaysIncludingToday() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return IntStream.iterate(0, i -> i + 1)
                .mapToObj(i -> LocalDate.now().minusDays(i))
                .filter(date -> !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY))
                .limit(7)
                .sorted((d1, d2) -> d1.compareTo(d2))
                .map(date -> date.format(dateFormatter))
                .collect(Collectors.toList());
    }

    public static String getPreviousWeekday(String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);

        date = date.minusDays(1);

        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }

        return date.format(dateFormatter);
    }


}
