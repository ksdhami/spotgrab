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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spotgrab.client.UserClient;
import com.spotgrab.models.User;
import com.spotgrab.module.GlideApp;


public class HomeSpotterActivity extends AppCompatActivity {

    private static final String TAG = "HomeSpotterActivity";

    private Context mContext;

    private String defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/spotgrab-4f2cf.appspot.com/o/images%2Fprofile.png?alt=media&token=8fe8b4bf-1df0-4e82-bbce-35be6090a5c4";

    private Button notificationButton, activeButton, offerButton, postSchedButton, menuButton;
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
        setContentView(R.layout.activity_home_spotter);

        mContext = HomeSpotterActivity.this;

        notificationButton = findViewById(R.id.notificationSpotButton);
        activeButton = findViewById(R.id.activeSpotButton);
        offerButton = findViewById(R.id.offersSpotButton);

        postSchedButton = findViewById(R.id.postSchedButton);
        postSchedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\npost shift clicked.\n");
                Intent intent = new Intent(mContext, SpotCalendarActivity.class);
                startActivity(intent);
            }
        });

        menuButton = findViewById(R.id.menuSpotButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nmenu clicked.\n");
                Intent intent = new Intent(mContext, SpotNavDrawerActivity.class);
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
                                  userRating.setRating(loggedInUser.getRating().intValue());
                                }

                                //Toast.makeText(mContext, user.getAccount(), Toast.LENGTH_SHORT).show();
                                //((UserClient)(getApplicationContext())).setUser(user);
                            } else {
                                Log.d(TAG, "onComplete: failed to set the user client.");
                            }
                        }
                    });

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
