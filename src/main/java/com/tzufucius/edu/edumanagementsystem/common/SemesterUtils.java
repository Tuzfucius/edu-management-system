package com.tzufucius.edu.edumanagementsystem.common;

import java.time.LocalDate;

public final class SemesterUtils {
    private SemesterUtils() {
    }

    public static String currentSemester() {
        return semesterOf(LocalDate.now());
    }

    public static String semesterOf(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        if (month >= 9) {
            return year + "-" + (year + 1) + "-1";
        }
        if (month == 1) {
            return (year - 1) + "-" + year + "-1";
        }
        return (year - 1) + "-" + year + "-2";
    }
}
