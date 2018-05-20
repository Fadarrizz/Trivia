package com.example.fadarrizz.trivianew;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fadarrizz.trivianew.Common.Common;
import com.example.fadarrizz.trivianew.Interface.RankingCallback;
import com.example.fadarrizz.trivianew.Model.QuestionScore;
import com.example.fadarrizz.trivianew.Model.Ranking;
import com.example.fadarrizz.trivianew.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment {

    View mFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore, rankingTable;

    int sum = 0;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("QuestionScore");
        rankingTable = database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingList = mFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());

        // Reverse recycler data because of Firebase's ascending OrderByChild method
        rankingList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        // Update score to ranking table
        updateScore(Common.currentUser.getUserName(), new RankingCallback<Ranking>() {
            @Override
            public void Callback(Ranking Ranking) {
                rankingTable.child(Ranking.getUserName()).setValue(Ranking);
                showRanking();
            }
        });

        // Set adapter
        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class, R.layout.layout_ranking,
                RankingViewHolder.class, rankingTable.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, Ranking model, int position) {
                viewHolder.textViewName.setText(model.getUserName());
                viewHolder.textViewScore.setText(String.valueOf(model.getScore()));

            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);

        return mFragment;
    }

    private void showRanking() {
        rankingTable.orderByChild("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Ranking local = data.getValue(Ranking.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateScore(final String userName, final RankingCallback<Ranking> Callback) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            QuestionScore questionScore = data.getValue(QuestionScore.class);
                            sum += Integer.parseInt(questionScore.getScore());
                        }
                        Ranking ranking = new Ranking(userName,sum);
                        Callback.Callback(ranking);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
