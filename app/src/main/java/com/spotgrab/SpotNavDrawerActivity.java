package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SpotNavDrawerActivity extends AppCompatActivity {

    private static final String TAG = "SpotNavDrawer";
    private Context mContext;


    private Button homeBt, calendarBt, availabilityBt, activeBt, offersBt, settingBt, logoutBt;
    private TextView homeText, calendarText, availabilityText, activeText, offersText, settingText;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_nav_drawer);

        mContext = SpotNavDrawerActivity.this;

        setupFirebaseListener();


        // ICON BUTTONS
        homeBt = findViewById(R.id.spotNavHomeBt);
        calendarBt = findViewById(R.id.spotNavCalendarBt);
        availabilityBt = findViewById(R.id.spotNavSchedBt);
        activeBt = findViewById(R.id.spotNavActiveBt);
        offersBt = findViewById(R.id.spotNavOffersBt);
        settingBt = findViewById(R.id.spotNavSettingsBt);
        logoutBt = findViewById(R.id.buttonLogout);
        logoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out the user.");
                FirebaseAuth.getInstance().signOut();
            }
        });



        // TEXT BUTTONS
        homeText = findViewById(R.id.spotNavHome);
//        homeText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        calendarText = findViewById(R.id.spotNavCalendar);
        availabilityText = findViewById(R.id.spotNavSched);
        activeText = findViewById(R.id.spotNavActiveJobs);
        offersText = findViewById(R.id.spotNavOffersBt);
        settingText = findViewById(R.id.spotNavSettings);
    }


    private void setupFirebaseListener(){
        Log.d(TAG, "setupFirebaseListener: setting up the auth state listener.");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Toast.makeText(mContext, "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    finish();
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}
