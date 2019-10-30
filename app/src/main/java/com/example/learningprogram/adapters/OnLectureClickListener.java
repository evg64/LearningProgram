package com.example.learningprogram.adapters;

import androidx.annotation.NonNull;

import com.example.learningprogram.models.Lecture;

/**
 * Обработчик нажатия на элемент списка лекций
 *
 * @author Evgeny Chumak
 **/
public interface OnLectureClickListener {

    /**
     * Обрабатывает нажатие на элемент списка с переданной лекцией
     *
     * @param lecture на элемент списка с какой лекцией нажали
     */
    void onItemClick(@NonNull Lecture lecture);
}
