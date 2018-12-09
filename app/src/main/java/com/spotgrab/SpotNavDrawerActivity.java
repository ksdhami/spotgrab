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
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, HomeSpotterActivity.class);
                startActivity(intent);
            }
        });

        calendarBt = findViewById(R.id.spotNavCalendarBt);
        calendarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotCalendarActivity.class);
                startActivity(intent);
            }
        });

        availabilityBt = findViewById(R.id.spotNavSchedBt);
        availabilityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotSchedActivity.class);
                startActivity(intent);
            }
        });

        activeBt = findViewById(R.id.spotNavActiveBt);
        activeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotActiveActivity.class);
                startActivity(intent);
            }
        });

        offersBt = findViewById(R.id.spotNavOffersBt);
        offersBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotOffersActivity.class);
                startActivity(intent);
            }
        });

        settingBt = findViewById(R.id.spotNavSettingsBt);
        settingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
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



        // TEXT BUTTONS
        homeText = findViewById(R.id.spotNavHome);
        homeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, HomeSpotterActivity.class);
                startActivity(intent);
            }
        });

        calendarText = findViewById(R.id.spotNavCalendar);
        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotCalendarActivity.class);
                startActivity(intent);
            }
        });

        availabilityText = findViewById(R.id.spotNavSched);
        availabilityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotSchedActivity.class);
                startActivity(intent);
            }
        });

        activeText = findViewById(R.id.spotNavActiveJobs);
        activeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotActiveActivity.class);
                startActivity(intent);
            }
        });

        offersText = findViewById(R.id.spotNavOffers);
        offersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
                Intent intent = new Intent(mContext, SpotOffersActivity.class);
                startActivity(intent);
            }
        });

        settingText = findViewById(R.id.spotNavSettings);
        settingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nsetting clicked.\n");
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
                    Toast toast = Toast.makeText(mContext, "Signed out", Toast.LENGTH_SHORT);
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
