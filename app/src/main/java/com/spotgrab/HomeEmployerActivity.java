package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.client.UserClient;
import com.spotgrab.models.User;
import com.spotgrab.module.GlideApp;


public class HomeEmployerActivity extends AppCompatActivity {

    private static final String TAG = "EmpHomeActivity";

    private Context mContext;

    private String defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/spotgrab-4f2cf.appspot.com/o/images%2Fprofile.png?alt=media&token=8fe8b4bf-1df0-4e82-bbce-35be6090a5c4";

    private Button activeButton, pendingButton, postShiftButton, menuButton;
    private ImageView profileImage;
    private TextView userDisplayName, userNumJobs;
    private RatingBar userRating;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DocumentReference docRef;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;

    private String userID;

    private User loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employer);

        Log.d(TAG, "\nonCreate: started.\n");


        mContext = HomeEmployerActivity.this;

        activeButton = findViewById(R.id.activeEmpButton);
        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "\nonClick: activeButton\n");
                Intent intent = new Intent(HomeEmployerActivity.this, EmpActiveActivity.class);
                startActivity(intent);
            }
        });

        pendingButton = findViewById(R.id.pendingEmpButton);
        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "\nonClick: pendingButton\n");
                Intent intent = new Intent(HomeEmployerActivity.this, EmpPendingActivity.class);
                startActivity(intent);
            }
        });

        postShiftButton = findViewById(R.id.postShiftButton);
        postShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\npost shift clicked.\n");
                Intent intent = new Intent(mContext, EmpCalendarActivity.class);
                startActivity(intent);
            }
        });

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        profileImage = findViewById(R.id.profileImage);

        userDisplayName = findViewById(R.id.firstLastNameText);
        userNumJobs = findViewById(R.id.numJobsText);

        userRating = findViewById(R.id.ratingBar);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");
                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    DocumentReference userRef = mFirestore.collection("user").document(mUser.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: successfully set the user client." + mUser.getUid().toString());
                                loggedInUser = task.getResult().toObject(User.class);
                                userDisplayName.setText(loggedInUser.getfName() + " " + loggedInUser.getlName() + " ");
                                userNumJobs.setText(loggedInUser.getNumJobs().toString());
                                if(loggedInUser.getRating() == null) {
                                    userRating.setRating(0);
                                } else {
                                    userRating.setRating(loggedInUser.getRating().floatValue());
                                }
                                //((UserClient)(getApplicationContext())).setUser(user);
                            }
                        }
                    });

//                    mFirestore.collection("user").document(mUser.getUid().toString()).collection("shift").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                }
//                            }
//                        }
//                    });

                    mStorage = FirebaseStorage.getInstance().getReference().child("images/").child(mUser.getUid());
                    GlideApp.with(mContext).load(mStorage).into(profileImage);

                } else {
                    // User is signed out
                    Log.d(TAG, "\nonAuthStateChanged:signed_out\n");
                    GlideApp.with(mContext).load(defaultProfileImageUrl).into(profileImage);
                }
                // ...
            }
        };

//        auth = FirebaseAuth.getInstance();
//        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                if (firebaseUser != null) {
//                    userID = firebaseUser.getUid().toString();
////                    sharedPref = getPreferences(MODE_PRIVATE);
////                    SharedPreferences.Editor editor = sharedPref.edit();
////                    editor.putString("firebasekey", userID);
////                    editor.commit();
//                }
//            }
//        };
//
//        docRef = db.collection("user").document(userID);
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                loggedInUser = documentSnapshot.toObject(User.class);
//            }
//        });
//
//        String fullName = MainActivity.user.getfName() + " " + MainActivity.user.getlName();
//        userDisplayName.setText(fullName);

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

