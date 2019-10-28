package com.example.learningprogram;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningprogram.adapters.DisplayModeSpinnerAdapter;
import com.example.learningprogram.adapters.LectorSpinnerAdapter;
import com.example.learningprogram.adapters.LecturesAdapter;
import com.example.learningprogram.dataprovider.LearningProgramProvider;
import com.example.learningprogram.models.DisplayMode;
import com.example.learningprogram.models.Lecture;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Основной экран со списком лекций
 */
public class MainActivity extends AppCompatActivity {

    private static final int POSITION_ALL = 0;

    private LearningProgramProvider mLearningProgramProvider = new LearningProgramProvider();
    private LecturesAdapter mLecturesAdapter;
    private View mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingView = findViewById(R.id.loading_view);
        new LoadLecturesTask(this, savedInstanceState == null).execute();
    }

    private void initRecyclerView(boolean isFirstCreate) {
        RecyclerView recyclerView = findViewById(R.id.learning_program_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mLecturesAdapter = new LecturesAdapter(getResources());
        mLecturesAdapter.setLectures(mLearningProgramProvider.provideLectures());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mLecturesAdapter);
        if (isFirstCreate) {
            Lecture nextLecture = mLearningProgramProvider.getLectureNextTo(new Date());
            int positionOfNextLecture = mLecturesAdapter.getPositionOf(nextLecture);
            if (positionOfNextLecture != -1) {
                recyclerView.scrollToPosition(positionOfNextLecture);
            }
        }
    }

    private void initLectorsSpinner() {
        Spinner spinner = findViewById(R.id.lectors_spinner);
        final List<String> spinnerItems = mLearningProgramProvider.provideLectors();
        Collections.sort(spinnerItems);
        spinnerItems.add(POSITION_ALL, getResources().getString(R.string.all));
        LectorSpinnerAdapter adapter = new LectorSpinnerAdapter(spinnerItems);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final List<Lecture> lectures = position == POSITION_ALL ?
                        mLearningProgramProvider.provideLectures() :
                        mLearningProgramProvider.filterBy(spinnerItems.get(position));
                mLecturesAdapter.setLectures(lectures);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initDisplayModeSpinner() {
        Spinner spinner = findViewById(R.id.display_mode_spinner);
        spinner.setAdapter(new DisplayModeSpinnerAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DisplayMode selectedDisplayMode = DisplayMode.values()[position];
                mLecturesAdapter.setDisplayMode(selectedDisplayMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private static class LoadLecturesTask extends AsyncTask<Void, Void, List<Lecture>> {
        private final WeakReference<MainActivity> mActivityRef;
        private final LearningProgramProvider mProvider;
        private final boolean mIsFirstCreate;

        private LoadLecturesTask(@NonNull MainActivity activity, boolean isFirstCreate) {
            mActivityRef = new WeakReference<>(activity);
            mProvider = activity.mLearningProgramProvider;
            mIsFirstCreate = isFirstCreate;
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = mActivityRef.get();
            if (activity != null) {
                activity.mLoadingView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Lecture> doInBackground(Void... arg) {
            return mProvider.loadLecturesFromWeb();
        }

        @Override
        protected void onPostExecute(List<Lecture> lectures) {
            MainActivity activity = mActivityRef.get();
            activity.mLoadingView.setVisibility(View.GONE);
            if (lectures == null) {
                Toast.makeText(activity, R.string.failed_to_load_lectures, Toast.LENGTH_SHORT).show();
            } else {
                activity.initRecyclerView(mIsFirstCreate);
                activity.initLectorsSpinner();
                activity.initDisplayModeSpinner();
            }
        }
    }
}
