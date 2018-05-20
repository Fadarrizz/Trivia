package com.example.fadarrizz.trivianew;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fadarrizz.trivianew.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class playTrivia extends AppCompatActivity implements View.OnClickListener {

    // Interval of 1 sec
    final static long INTERVAL = 1000;
    // 30 sec answer time
    final static long TIMEOUT = 30000;
    int progressValue = 0;

    CountDownTimer countDownTimer;

    int index=0, score=0, thisQuestion=0, totalQuestion, correctAnswer;

    ProgressBar progressBar;
    Button btnA, btnB, btnC, btnD;
    TextView textViewScore, textViewQuestionsTotal, textViewQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_trivia);

        textViewScore = findViewById(R.id.textViewScore);
        textViewQuestionsTotal = findViewById(R.id.textViewTotalQuestion);
        textViewQuestion = findViewById(R.id.textViewQuestion);

        progressBar = findViewById(R.id.progressBar);

        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        countDownTimer.cancel();
        if (index < totalQuestion) {
            Button clickedButton = (Button) v;
            // Increase score when correct answer is clicked
            if (clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer())) {
                score += 10;
                correctAnswer++;
                showNextQuestion(++index);
            }
            // Go to game over activity if wrong answer is clicked
            else {
                gameOver(score, totalQuestion, correctAnswer);
            }
            // Update score
            textViewScore.setText(String.format("%d", score));
        }
    }

    // Reset values and set new question
    private void showNextQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            textViewQuestionsTotal.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            textViewQuestion.setText(Common.questionList.get(index).getQuestion());

            ArrayList answerList = new ArrayList();
            answerList.add(Common.questionList.get(index).getAnswerA());
            answerList.add(Common.questionList.get(index).getAnswerB());
            answerList.add(Common.questionList.get(index).getAnswerC());
            answerList.add(Common.questionList.get(index).getAnswerD());

            randomizeQuestions(answerList);

            countDownTimer.start();
        }
        // Go to game over activity when last question has been answered
        else {
            gameOver(score, totalQuestion, correctAnswer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        countDownTimer = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                showNextQuestion(++index);
            }
        };
        showNextQuestion(++index);
    }

    // Send data to gameOver activity
    private void gameOver(int score, int totalQuestion, int correctAnswer) {
        Intent intent = new Intent(this, GameOver.class);
        Bundle dataSend = new Bundle();
        dataSend.putInt("score", score);
        dataSend.putInt("total", totalQuestion);
        dataSend.putInt("correct", correctAnswer);
        intent.putExtras(dataSend);
        startActivity(intent);
        finish();
    }

    // Randomize the order of answers
    private void randomizeQuestions(ArrayList answers) {
        Button buttons[] = new Button[4];

        buttons[0] = findViewById(R.id.btnAnswerA);
        buttons[1] = findViewById(R.id.btnAnswerB);
        buttons[2] = findViewById(R.id.btnAnswerC);
        buttons[3] = findViewById(R.id.btnAnswerD);

        Collections.shuffle(answers);

        for (int i = 0; i < answers.size(); i++) {
            buttons[i].setText(answers.get(i).toString());
        }
    }

    // Override back button to go to Home activity
    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(this, Home.class));
        finish();
        return;
    }
}
