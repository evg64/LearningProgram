package com.example.learningprogram.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер детальной информации о лекции
 *
 * @author Evgeny Chumak
 **/
public class DetailsFragmentSubtopicsAdapter extends RecyclerView.Adapter<DetailsFragmentSubtopicsAdapter.SubtopicHolder> {

    private final List<String> mSubtopics;

    public DetailsFragmentSubtopicsAdapter(@NonNull List<String> subtopics) {
        mSubtopics = new ArrayList<>(subtopics);
    }

    @NonNull
    @Override
    public SubtopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SubtopicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtopicHolder holder, int position) {
        holder.mName.setText(mSubtopics.get(position));
    }

    @Override
    public int getItemCount() {
        return mSubtopics.size();
    }

    static class SubtopicHolder extends RecyclerView.ViewHolder {

        private final TextView mName;

        SubtopicHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(android.R.id.text1);
        }
    }
}
