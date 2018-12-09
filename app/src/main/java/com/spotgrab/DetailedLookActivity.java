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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.models.Shift;
import com.spotgrab.models.User;
import com.spotgrab.module.GlideApp;

public class DetailedLookActivity extends AppCompatActivity {

    private static final String TAG = "DetailedLook";

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;

    private TextView nameUser, numJobs;
    private Button selected, menuButton;
    private ImageView profilePhoto;
    private RatingBar ratingBar;

    private User candidateUser;
    private Shift shiftCurrent;

    private String currentUserId;
    private String shiftDescription, shiftPay, shiftLocation, shiftTitle;
    private String candidateUid, shiftId, schedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_look);

        Log.d(TAG, "onCreate");

        mContext = DetailedLookActivity.this;

        // get candidate uid from list of candidates for document fields
        Bundle bundle = getIntent().getExtras();
        candidateUid = bundle.getString("uid");
        shiftId = bundle.getString("shift id");
        schedId = bundle.getString("sched id");
        currentUserId = bundle.getString("current user id");
        Log.d(TAG, "candidate user id is: "+candidateUid);
        Log.d(TAG, "candidate sched id is: "+schedId);
        Log.d(TAG, "shift id is: "+shiftId);
        Log.d(TAG, "current employer user id is: "+currentUserId);


        //Toast.makeText(mContext, "candidate user id is:"+candidateUid, Toast.LENGTH_SHORT).show();

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        nameUser= findViewById(R.id.nameUser);
        numJobs= findViewById(R.id.numJobs);


        selected = findViewById(R.id.selected);
        profilePhoto = findViewById(R.id.profilePhoto);

        // Initialize RatingBar
        ratingBar = findViewById(R.id.ratingBar);


        // Get candidate info from database
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        DocumentReference userRef = mFirestore.collection("user").document(candidateUid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully set the user client." + candidateUid);
                    candidateUser = task.getResult().toObject(User.class);
                    nameUser.setText(candidateUser.getfName() + " " + candidateUser.getlName() + " ");
                    numJobs.setText(candidateUser.getNumJobs().toString());
                    if(candidateUser.getRating() == null) {
                        ratingBar.setRating(0);
                    } else {
                        ratingBar.setRating(candidateUser.getRating().intValue());
                    }

                    //Toast.makeText(mContext, user.getAccount(), Toast.LENGTH_SHORT).show();
                    //((UserClient)(getApplicationContext())).setUser(user);
                } else {
                    Log.d(TAG, "onComplete: failed to set the user client.");
                }

                mStorage = FirebaseStorage.getInstance().getReference().child("images/").child(candidateUid);
                GlideApp.with(mContext).load(mStorage).into(profilePhoto);
            }
        });

        selected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Log.d(TAG, "clicked select button");
                //Toast.makeText(mContext, "Add Clicked", Toast.LENGTH_SHORT).show();

                mFirestore = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                mFirestore.setFirestoreSettings(settings);

                // update shift to offer sent = yes; also set spotter uid to selected candidate
//                mAuth = FirebaseAuth.getInstance();
//                mAuthListener = new FirebaseAuth.AuthStateListener() {
//                    @Override
//                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                        mUser = firebaseAuth.getCurrentUser();
//
//                        if(mUser != null) {
//                            Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");
//                            currentUserId = mUser.getUid();
//
//                            mFirestore = FirebaseFirestore.getInstance();
//                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                                    .setTimestampsInSnapshotsEnabled(true)
//                                    .build();
//                            mFirestore.setFirestoreSettings(settings);

                            DocumentReference userRef = mFirestore.collection("user").document(currentUserId).collection("shift").document(shiftId);

                            // Keep in pending with new text saying offer sent
                            userRef.update("shift_Offer_Sent", "yes");
                            userRef.update("shift_Spotter_Uid", candidateUid);

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "onComplete: getting shift information to update candidate schedule.");

                                        shiftCurrent = task.getResult().toObject(Shift.class);
                                        shiftDescription = shiftCurrent.getShift_Description();
                                        Log.d(TAG, "shift description: " + shiftDescription);
                                        shiftPay = shiftCurrent.getShift_Pay();
                                        Log.d(TAG, "shift pay: " + shiftPay);
                                        shiftLocation = shiftCurrent.getShift_Location();
                                        Log.d(TAG, "shift location: " + shiftLocation);
                                        shiftTitle = shiftCurrent.getShift_JobTitle();
                                        Log.d(TAG, "shift title: " + shiftTitle);


                                        DocumentReference candidateRef = mFirestore.collection("user").document(candidateUid).collection("schedule").document(schedId);

                                        // update fields in candidate(spotter)
                                        candidateRef.update("employer_Id", currentUserId);
                                        // Move to offer for candidate
                                        candidateRef.update("offer_received", "yes");
                                        candidateRef.update("shift_Description", shiftDescription);
                                        candidateRef.update("shift_Location", shiftLocation);
                                        candidateRef.update("shift_Pay", shiftPay);
                                        candidateRef.update("shift_Title", shiftTitle);
                                        candidateRef.update("shift_Id", shiftId);

                                    }
                                }
                            });
//                        }
//                    }
//                };

//                DocumentReference candidateRef = mFirestore.collection("user").document(candidateUid).collection("schedule").document(schedId);
//
//                // update fields in candidate(spotter)
//                candidateRef.update("employer_Id", currentUserId);
//                // Move to offer for candidate
//                candidateRef.update("offer_received", "yes");
//                candidateRef.update("shift_Description", shiftDescription);
//                candidateRef.update("shift_Location", shiftLocation);
//                candidateRef.update("shift_Pay", shiftPay);
//                candidateRef.update("shift_Title", shiftTitle);
//                candidateRef.update("shift_Id", shiftId);

                Toast toast = Toast.makeText(mContext, "Your Offer was Sent", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(Color.parseColor("#36454f"));
                TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                toast.show();

                Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                startActivity(intent);



            }
        });

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }

}