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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.models.Schedule;
import com.spotgrab.models.Shift;
import com.spotgrab.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

public class CandidateSelectorActivity extends AppCompatActivity {

    private static final String TAG = "CandidateSelectorActivity";
    private Context mContext;

    private ArrayList<CardItem> mCardArrayList;
    private RecyclerView mRecyclerView;
    private CandidateSelectorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;

    private Shift postedShift;
    private String shiftDate, shiftStart, shiftEnd;

    private Button menuButton;

    private String shiftId, schedId, employerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_selector);

        mContext = CandidateSelectorActivity.this;

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        shiftId = bundle.getString("Shift ID");
        shiftDate = bundle.getString("shift Date");
        shiftStart = bundle.getString("shift Start");
        shiftEnd = bundle.getString("shift End");
//        final String shiftDate = bundle.getString("Shift Date");
//        final String shiftStartTime = bundle.getString("Shift Start Time");
//        final String shiftEndTime = bundle.getString("Shift End Time");
//
        Log.d(TAG, "\nshift id :"+shiftId+"\n");
        //shiftDate = postedShift.getShift_Date();
        Log.d(TAG, "\nshift date: "+shiftDate);
        //shiftStart = postedShift.getShift_StartTime();
        Log.d(TAG, "\nshift start: "+shiftStart);
        //shiftEnd = postedShift.getShift_EndTime();
        Log.d(TAG, "\nshift end: "+shiftEnd);

        //Toast.makeText(mContext, "shift id :"+shiftId, Toast.LENGTH_SHORT).show();

        mCardArrayList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "\nAfter mAuth\n");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "\nAfter onAuthStateChanged\n");
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");

                    employerUserId = mUser.getUid();

                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    // Get shift information to compare with candidates
//                    DocumentReference documentReference = mFirestore.collection("user").document(mUser.getUid()).collection("shift").document(shiftId);
//                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                Log.d(TAG, "onComplete: successfully set the shift for user." + mUser.getUid().toString());
//                                postedShift = task.getResult().toObject(Shift.class);
//                                //shiftDate = postedShift.getShift_Date();
//                                Log.d(TAG, shiftDate);
//                                //shiftStart = postedShift.getShift_StartTime();
//                                Log.d(TAG, shiftStart);
//                                //shiftEnd = postedShift.getShift_EndTime();
//                                Log.d(TAG, shiftEnd);
//                            }
//                            else {
//                                Log.d(TAG, "onComplete: failed set the shift for user." + mUser.getUid().toString());
//
//                            }
//                        }
//                    });

                    mFirestore.collection("user").whereEqualTo("account", "Spotter").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("", "Error : " + e.getMessage());
                            }

                            for (DocumentSnapshot doc: queryDocumentSnapshots) {
                                if (doc != null) {
                                    // Gets spotters id
                                    Log.d(TAG, "spotter id = "+doc.getId());
                                    final User spotter = doc.toObject(User.class);

                                    final String candidateUid = doc.getId().toString();
                                    final String candidateName = ""+spotter.getfName()+" "+spotter.getlName()+" ";
                                    Log.d(TAG, candidateName);
                                    final float rating;
                                    if(spotter.getRating()==null) {
                                        rating = 0;
                                    } else {
                                        rating = spotter.getRating();
                                    }

//                                  // Get schedules of each spotter
                                    doc.getReference().collection("schedule").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d("", "Error : " + e.getMessage());
                                            }

                                            // Get availability to compare with shift and then display
                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                                if (doc != null) {
                                                    Log.d(TAG, "sched id = "+doc.getId());
                                                    Schedule sched = doc.toObject(Schedule.class);

                                                    schedId = doc.getId();

                                                    String schedStart = doc.getString("schedule_StartTime");
                                                    Log.d(TAG, schedStart);
                                                    String schedEnd = doc.getString("schedule_EndTime");
                                                    Log.d(TAG, schedEnd);
                                                    String schedDate = doc.getString("schedule_Date");
                                                    Log.d(TAG, schedDate);

                                                    Log.d(TAG, shiftDate);
                                                    if(shiftDate.equals(schedDate)) {
                                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                                                        try {
                                                            Log.d(TAG, "getting time difference for comparisons");

                                                            // returns milliseconds
                                                            Date spotStartTime = sdf.parse(schedStart);
                                                            Log.d(TAG, "spotter start "+spotStartTime.toString());
                                                            Date shiftStartTime = sdf.parse(shiftStart);
                                                            Log.d(TAG, "shift start "+shiftStartTime.toString());

                                                            Date spotEndTime = sdf.parse(schedEnd);
                                                            Log.d(TAG, "spotter end "+spotEndTime.toString());
                                                            Date shiftEndTime = sdf.parse(shiftEnd);
                                                            Log.d(TAG, "shift end "+shiftEndTime.toString());

                                                            Log.d(TAG, "shift end time "+shiftEndTime.getTime());
                                                            Log.d(TAG, "spot end time "+spotEndTime.getTime());

                                                            Long sst = shiftStartTime.getTime() - spotStartTime.getTime();
                                                            Log.d(TAG, sst.toString());
                                                            Long set = spotEndTime.getTime() - shiftEndTime.getTime();
                                                            Log.d(TAG, set.toString());

                                                            if(sst>0 && set>0) {
                                                                mCardArrayList.add(new CardItem(candidateName, candidateUid, rating, schedId));
                                                                buildRecyclerView();
                                                            }

                                                        } catch (ParseException exception) {
                                                            Log.d(TAG, "error converting times for comparisons");
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                    });

                                }
                            }
                        }
                    });
                }
            }
        };


//         init();

//        buildRecyclerView();

    }

//    private void init() {
//
//
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                Log.d(TAG, "\nAfter onAuthStateChanged.\n");
//                mUser = firebaseAuth.getCurrentUser();
//
//                if(mUser != null) {
//                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");
//
//                    mFirestore = FirebaseFirestore.getInstance();
//                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                            .setTimestampsInSnapshotsEnabled(true)
//                            .build();
//                    mFirestore.setFirestoreSettings(settings);
//
//                    // Get shift id from pending screen through addExtra in intent
//
//                    DocumentReference userRef = mFirestore.collection("user").document(mUser.getUid()).collection("shift").document();
//
//                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                Log.d(TAG, "onComplete: successfully set the user client." + mUser.getUid());
//                                postedShift = task.getResult().toObject(Shift.class);
//                            }
//                        }
//                    });
//                }
//            }
//        };
//    }

    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CandidateSelectorAdapter(mCardArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CandidateSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //here we can handle what we do on the clicks

                String candidateDetailID = mCardArrayList.get(position).getmUid();
                String candidateScedID = mCardArrayList.get(position).getSchedId();

                // Go to detailed look of candidate
                Intent intent = new Intent(CandidateSelectorActivity.this, DetailedLookActivity.class);
                intent.putExtra("current user id", employerUserId);
                intent.putExtra("uid", candidateDetailID);
                intent.putExtra("sched id", candidateScedID);
                intent.putExtra("shift id", shiftId);
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
        if(mFirestore != null) {

        }
    }


}
