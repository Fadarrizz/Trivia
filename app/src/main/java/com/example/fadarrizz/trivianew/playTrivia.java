package com.example.fadarrizz.trivianew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fadarrizz.trivianew.Helpers.Helpers;
import com.example.fadarrizz.trivianew.Interface.VolleyCallback;
import com.example.fadarrizz.trivianew.Model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class playTrivia extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "playTrivia";

    // Interval of 1 sec
    final static long INTERVAL = 1000;
    // 30 sec answer time
    final static long TIMEOUT = 30000;

    int progressValue = 0;

    CountDownTimer countDownTimer;

    int index=0, score=0, thisQuestion=0, correctAnswer;

    ProgressBar progressBar;
    Button btnA, btnB, btnC, btnD;
    TextView textViewScore, textViewQuestionsCount, textViewQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_trivia);

        textViewScore = findViewById(R.id.textViewScore);
        textViewQuestionsCount = findViewById(R.id.textViewQuestionCount);
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
        Button clickedButton = (Button) v;
        // Increase score when correct answer is clicked
        if (clickedButton.getText().equals(Helpers.currentQuestion.getCorrectAnswer())) {
            score += 10;
            correctAnswer++;
            getQuestion(++index);
        }
        // Go to gameOver if wrong answer is clicked
        else {
            gameOver(score, correctAnswer);
        }
        // Update score
        textViewScore.setText(String.format("Score: %d", score));
    }

    public void getQuestion(final int index) {

        JSONRequest.getResponse(this, "http://jservice.io/api/random?count=4", new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONArray response) {
                Question question = Helpers.currentQuestion;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Set first question as main question with answer
                        if (i == 0) {
                            question.setQuestion(jsonObject.getString("question"));
                            String correctAnswer = jsonObject.getString("answer");
                            question.setCorrectAnswer(correctAnswer);
                            question.setAnswerA(correctAnswer);;
                            // Set answer from second question
                        } else if (i == 1) {
                            question.setAnswerB(jsonObject.getString("answer"));
                            // Set answer from third question
                        } else if (i == 2) {
                            question.setAnswerC(jsonObject.getString("answer"));
                            // Set answer from fourth question
                        } else if (i == 3) {
                            question.setAnswerD(jsonObject.getString("answer"));
                        }
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                showNextQuestion(index);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Reset values and set new question
    private void showNextQuestion(int index) {
        thisQuestion++;
        textViewQuestionsCount.setText(String.format("Question: %d", thisQuestion));
        progressBar.setProgress(0);
        progressValue = 0;

        textViewQuestion.setText(Helpers.currentQuestion.getQuestion());

        ArrayList answerList = new ArrayList();
        answerList.add(Helpers.currentQuestion.getAnswerA());
        answerList.add(Helpers.currentQuestion.getAnswerB());
        answerList.add(Helpers.currentQuestion.getAnswerC());
        answerList.add(Helpers.currentQuestion.getAnswerD());

        randomizeQuestions(answerList);
        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        countDownTimer = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                getQuestion(++index);
            }
        };
        getQuestion(++index);
    }

    // Send data to gameOver activity
    private void gameOver(int score, int correctAnswer) {
        Intent intent = new Intent(this, GameOver.class);
        Bundle dataSend = new Bundle();
        dataSend.putInt("score", score);
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
