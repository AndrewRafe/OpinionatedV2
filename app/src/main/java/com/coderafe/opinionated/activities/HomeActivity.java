package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * The home activity is the screen that the user will be on when they login or re-enter the app
 * after already logging in. From here they can navigate to answer questions and view question
 * results
 */
public class HomeActivity extends AppCompatActivity {

    private DatabaseReader mDatabaseReader;
    private User mUser;

    private TextView mWelcomeTextView;
    private TextView mQuestionsAnsweredTextView;
    private Button mAnswerMoreQuestionsButton;

    private RelativeLayout mMainLayout;
    private ProgressBar mLoadDataProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        establishReadConnection();
        //mUser = mDatabaseReader.getUser();

        mWelcomeTextView = (TextView) findViewById(R.id.home_welcome_tv);
        mQuestionsAnsweredTextView = (TextView) findViewById(R.id.home_questions_answered_tv);
        mAnswerMoreQuestionsButton = (Button) findViewById(R.id.home_answer_questions_btn);
        mMainLayout = (RelativeLayout) findViewById(R.id.home_main_layout);
        mLoadDataProgressBar = (ProgressBar) findViewById(R.id.home_load_data_progress_bar);

        //Dont show the views until data is loaded
        makeViewVisible(false);
        new LoadUserData().execute();

        mAnswerMoreQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AnswerQuestionActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //new LoadUserData().execute();
        //Toast toast = Toast.makeText(this, mUser.getEmail(), Toast.LENGTH_LONG);
        //toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadUserData().execute();
    }


    private void establishReadConnection() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReader = new DatabaseReader(firebaseAuth.getCurrentUser());
    }

    /**
     * Async Task that loads the user data off the main thread to be displayed in this activity
     */
    private class LoadUserData extends AsyncTask<Void, Void, User> {
        @Override
        public User doInBackground(Void... voids) {
            User user = mDatabaseReader.getUser();
            while (user == null) {
                user = mDatabaseReader.getUser();
            }
            return user;
        }

        @Override
        public void onProgressUpdate(Void... voids) {

        }

        @Override
        public void onPostExecute(User user) {
            mUser = user;
            Log.d("ASYNC", "USER DATA LOADED");
            mWelcomeTextView.setText(getString(R.string.home_welcome_message)
                    + " " + mUser.getEmail());

            mQuestionsAnsweredTextView.setText(getString(R.string.home_questions_answered_message_part1)
                    + " " + mUser.getNumQuestionsAnswered() + " "
                    + getString(R.string.home_questions_answered_message_part2));
            makeViewVisible(true);
        }

    }

    /**
     * Helper method to make the activity show the loading bar if the views should not be visible
     * @param isVisible Whether the activity views should be visible or not
     */
    private void makeViewVisible(boolean isVisible) {
        if (isVisible) {
            mQuestionsAnsweredTextView.setVisibility(View.VISIBLE);
            mWelcomeTextView.setVisibility(View.VISIBLE);
            mAnswerMoreQuestionsButton.setVisibility(View.VISIBLE);
            mLoadDataProgressBar.setVisibility(View.GONE);
        } else {
            mQuestionsAnsweredTextView.setVisibility(View.GONE);
            mWelcomeTextView.setVisibility(View.GONE);
            mAnswerMoreQuestionsButton.setVisibility(View.GONE);
            mLoadDataProgressBar.setVisibility(View.VISIBLE);
        }
    }


}
