package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

//import com.spotgrab.R;

public class SpotActiveActivity extends AppCompatActivity {
    private static final String TAG = "SpotActiveActivity";
    private ArrayList<SpotCardItem> mCardArrayList;
    private RecyclerView mRecyclerView;
    private SpotSelectorAdapter mAdapter;
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
        mContext = SpotActiveActivity.this;
        Log.d(TAG, "\nonCreateView: view created\n");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.acitivity_candidate_selector);
        setContentView(R.layout.activity_spot_active);

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
                                Log.d(TAG, "Here : " + loggedInUserSchedule.getShift_Accepted());
                                if ((loggedInUserSchedule.getShift_Accepted() != null)) {
                                    mCardArrayList.add(new SpotCardItem(loggedInUserSchedule.getShift_Title(), loggedInUserSchedule.getSchedule_StartTime(),
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
        mAdapter = new SpotSelectorAdapter(mCardArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SpotSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String shiftId = mCardArrayList.get(position).getShift_Id();
                String employerId = mCardArrayList.get(position).getShift_Spotter_Uid();
                String scheduleId = mCardArrayList.get(position).getDoc_Id();

                //here we can handle what we do on the clicks
                Intent intent = new Intent(mContext, SpotRatingActivity.class);
                intent.putExtra("shiftId", shiftId);
                intent.putExtra("employerId", employerId);
                intent.putExtra("schedId", scheduleId);
                startActivity(intent);
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

