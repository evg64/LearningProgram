package com.example.learningprogram.models;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Модель лекции
 *
 * @author Evgeny Chumak
 **/
public class Lecture {
    private static final int LECTURES_PER_WEEK = 3;

    private final int mNumber;
    private final String mDate;
    private final String mTheme;
    private final String mLector;
    private final int weekIndex;

    public Lecture(
            int number,
            @NonNull String date,
            @NonNull String theme,
            @NonNull String lector
    ) {
        mNumber = number;
        mDate = date;
        mTheme = theme;
        mLector = lector;
        weekIndex = (mNumber - 1) / LECTURES_PER_WEEK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lecture lecture = (Lecture) o;
        return mNumber == lecture.mNumber &&
                weekIndex == lecture.weekIndex &&
                Objects.equals(mDate, lecture.mDate) &&
                Objects.equals(mTheme, lecture.mTheme) &&
                Objects.equals(mLector, lecture.mLector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mNumber, mDate, mTheme, mLector, weekIndex);
    }

    public int getNumber() {
        return mNumber;
    }

    public String getDate() {
        return mDate;
    }

    public String getTheme() {
        return mTheme;
    }

    public String getLector() {
        return mLector;
    }

    public int getWeekIndex() {
        return weekIndex;
    }
}
