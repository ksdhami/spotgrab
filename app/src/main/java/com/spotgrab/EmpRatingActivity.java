package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.spotgrab.models.Shift;
import com.spotgrab.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmpRatingActivity extends AppCompatActivity {

    private static final String TAG = "EmpRatingActivity";
    private Context mContext;

    private float rate;

    private Button strt_btn, finish_btn, done_btn;
    private TextView startTime, endTime;

    private Date currentTime;
    private SimpleDateFormat dateFormat;
    private String currStartTime, currEndTime;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    DocumentReference userRef;

    public RatingBar ratingBar;

    private String shiftId;
    private String currentUserId, spotterUserId;
    private String shiftStartSet, shiftEndSet;
    private Integer numJobsCompleted, updatedNumJobs;
    private Float currRating, updatedCurrRating;

    private Shift shift;
    private User loggedInUser, spotterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Log.d(TAG, "\nonCreate: started.\n");

        mContext = EmpRatingActivity.this;

        strt_btn = findViewById(R.id.strt_btn);
        finish_btn = findViewById(R.id.finish_btn);
        done_btn = findViewById(R.id.done_btn);

        startTime = findViewById(R.id.startTimeText);
        endTime = findViewById(R.id.endTimeText);

        ratingBar = findViewById(R.id.stars);

        Bundle bundle = getIntent().getExtras();
        shiftId = bundle.getString("Shift ID");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null) {
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");

                    currentUserId = mUser.getUid();

                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    userRef = mFirestore.collection("user").document(mUser.getUid()).collection("shift").document(shiftId);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            shift = task.getResult().toObject(Shift.class);

                            shiftStartSet = shift.getShift_Start_Set();
                            Log.d(TAG, "shift start set: "+shiftStartSet);

                            shiftEndSet = shift.getShift_End_Set();
                            Log.d(TAG, "shift end set: "+shiftEndSet);

                            spotterUserId = shift.getShift_Spotter_Uid();
                            Log.d(TAG, "spotter id: "+spotterUserId);

                            startTime.setText(shift.getShift_Start_Set());
                            endTime.setText(shift.getShift_End_Set());

                        }
                    });
                } else {
                    Log.d(TAG, "\nonAuthStateChanged:signed_out:\n");
                }
            }
        };


        //if ((shiftStartSet.equals("null")) && (shiftEndSet.equals("null"))) {
            strt_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if ((shiftStartSet == null) && (shiftEndSet == null)) {
                        Toast toast = Toast.makeText(mContext, "Shift Started", Toast.LENGTH_SHORT);
                        View view = toast.getView();
                        view.setBackgroundColor(Color.parseColor("#36454f"));
                        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                        toast.show();

                        currentTime = Calendar.getInstance().getTime();
                        dateFormat = new SimpleDateFormat("hh:mm");
                        currStartTime = dateFormat.format(currentTime);
                        startTime.setText(currStartTime);
                        Log.d(TAG, startTime.getText().toString());

                        mFirestore = FirebaseFirestore.getInstance();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        mFirestore.setFirestoreSettings(settings);

                        userRef = mFirestore.collection("user").document(currentUserId).collection("shift").document(shiftId);
                        userRef.update("shift_Start_Set", currStartTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "shift start time updated to " + currStartTime);
                            }
                        });

                        refresh();

                    }
                }
            });


        //}




        //if ((shiftStartSet.equals(startTime.toString())) && (shiftEndSet == null)) {
            finish_btn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if ((shiftStartSet != null) && (shiftEndSet == null)) {
                        Toast toast = Toast.makeText(mContext, "Shift Ended", Toast.LENGTH_SHORT);
                        View view = toast.getView();
                        view.setBackgroundColor(Color.parseColor("#36454f"));
                        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                        toast.show();

                        currentTime = Calendar.getInstance().getTime();
                        dateFormat = new SimpleDateFormat("hh:mm");
                        currEndTime = dateFormat.format(currentTime);
                        endTime.setText(currEndTime);

                        mFirestore = FirebaseFirestore.getInstance();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        mFirestore.setFirestoreSettings(settings);

                        userRef = mFirestore.collection("user").document(currentUserId).collection("shift").document(shiftId);
                        userRef.update("shift_End_Set", currEndTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "shift end time updated to " + currEndTime);
                            }
                        });

                        refresh();
                    }
                }
            });

        //}
//        else {
//            Toast.makeText(EmpRatingActivity.this, "Shift has not started", Toast.LENGTH_SHORT).show();
//        }


            // Can only access rating once finish is clicked
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    //Toast.makeText(EmpRatingActivity.this, "your selected value is: " + rating, Toast.LENGTH_SHORT).show();
                    rate = rating;
                    Log.d(TAG, "onRatingChanged: selected rating: " + rate);
                }
            });


            done_btn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if ((shiftStartSet != null) && (shiftEndSet != null) && (ratingBar.getRating() != 0)) {
                        //Toast.makeText(EmpRatingActivity.this, "DONE" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

                        mFirestore = FirebaseFirestore.getInstance();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        mFirestore.setFirestoreSettings(settings);

                        userRef = mFirestore.collection("user").document(spotterUserId);

                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Done bt onComplete: successfully set the user client." + mUser.getUid());
                                    Log.d(TAG, "Done bt onComplete: employer Id: " + spotterUserId);
                                    spotterUser = task.getResult().toObject(User.class);

                                    numJobsCompleted = spotterUser.getNumJobs();
                                    Log.d(TAG, "Done bt onComplete: number of current jobs completed: " + numJobsCompleted);

                                    updatedNumJobs = numJobsCompleted + 1;
                                    Log.d(TAG, "Done bt onComplete: updated num jobs: " + updatedNumJobs);

                                    userRef.update("numJobs", updatedNumJobs).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "num jobs updated to " + updatedNumJobs);
                                        }
                                    });

                                    currRating = spotterUser.getRating();
                                    Log.d(TAG, "Done bt onComplete: current rating: " + currRating);
                                    if(currRating == null) {
                                        updatedCurrRating = rate;
                                        Log.d(TAG, "Done bt onComplete: updated rating when null: " + updatedCurrRating);
                                    } else {
                                        updatedCurrRating = ((currRating * numJobsCompleted) + rate) / (updatedNumJobs);
                                        Log.d(TAG, "Done bt onComplete: updated rating: " + updatedCurrRating);
                                    }

                                    userRef.update("rating", updatedCurrRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "rating updated to " + updatedCurrRating);
                                        }
                                    });

                                }
                            }
                        });

//                        final Integer updatedNumJobs = numJobsCompleted++;
//                        final Long updatedRating = (long) ((currRating * numJobsCompleted) + rate) / (updatedNumJobs);
//                        userRef.update("rating", updatedRating).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "rating updated to " + updatedRating);
//                            }
//                        });
//                        userRef.update("numJobs", updatedNumJobs).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "num jobs updated to " + updatedNumJobs);
//                            }
//                        });
//
//                        // Delete shift from employer
//                        userRef = mFirestore.collection("user").document(currentUserId).collection("shift").document(shiftId);
//                        userRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot successfully deleted: " + shiftId);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error deleting document: " + shiftId, e);
//                            }
//                        });
                    }

                    Toast toast = Toast.makeText(mContext, "Shift Completed", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#36454f"));
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                    toast.show();

                    deleteShift();

                    Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                    startActivity(intent);

                }
            });

        //}
//        else {
//            Toast.makeText(EmpRatingActivity.this, "Shift has not started/ended or a rating was not given", Toast.LENGTH_SHORT).show();
//        }


    }

    public void deleteShift() {
        Log.d(TAG, "in delete schedule method");

        //userRef = mFirestore.collection("user").document(currentUserId);
        Log.d(TAG, "currentUserId in done bt: "+ currentUserId);

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        mFirestore.collection("user").document(currentUserId)
                .collection("shift").document(shiftId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "shift delete success on completed shift: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "shift delete failed on completed shift: ");
            }
        });

    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

