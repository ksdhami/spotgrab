package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nhome button clicked.\n");
                Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                startActivity(intent);
            }
        });

        calendarBt = findViewById(R.id.empNavCalendarBt);
        calendarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\ncalendar button clicked.\n");
                Intent intent = new Intent(mContext, EmpCalendarActivity.class);
                startActivity(intent);
            }
        });

        activeBt = findViewById(R.id.empNavActiveBt);
        activeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nactive button clicked.\n");
                Intent intent = new Intent(mContext, EmpActiveActivity.class);
                startActivity(intent);
            }
        });

        pendingBt = findViewById(R.id.empNavPendingBt);
        pendingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\npending button clicked.\n");
                Intent intent = new Intent(mContext, EmpPendingActivity.class);
                startActivity(intent);
            }
        });

        settingBt = findViewById(R.id.empNavSettingsBt);
        settingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting button clicked.\n");
                Intent intent = new Intent(mContext, EditProfileActivity.class);
                startActivity(intent);
            }
        });

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
        homeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nhome button clicked.\n");
                Intent intent = new Intent(mContext, HomeEmployerActivity.class);
                startActivity(intent);
            }
        });

        calendarText = findViewById(R.id.empNavCalendar);
        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\ncalendar button clicked.\n");
                Intent intent = new Intent(mContext, EmpCalendarActivity.class);
                startActivity(intent);
            }
        });

        activeText = findViewById(R.id.empNavActiveJobs);
        activeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nactive button clicked.\n");
                Intent intent = new Intent(mContext, EmpActiveActivity.class);
                startActivity(intent);
            }
        });

        pendingText = findViewById(R.id.empNavPendingJobs);
        pendingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\npending button clicked.\n");
                Intent intent = new Intent(mContext, EmpPendingActivity.class);
                startActivity(intent);
            }
        });

        settingText = findViewById(R.id.empNavSettings);
        settingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting button clicked.\n");
                Intent intent = new Intent(mContext, EditProfileActivity.class);
                startActivity(intent);
            }
        });

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
                    Toast toast = Toast.makeText(mContext, "Signed Out", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#36454f"));
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                    toast.show();
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
