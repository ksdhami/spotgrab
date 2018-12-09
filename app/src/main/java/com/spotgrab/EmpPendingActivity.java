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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.models.Schedule;
import com.spotgrab.models.Shift;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class EmpPendingActivity extends AppCompatActivity {

    private static final String TAG = "EmpPendingActivity";
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

    private String spotShiftUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = EmpPendingActivity.this;
        Log.d(TAG, "\nonCreateView: view created\n");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_pending);

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

//                    mFirestore.collection("user").document(mUser.getUid().toString())
//                            .collection("shift").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            Log.d(TAG, "onEvent");
//                            if (task != null) {
//                                for (DocumentSnapshot doc : task.getResult()) {
//
//                                    loggedInUserShift = doc.toObject(Shift.class);
//                                    mCardArrayList.add(new EmpCardItem(loggedInUserShift.getShift_JobTitle(), loggedInUserShift.getShift_StartTime(),
//                                            loggedInUserShift.getShift_EndTime(), loggedInUserShift.getShift_Date(), loggedInUserShift.getShift_Pay(),
//                                            loggedInUserShift.getShift_Location(), loggedInUserShift.getShift_Description(), loggedInUserShift.getShift_Filled(),
//                                            loggedInUserShift.getShift_Offer_Sent(), loggedInUserShift.getShift_Spotter_Uid()));
//                                    //createCardArrayList();
//                                    buildRecyclerView();
//                                    Log.d(TAG, "Stuff: " + mUser.getUid() + "More: " + loggedInUserShift.getShift_Date());
//                                    Log.d(TAG, "else; no documents in active");
//                                    Toast.makeText(mContext, "no active jobs", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                    Log.d(TAG, "else; no documents in active");
//                                    Toast.makeText(mContext, "no active jobs", Toast.LENGTH_SHORT).show();
//                                }
//                        }
//                    });
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
                                    if (loggedInUserShift.getShift_Filled().equals("no")) {
                                        mCardArrayList.add(new EmpCardItem(loggedInUserShift.getShift_JobTitle(), loggedInUserShift.getShift_StartTime(),
                                                loggedInUserShift.getShift_EndTime(), loggedInUserShift.getShift_Date(),loggedInUserShift.getShift_Pay(),
                                                loggedInUserShift.getShift_Location(),loggedInUserShift.getShift_Description(),loggedInUserShift.getShift_Filled(),
                                                loggedInUserShift.getShift_Offer_Sent(),loggedInUserShift.getShift_Spotter_Uid(), doc.getId()));
                                        //createCardArrayList();
                                        buildRecyclerView();
                                        Log.d(TAG, "Stuff: " + mUser.getUid() + "More: " + loggedInUserShift.getShift_Date());
                                    }
//                                    Log.d(TAG, "else; no documents in active");
//                                    Toast.makeText(mContext, "no active jobs", Toast.LENGTH_SHORT).show();
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
                Log.d(TAG, "clicked card item");
                final String shiftOfferSent = mCardArrayList.get(position).getShift_Offer_Sent();
                final String shiftId = mCardArrayList.get(position).getShift_Id();
                Log.d(TAG, "shift offer sent = "+shiftOfferSent);
                final String shiftDate = mCardArrayList.get(position).getShift_Date();
                final String shiftStart = mCardArrayList.get(position).getShift_StartTime();
                final String shiftEnd = mCardArrayList.get(position).getShift_EndTime();
                final String shiftSpotterUid = mCardArrayList.get(position).getShift_Spotter_Uid();
                Log.d(TAG, "spotter uid = "+shiftSpotterUid);

                if (shiftOfferSent.equals("yes")) {
                    new AlertDialog.Builder(mContext).setTitle("Rescind Offer")
                            .setMessage("Do you want to rescind the offer sent.")
                            .setPositiveButton("Rescind", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "offer rescind clicked");
                                    mFirestore = FirebaseFirestore.getInstance();
                                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                            .setTimestampsInSnapshotsEnabled(true)
                                            .build();
                                    mFirestore.setFirestoreSettings(settings);

                                    // update employer offer sent
                                    DocumentReference documentReference = mFirestore.collection("user").document(mUser.getUid()).collection("shift").document(shiftId);

                                    // UNCOMMENT
                                    documentReference.update("shift_Spotter_Uid", null);
                                    documentReference.update("shift_Offer_Sent", "no");

                                    // update spotter info in their schedule

                                    Query docRef = mFirestore.collection("user").document(shiftSpotterUid).collection("schedule").whereEqualTo("shift_Id", shiftId);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "task successful");
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, "spotter schedule document = "+document.getId());
                                                    if (document.get("shift_Id").equals(shiftId) && document.get("employer_Id").equals(mUser.getUid())) {
                                                        Log.d(TAG, "right spotter");
                                                        DocumentReference spotCurrUserRef = mFirestore.collection("user").document(shiftSpotterUid).collection("schedule").document(document.getId());

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
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
//                                    docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                            Log.d(TAG, "onEvent");
//                                            if (e != null) {
//                                                Log.d("", "Error : " + e.getMessage());
//                                            }
//                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                                                //if (doc.get("shift_Id") == shiftId) {
//                                                    Log.d(TAG, "query doc for spotter; id is: "+doc.getId());
//                                                //}
//                                            }
//                                        }
//                                    });


                                    Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    //overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }

                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            //overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(mContext).setTitle("Take A Look At Some Candidates")
                        .setMessage("Do you want to browse candidates or remove the shift.")
                        .setPositiveButton("Browse Candidates", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, CandidateSelectorActivity.class);
                                intent.putExtra("shift Start", shiftStart);
                                intent.putExtra("shift End", shiftEnd);
                                intent.putExtra("shift Date", shiftDate);
                                intent.putExtra("Shift ID", shiftId);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Remove Shift", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "removing shift");

                                mFirestore = FirebaseFirestore.getInstance();
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFirestore.setFirestoreSettings(settings);

                                mFirestore.collection("user").document(mUser.getUid()).collection("shift").document(shiftId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "shift delete success: ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "shift delete failed: ");
                                    }
                                });

                                Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                //overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                    }).show();

//                    Intent intent = new Intent(mContext, CandidateSelectorActivity.class);
//                    intent.putExtra("shift Start", shiftStart);
//                    intent.putExtra("shift End", shiftEnd);
//                    intent.putExtra("shift Date", shiftDate);
//                    intent.putExtra("Shift ID", shiftId);
//                    startActivity(intent);
                }
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

