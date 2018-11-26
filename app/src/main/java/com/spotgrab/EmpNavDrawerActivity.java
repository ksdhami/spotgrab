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

public class EmpNavDrawerActivity extends AppCompatActivity {

    private static final String TAG = "EmpNavDrawer";
    private Context mContext;

    private Button homeBt, calendarBt, activeBt, pendingBt, settingBt, logoutBt;
    private TextView homeText, calendarText, activeText, pendingText, settingText;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_nav_drawer);

        mContext = EmpNavDrawerActivity.this;

        setupFirebaseListener();


        // ICON BUTTON
        homeBt = findViewById(R.id.empNavHomeBt);
        calendarBt = findViewById(R.id.empNavCalendarBt);
        activeBt = findViewById(R.id.empNavActiveBt);
        pendingBt = findViewById(R.id.empNavPendingBt);
        settingBt = findViewById(R.id.empNavSettingsBt);
        logoutBt = findViewById(R.id.buttonLogout);
        logoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out the user.");
                FirebaseAuth.getInstance().signOut();
            }
        });


        // TEXT BUTTON
        homeText = findViewById(R.id.empNavHome);
//        homeText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        calendarText = findViewById(R.id.empNavCalendar);
        activeText = findViewById(R.id.empNavActiveJobs);
        pendingText = findViewById(R.id.empNavPendingJobs);
        settingText = findViewById(R.id.empNavSettings);
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
