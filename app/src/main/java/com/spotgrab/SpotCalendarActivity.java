package com.spotgrab;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.spotgrab.models.Schedule;
import com.spotgrab.models.User;

import java.util.Calendar;
import java.util.UUID;

public class SpotCalendarActivity extends AppCompatActivity {

    private static final String TAG = "SpotCalendarActivity";

    private CalendarView myCalendarView;

    private TextView dateDisplay;
    private EditText textChange, startTime, endTime;
    private Button newButton, menuButton;

    public  String sStartTime, sEndTime, sDate;

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    private String userID;

    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_calendar);

        Log.d(TAG, "\nonCreate: started.\n");

        mContext = SpotCalendarActivity.this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null) {
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");
                } else {
                    Log.d(TAG, "\nonAuthStateChanged:signed_out\n");
                }
            }
        };

        menuButton = findViewById(R.id.menuSpotButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        newButton = findViewById(R.id.spotCalAddBt);
        myCalendarView = findViewById(R.id.calendarView);
        dateDisplay = findViewById(R.id.spotDateDisplay);

        startTime = findViewById(R.id.spotStartCalET);
        endTime = findViewById(R.id.spotEndCalET);

        startTime.requestFocus();
        startTime.setShowSoftInputOnFocus(false);
        endTime.requestFocus();
        endTime.setShowSoftInputOnFocus(false);


        myCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
                month = month+1;
                sDate = year + "/" + month + "/" + dayOfMonth;
                Log.d(TAG, "debug");
                dateDisplay.setText(sDate);
//                if (year == 2018 && month == 11 && dayOfMonth == 13) { // TEST
//                    newButton.setVisibility(View.VISIBLE);
//                    textChange = findViewById(R.id.spotStartCalET);
//                    textChange.setText("9:00");
//                    textChange = findViewById(R.id.spotEndCalET);
//                    textChange.setText("14:00");
//                }
//                else {
                textChange = findViewById(R.id.spotStartCalET);
                textChange.setText("");
                textChange = findViewById(R.id.spotEndCalET);
                textChange.setText("");
                //               }
                Toast.makeText(getApplicationContext(), "Selected Date:\n" + "Day = " + dayOfMonth + "\n" + "Month = " + month + "\n" + "Year = " + year, Toast.LENGTH_SHORT).show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Log.d(TAG, "clicked on start");
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SpotCalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour <= 9 && selectedMinute <= 9) {
                            startTime.setText("0" + selectedHour + ":" + "0" + selectedMinute);
                        } else if (selectedMinute <= 9) {
                            startTime.setText(selectedHour + ":" + "0" + selectedMinute);
                        } else if (selectedHour <= 9) {
                            startTime.setText("0" + selectedHour + ":" + selectedMinute);
                        } else {
                            startTime.setText(selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);// Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Log.d(TAG, "clicked on end");
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SpotCalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour <= 9 && selectedMinute <= 9) {
                            endTime.setText("0" + selectedHour + ":" + "0" + selectedMinute);
                        } else if (selectedMinute <= 9) {
                            endTime.setText(selectedHour + ":" + "0" + selectedMinute);
                        } else if (selectedHour <= 9) {
                            endTime.setText("0" + selectedHour + ":" + selectedMinute);
                        } else {
                            endTime.setText(selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);// Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sStartTime = startTime.getText().toString();
                sEndTime = endTime.getText().toString();

                if (checkInputs(sStartTime, sEndTime, sDate)) {
                    Toast.makeText(getApplicationContext(), "Start Time: " + sStartTime + "\nEnd Time: " + sEndTime, Toast.LENGTH_SHORT).show();

                    addNewSched(sStartTime,sEndTime, sDate);
                }
                else {
                    //Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkInputs(String sStartTime, String sEndTime, String sDate){
        Log.d(TAG, "checkInputs: checking inputs for null values.");

        if(sStartTime.equals("") || sEndTime.equals("") || sDate.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addNewSched(String sStartTime, String sEndTime, String sDate) {

        Schedule sched = new Schedule(sStartTime, sEndTime, sDate);

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        mFirestore.setFirestoreSettings(settings);

        DocumentReference newSchedRef = mFirestore.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("schedule").document("sched: "+UUID.randomUUID());

        newSchedRef.set(sched).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(mContext, "Schedule Added", Toast.LENGTH_SHORT).show();
                    textChange.getText().clear();
                    startTime.getText().clear();
                    endTime.getText().clear();
                } else {
                    Toast.makeText(mContext, "Failed to Add Schedule", Toast.LENGTH_SHORT).show();
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
