package com.example.fadarrizz.trivianew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fadarrizz.trivianew.Common.Common;
import com.example.fadarrizz.trivianew.Model.Question;
import com.example.fadarrizz.trivianew.Model.QuestionScore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOver extends AppCompatActivity {

    private static final String TAG = "GameOver";

    Button btnTryAgain;
    TextView textViewResult, getTextViewResult;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference questionScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("QuestionScore");

        textViewResult = findViewById(R.id.textViewTotalScore);
        getTextViewResult = findViewById(R.id.textViewTotalQuestion);
        progressBar = findViewById(R.id.gameOverProgressBar);
        btnTryAgain = findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        // Receive data from bundle
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            final int score = extra.getInt("score");
            int totalQuestion = extra.getInt("total");
            int correctAnswer = extra.getInt("correct");

            final int highScore;

            textViewResult.setText(String.format("Score: %d", score));
            getTextViewResult.setText(String.format("Correct answered: %d / %d",
                                        correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);


            questionScore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(Common.currentUser.getUserName()).exists()) {
                        // Get current score value
                        QuestionScore currentScoreClass = dataSnapshot.child(Common.currentUser.getUserName())
                                .getValue(QuestionScore.class);
                        // Set score if higher then current score
                        if (score > Integer.parseInt(currentScoreClass.getScore())) {
                            setScore(score);
                        }
                    } else {
                        setScore(score);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });
        }
    }

    public void setScore(int score) {
        questionScore.child(String.format("%s", Common.currentUser.getUserName()))
                .setValue(new QuestionScore(Common.currentUser.getUserName(),
                        String.valueOf(score)));
    }
}
