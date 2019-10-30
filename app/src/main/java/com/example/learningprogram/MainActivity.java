package com.example.learningprogram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningprogram.fragment.LecturesFragment;

/**
 * Активити-контейнер для 2х фрагментов: списка лекций и детальной информации о лекции
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root, LecturesFragment.newInstance())
                    .commit();
        }
    }
}
