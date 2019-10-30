package com.example.learningprogram.dataprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learningprogram.models.Lecture;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Поставщик данных об учебной программе
 *
 * @author Evgeny Chumak
 **/
public class LearningProgramProvider {
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final String LECTURES_URL = "http://landsovet.ru/learning_program.json";

    private List<Lecture> mLectures;

    /**
     * Возвращает все лекции курса
     */
    @Nullable
    public List<Lecture> getLectures() {
        return mLectures == null ? null : new ArrayList<>(mLectures);
    }

    /**
     * Возвращает список лекторов курса
     */
    public List<String> provideLectors() {
        Set<String> lectorsSet = new HashSet<>();
        for (Lecture lecture : mLectures) {
            lectorsSet.add(lecture.getLector());
        }
        return new ArrayList<>(lectorsSet);
    }

    /**
     * Фильтрует список лекций по имени лектора
     *
     * @param lectorName по кому фильтровать
     */
    public List<Lecture> filterBy(String lectorName) {
        List<Lecture> result = new ArrayList<>();
        for (Lecture lecture : mLectures) {
            if (lecture.getLector().equals(lectorName)) {
                result.add(lecture);
            }
        }
        return result;
    }

    /**
     * Возвращает лекцию, следующую за переданной датой. Если передана дата позже, чем последняя лекция,
     * будет возвращена последняя лекция.
     */
    @NonNull
    public Lecture getLectureNextTo(@NonNull List<Lecture> lectures, @NonNull Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
        for (Lecture lecture : lectures) {
            try {
                Date lectureDate = format.parse(lecture.getDate());
                if (lectureDate != null && lectureDate.after(date)) {
                    return lecture;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return lectures.get(lectures.size() - 1);
    }

    @Nullable
    public List<Lecture> loadLecturesFromWeb() {
        if (mLectures != null) {
            return mLectures;
        }
        InputStream is = null;
        try {
            final URL url = new URL(LECTURES_URL);
            URLConnection connection = url.openConnection();
            is = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            Lecture[] lectures = mapper.readValue(is, Lecture[].class);
            mLectures = Arrays.asList(lectures);
            return new ArrayList<>(mLectures);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
