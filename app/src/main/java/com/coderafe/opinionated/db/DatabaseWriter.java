package com.coderafe.opinionated.db;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.coderafe.opinionated.model.Answer;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.ChoiceInstance;
import com.coderafe.opinionated.model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class to write information to the firebase database
 */
public class DatabaseWriter {

    private final String ANSWER_TABLE="answers";
    private final String USER_ID_CHILD="userId";
    private final String CHOICE_INSTANCE_ID="choiceInstance";
    private final String QUESTIONS_ANSWERED_CHILD="questionsAnswered";
    private final String CHOICE_INSTANCE_TABLE="choiceInstances";
    private final String QUESTION_ID_CHILD="questionId";
    private final String CHOICE_ID_CHILD="choiceId";
    private final String USER_TABLE="users";

    private final String CHOICE_INSTANCE_ID_FIND_TAG="CHOICE_INSTANCE_FIND";

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;


    public DatabaseWriter(FirebaseUser firebaseUser) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void submitAnswer(String choiceInstanceId) {
        //TODO: Create an answer model and push to firebase

        Map<String, String> post = new HashMap<String,String>();
        post.put(USER_ID_CHILD, mFirebaseUser.getUid());
        post.put(CHOICE_INSTANCE_ID, choiceInstanceId);
        mDatabase.child(ANSWER_TABLE).push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            /**
             * Updates that the user has answered this question by increment questionsAnswered field
             * in the firebase database
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String userId = mFirebaseUser.getUid();
                mDatabase.child(USER_TABLE).child(userId).child(QUESTIONS_ANSWERED_CHILD).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        mutableData.setValue((long) mutableData.getValue() + 1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }
        });
    }



}
