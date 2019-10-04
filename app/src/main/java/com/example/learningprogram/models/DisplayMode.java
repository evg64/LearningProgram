package com.example.learningprogram.models;

import androidx.annotation.StringRes;

import com.example.learningprogram.R;

/**
 * Режимы отображения списка лекций
 *
 * @author Evgeny Chumak
 **/
public enum DisplayMode {
    /**
     * Без группировки - элементы отображаются один за одним
     */
    UNGROUPED(R.string.ungrouped),
    /**
     * С группировкой по неделям - перед лекциями соответствующей недели появляется
     * ещё 1 элемент списка, в котором указан номер недели
     */
    GROUP_BY_WEEK(R.string.group_by_week);

    @StringRes
    private final int mTitleStringResourceId;

    DisplayMode(@StringRes int titleStringResourceId) {
        mTitleStringResourceId = titleStringResourceId;
    }

    /**
     * Каким строковым ресурсом можно отобразить пользователю соответствующую константу {@link DisplayMode}
     */
    @StringRes
    public int getTitleStringResourceId() {
        return mTitleStringResourceId;
    }
}
