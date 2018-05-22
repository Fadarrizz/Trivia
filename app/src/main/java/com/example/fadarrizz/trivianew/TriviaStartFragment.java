package com.example.fadarrizz.trivianew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fadarrizz.trivianew.Common.Common;
import com.example.fadarrizz.trivianew.Interface.VolleyCallback;
import com.example.fadarrizz.trivianew.Model.Question;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TriviaStartFragment extends Fragment {
    private static final String TAG = "TriviaStartFragment";

    Context thisContext;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    FirebaseDatabase database;
    DatabaseReference users;

    View mFragment;
    Button btnStart;
    Button signOutButton;

    RequestQueue requestQueue;

    public static TriviaStartFragment newInstance() {
        TriviaStartFragment triviaStartFragment = new TriviaStartFragment();
        return triviaStartFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity().getApplicationContext();

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
    }

    @Override
    public void onStart() {
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign Out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(thisContext, gso);

        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_trivia_start, container, false);

        btnStart = mFragment.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getQuestionsTask().execute("http://jservice.io/api/random?count=4");
            }
        });

        signOutButton = mFragment.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        return mFragment;
    }

    public class getQuestionsTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // Clear questionList at start
            if(Common.questionList.size() > 0) {
                Common.questionList.clear();
            }

            int TOTAL_QUESTIONS = 15;

            try {
                for (int i = 0; i < TOTAL_QUESTIONS; i++) {
                    JSONRequest(params[0]);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pDialog.dismiss();
            Intent intent = new Intent(getActivity(), playTrivia.class);
            startActivity(intent);
        }
    }

    public void JSONRequest(String url) {
        requestQueue = Volley.newRequestQueue(thisContext);

        final JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Question question = new Question();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Set first question as main question with answer
                        if (i == 0) {
                            question.setQuestion(jsonObject.getString("question"));
                            String correctAnswer = jsonObject.getString("answer");
                            question.setCorrectAnswer(correctAnswer);
                            question.setAnswerA(correctAnswer);
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
                        Toast.makeText(thisContext, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                // Add question to list
                Common.questionList.add(question);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(thisContext, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Log.d("TAG", "Request added");
        requestQueue.add(request);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();

        // Start MainActivity
        Intent intent = new Intent(thisContext, MainActivity.class);
        startActivity(intent);
    }
}
