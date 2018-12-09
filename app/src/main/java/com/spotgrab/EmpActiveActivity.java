package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotgrab.models.Shift;

import java.util.ArrayList;

public class EmpActiveActivity extends AppCompatActivity {

    private static final String TAG = "EmpActiveActivity";
    private Context mContext;

    private ArrayList<EmpCardItem> mCardArrayList;
    private RecyclerView mRecyclerView;
    private EmpActivePendingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button menuButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;

    private Shift loggedInUserShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = EmpActiveActivity.this;
        Log.d(TAG, "\nonCreateView: view created\n");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_active);

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
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
                            .collection("shift").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            Log.d(TAG, "onEvent");
                            if (e != null) {
                                Log.d("", "Error : " + e.getMessage());
                            }
                            for (DocumentSnapshot doc : documentSnapshots) {
                                loggedInUserShift = doc.toObject(Shift.class);
                                if (loggedInUserShift.getShift_Filled().equals("yes")) {
                                    mCardArrayList.add(new EmpCardItem(loggedInUserShift.getShift_JobTitle(), loggedInUserShift.getShift_StartTime(),
                                            loggedInUserShift.getShift_EndTime(), loggedInUserShift.getShift_Date(),loggedInUserShift.getShift_Pay(),
                                            loggedInUserShift.getShift_Location(),loggedInUserShift.getShift_Description(),loggedInUserShift.getShift_Filled(),
                                            loggedInUserShift.getShift_Offer_Sent(),loggedInUserShift.getShift_Spotter_Uid(), doc.getId()));
                                    //createCardArrayList();
                                    buildRecyclerView();
                                    Log.d(TAG, "Stuff: " + mUser.getUid() + "More: " + loggedInUserShift.getShift_Date());
                                }
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
        mAdapter = new EmpActivePendingAdapter(mCardArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new EmpActivePendingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String shiftId = mCardArrayList.get(position).getShift_Id();
                //here we can handle what we do on the clicks
                Intent intent = new Intent(mContext, EmpRatingActivity.class);
                intent.putExtra("Shift ID", shiftId);
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