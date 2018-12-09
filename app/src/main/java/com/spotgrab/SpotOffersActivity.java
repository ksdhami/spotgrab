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

//import com.spotgrab.R;

public class SpotOffersActivity extends AppCompatActivity {
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
        mContext = SpotOffersActivity.this;
        Log.d(TAG, "\nonCreateView: view created\n");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.acitivity_candidate_selector);
        setContentView(R.layout.activity_spot_offers);

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
                                Log.d(TAG, "Here : " + loggedInUserSchedule.getOffer_received());
                                if (loggedInUserSchedule.getOffer_received().equals("yes") && loggedInUserSchedule.getShift_Accepted()==null) {
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
                Log.d(TAG, "spot offer clicked");

                final String shiftId = mCardArrayList.get(position).getShift_Id();
                Log.d(TAG, "shift id is: "+shiftId);
                final String employerId = mCardArrayList.get(position).getShift_Spotter_Uid();
                Log.d(TAG, "employer id is: "+employerId);

                new AlertDialog.Builder(mContext).setTitle("New Shift Offer")
                        .setMessage("Do you want to accept or decline the offer.")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "spot offer accepted");
                                mFirestore = FirebaseFirestore.getInstance();
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFirestore.setFirestoreSettings(settings);

                                mFirestore.collection("user").document(mUser.getUid()).collection("schedule").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        Log.d(TAG, "onEvent");
                                        if (e != null) {
                                            Log.d("", "Error : " + e.getMessage());
                                        }
                                        for (DocumentSnapshot doc : documentSnapshots) {
                                            if (doc.get("shift_Id") == shiftId && doc.get("employer_Id") == employerId) {
                                                DocumentReference spotCurrUserRef = mFirestore.collection("user").document(mUser.getUid()).collection("schedule").document(doc.getId());

                                                spotCurrUserRef.update("shift_Accepted", "yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter shift accepted ");
                                                    }
                                                });;

                                                DocumentReference empShiftRef = mFirestore.collection("user").document(employerId).collection("shift").document(shiftId);

                                                empShiftRef.update("shift_Filled", "yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "employer shift filled set to yes");
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                Intent intent = new Intent(mContext, HomeSpotterActivity.class);
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "spot offer declined");
                                mFirestore = FirebaseFirestore.getInstance();
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFirestore.setFirestoreSettings(settings);

                                mFirestore.collection("user").document(mUser.getUid()).collection("schedule").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        Log.d(TAG, "onEvent");
                                        if (e != null) {
                                            Log.d("", "Error : " + e.getMessage());
                                        }
                                        for (DocumentSnapshot doc : documentSnapshots) {
                                            if (doc.get("shift_Id") == shiftId && doc.get("employer_Id") == employerId) {
                                                // Update spotter to remove shift information and remove offer sent
                                                DocumentReference spotCurrUserRef = mFirestore.collection("user").document(mUser.getUid()).collection("schedule").document(doc.getId());

                                                spotCurrUserRef.update("offer_received", "no").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed offer ");
                                                    }
                                                });

                                                spotCurrUserRef.update("employer_Id", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed employer Id ");
                                                    }
                                                });

                                                spotCurrUserRef.update("shift_Description", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed shift description ");
                                                    }
                                                });

                                                spotCurrUserRef.update("shift_Id", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed shift Id ");
                                                    }
                                                });

                                                spotCurrUserRef.update("shift_Location", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed shift location ");
                                                    }
                                                });

                                                spotCurrUserRef.update("shift_Pay", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed shift pay ");
                                                    }
                                                });

                                                spotCurrUserRef.update("shift_Title", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "spotter removed shift title ");
                                                    }
                                                });

                                                // Update employer shift to remove spotter uid and remove offer
                                                DocumentReference empShiftRef = mFirestore.collection("user").document(employerId).collection("shift").document(shiftId);

                                                empShiftRef.update("shift_Offer_Sent", "no").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "employer shift offer sent returned to no");
                                                    }
                                                });
                                                empShiftRef.update("shift_Spotter_Uid", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "employer shift removed spotter uid; to null");
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                Intent intent = new Intent(mContext, HomeSpotterActivity.class);
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }

                        })
                        .show();
                //here we can handle what we do on the clicks
//                Intent intent = getIntent();
//                overridePendingTransition(0, 0);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(intent);

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

