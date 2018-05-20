package com.example.fadarrizz.trivianew.Interface;

import android.view.View;

// Interface for implementing onClick on recycler item

public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
