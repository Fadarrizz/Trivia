package com.example.fadarrizz.trivianew.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.fadarrizz.trivianew.Interface.ItemClickListener;
import com.example.fadarrizz.trivianew.R;

public class RankingViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewName, textViewScore;

    public RankingViewHolder(View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewScore = itemView.findViewById(R.id.textViewScore);
    }

}
