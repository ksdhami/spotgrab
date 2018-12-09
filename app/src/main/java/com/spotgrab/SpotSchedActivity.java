package com.spotgrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.models.Schedule;

import java.util.ArrayList;

import javax.annotation.Nullable;

//import com.spotgrab.R;

public class SpotSchedActivity extends AppCompatActivity {

    private static final String TAG = "SpotSchedActivity";

    private ArrayList<SpotCardItem> mCardArrayList;
    private RecyclerView mRecyclerView;
    private SpotAvailabilityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DocumentReference docRef;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;

    private Schedule loggedInUserSchedule;

    private Context mContext;

    private Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = SpotSchedActivity.this;
        Log.d(TAG, "\nonCreateView: view created\n");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.acitivity_candidate_selector);
        setContentView(R.layout.activity_spot_sched);

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        mCardArrayList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "\nAfter mAuth\n");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "\nAfter onAuthStateChanged\n");
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");
                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    mFirestore.collection("user").document(mUser.getUid().toString())
                            .collection("schedule").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            Log.d(TAG, "onEvent");
                            if (e != null) {
                                Log.d("", "Error : " + e.getMessage());
                            }
                            for (DocumentSnapshot doc : documentSnapshots) {
                                loggedInUserSchedule = doc.toObject(Schedule.class);
                                if (loggedInUserSchedule.getShift_Id() == null) {

                                    mCardArrayList.add(new SpotCardItem(loggedInUserSchedule.getSchedule_Date(), loggedInUserSchedule.getSchedule_StartTime(),
                                            loggedInUserSchedule.getSchedule_EndTime(), loggedInUserSchedule.getSchedule_Date(),loggedInUserSchedule.getShift_Pay(),
                                            loggedInUserSchedule.getShift_Location(),loggedInUserSchedule.getShift_Description(),loggedInUserSchedule.getShift_Accepted(),
                                            loggedInUserSchedule.getOffer_received(),loggedInUserSchedule.getEmployer_Id(), loggedInUserSchedule.getShift_Id(), doc.getId()));
                                    //createCardArrayList();
                                    buildRecyclerView();
                                }
                                Log.d(TAG, "Stuff: " + mUser.getUid() + "More: " + loggedInUserSchedule.getSchedule_Date());
                            }
                        }
                    });
                }
            }
        };
    }


    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SpotAvailabilityAdapter(mCardArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SpotAvailabilityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "sched clicked");

                final String schedDocDate = mCardArrayList.get(position).getShift_Date();
                Log.d(TAG, "sched date: "+schedDocDate);
                final String schedDocStart = mCardArrayList.get(position).getShift_StartTime();
                Log.d(TAG, "sched start: "+schedDocStart);
                final String schedDocEnd = mCardArrayList.get(position).getShift_EndTime();
                Log.d(TAG, "sched end: "+schedDocEnd);

                new AlertDialog.Builder(mContext)
                        .setTitle("Availability")
                        .setMessage("Do you want to remove your availability for "+schedDocDate+" from "+schedDocStart+" - "+schedDocEnd)
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "removing schedule");

                                mFirestore = FirebaseFirestore.getInstance();
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFirestore.setFirestoreSettings(settings);

                                mFirestore.collection("user").document(mUser.getUid().toString())
                                        .collection("schedule").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        Log.d(TAG, "onEvent");
                                        if (e != null) {
                                            Log.d("", "Error : " + e.getMessage());
                                        }
                                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                            Log.d(TAG, "doc id: "+doc.getId());
                                            loggedInUserSchedule = doc.toObject(Schedule.class);
                                            if ((loggedInUserSchedule.getSchedule_Date().equals(schedDocDate)) && (loggedInUserSchedule.getSchedule_StartTime().equals(schedDocStart)) && (loggedInUserSchedule.getSchedule_EndTime().equals(schedDocEnd))) {

                                                mFirestore.collection("user").document(mUser.getUid().toString())
                                                        .collection("schedule").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "sched delete success: ");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "sched delete failed: ");
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });

                                Intent intent = new Intent(mContext, HomeSpotterActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "cancel button pushed; do nothing");
                            }
                        })
                        .show();
            }
        });
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
