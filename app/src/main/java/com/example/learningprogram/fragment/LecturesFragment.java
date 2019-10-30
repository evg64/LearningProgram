package com.example.learningprogram.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningprogram.R;
import com.example.learningprogram.adapters.DisplayModeSpinnerAdapter;
import com.example.learningprogram.adapters.LectorSpinnerAdapter;
import com.example.learningprogram.adapters.LecturesAdapter;
import com.example.learningprogram.adapters.OnLectureClickListener;
import com.example.learningprogram.dataprovider.LearningProgramProvider;
import com.example.learningprogram.models.DisplayMode;
import com.example.learningprogram.models.Lecture;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Фрагмент со списком лекций
 *
 * @author Evgeny Chumak
 **/
public class LecturesFragment extends Fragment {

    private static final int POSITION_ALL = 0;

    private LearningProgramProvider mLearningProgramProvider = new LearningProgramProvider();
    private LecturesAdapter mLecturesAdapter;
    private View mLoadingView;
    private RecyclerView mRecyclerView;
    private Spinner mLectorsSpinner;
    private Spinner mDisplayModeSpinner;
    private OnLectureClickListener mOnLectureClickListener = (lecture) ->
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, DetailsFragment.newInstance(lecture))
                    .addToBackStack(DetailsFragment.class.getSimpleName())
                    .commit();

    public static Fragment newInstance() {
        return new LecturesFragment();
    }

    {
        // нужно для того, чтобы инстанс LecturesProvider не убивался после смены конфигурации
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lectures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingView = view.findViewById(R.id.loading_view);
        mRecyclerView = view.findViewById(R.id.learning_program_recycler);
        mLectorsSpinner = view.findViewById(R.id.lectors_spinner);
        mDisplayModeSpinner = view.findViewById(R.id.display_mode_spinner);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Lecture> lectures = mLearningProgramProvider.getLectures();
        if (lectures == null) {
            new LoadLecturesTask(this, savedInstanceState == null).execute();
        } else {
            initRecyclerView(savedInstanceState == null, lectures);
            initLectorsSpinner();
            initDisplayModeSpinner();
        }
    }

    private void initRecyclerView(boolean isFirstCreate, @NonNull List<Lecture> lectures) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mLecturesAdapter = new LecturesAdapter(getResources());
        mLecturesAdapter.setLectures(lectures);
        mLecturesAdapter.setClickListener(mOnLectureClickListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mLecturesAdapter);
        if (isFirstCreate) {
            Lecture nextLecture = mLearningProgramProvider.getLectureNextTo(lectures, new Date());
            int positionOfNextLecture = mLecturesAdapter.getPositionOf(nextLecture);
            if (positionOfNextLecture != -1) {
                mRecyclerView.scrollToPosition(positionOfNextLecture);
            }
        }
    }

    private void initLectorsSpinner() {
        final List<String> spinnerItems = mLearningProgramProvider.provideLectors();
        Collections.sort(spinnerItems);
        spinnerItems.add(POSITION_ALL, getResources().getString(R.string.all));
        LectorSpinnerAdapter adapter = new LectorSpinnerAdapter(spinnerItems);
        mLectorsSpinner.setAdapter(adapter);

        mLectorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final List<Lecture> lectures = position == POSITION_ALL ?
                        mLearningProgramProvider.getLectures() :
                        mLearningProgramProvider.filterBy(spinnerItems.get(position));
                mLecturesAdapter.setLectures(lectures);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initDisplayModeSpinner() {
        mDisplayModeSpinner.setAdapter(new DisplayModeSpinnerAdapter());
        mDisplayModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        private final WeakReference<LecturesFragment> mFragmentRef;
        private final LearningProgramProvider mProvider;
        private final boolean mIsFirstCreate;

        private LoadLecturesTask(@NonNull LecturesFragment fragment, boolean isFirstCreate) {
            mFragmentRef = new WeakReference<>(fragment);
            mProvider = fragment.mLearningProgramProvider;
            mIsFirstCreate = isFirstCreate;
        }

        @Override
        protected void onPreExecute() {
            LecturesFragment fragment = mFragmentRef.get();
            if (fragment != null) {
                fragment.mLoadingView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Lecture> doInBackground(Void... arg) {
            return mProvider.loadLecturesFromWeb();
        }

        @Override
        protected void onPostExecute(List<Lecture> lectures) {
            LecturesFragment fragment = mFragmentRef.get();
            if (fragment == null) {
                return;
            }
            fragment.mLoadingView.setVisibility(View.GONE);
            if (lectures == null) {
                Toast.makeText(fragment.requireContext(), R.string.failed_to_load_lectures, Toast.LENGTH_SHORT).show();
            } else {
                fragment.initRecyclerView(mIsFirstCreate, lectures);
                fragment.initLectorsSpinner();
                fragment.initDisplayModeSpinner();
            }
        }
    }
}
