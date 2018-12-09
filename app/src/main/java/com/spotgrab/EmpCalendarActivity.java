package com.spotgrab;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.spotgrab.models.Shift;
import com.spotgrab.models.User;

import java.util.Calendar;
import java.util.UUID;

public class EmpCalendarActivity extends AppCompatActivity {

    private static final String TAG = "EmpCalendarActivity";

    private CalendarView myCalendarView;

    private TextView dateDisplay;
    private EditText textChange, startTime, endTime, jobTitle, pay, location, description;
    private Button newButton, menuButton;

    public String sJobTitle, sPay, sLocation, sDescription, sStartTime, sEndTime, sDate;

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
        setContentView(R.layout.activity_emp_calendar);

        Log.d(TAG, "\nonCreate: started.\n");

        mContext = EmpCalendarActivity.this;
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

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
                startActivity(intent);
            }
        });

        newButton = findViewById(R.id.empCalAddBt);
        myCalendarView = findViewById(R.id.calendarView);
        dateDisplay = findViewById(R.id.empDateDisplay);

        jobTitle = findViewById(R.id.empJobCalET);
        startTime = findViewById(R.id.empStartCalET);
        endTime = findViewById(R.id.empEndCalET);
        pay = findViewById(R.id.empPayCalET);
        location = findViewById(R.id.empLocalCalET);
        description = findViewById(R.id.empDesCalET);

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
//                    textChange = findViewById(R.id.empJobCalET);
//                    textChange.setText("Cashier");
//                    textChange = findViewById(R.id.empStartCalET);
//                    textChange.setText("9:00");
//                    textChange = findViewById(R.id.empEndCalET);
//                    textChange.setText("14:00");
//                    textChange = findViewById(R.id.empPayCalET);
//                    textChange.setText("12");
//                    textChange = findViewById(R.id.empLocalCalET);
//                    textChange.setText("123 Street SW");
//                    textChange = findViewById(R.id.empDesCalET);
//                    textChange.setText("This is a job for a human");
//                }
//                else {
                    textChange = findViewById(R.id.empJobCalET);
                    textChange.setText("");
                    textChange = findViewById(R.id.empStartCalET);
                    textChange.setText("");
                    textChange = findViewById(R.id.empEndCalET);
                    textChange.setText("");
                    textChange = findViewById(R.id.empPayCalET);
                    textChange.setText("");
                    textChange = findViewById(R.id.empLocalCalET);
                    textChange.setText("");
                    textChange = findViewById(R.id.empDesCalET);
                    textChange.setText("");
 //               }
                //Toast.makeText(getApplicationContext(), "Selected Date:\n" + "Day = " + dayOfMonth + "\n" + "Month = " + month + "\n" + "Year = " + year, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Selected Date:\n" + "Day = " + dayOfMonth + "\n" + "Month = " + month + "\n" + "Year = " + year);
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
                mTimePicker = new TimePickerDialog(EmpCalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(EmpCalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

                sJobTitle = jobTitle.getText().toString();
                sStartTime = startTime.getText().toString();
                sEndTime = endTime.getText().toString();
                sPay = pay.getText().toString();
                sLocation = location.getText().toString();
                sDescription = description.getText().toString();

                if (checkInputs(sJobTitle, sStartTime, sEndTime, sDate, sPay, sLocation, sDescription)) {
                    Log.v("job", jobTitle.getText().toString());
                    //Toast.makeText(getApplicationContext(), "Job Title: " + sJobTitle + "\nStart Time: " + sStartTime + "\nEnd Time: " + sEndTime + "\nPay: " + sPay + "\nLocation: " + sLocation + "\nDescription: " + sDescription, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Job Title: " + sJobTitle + "\nStart Time: " + sStartTime + "\nEnd Time: " + sEndTime + "\nPay: " + sPay + "\nLocation: " + sLocation + "\nDescription: " + sDescription);

                    addNewShift(sJobTitle, sStartTime, sEndTime, sDate, sPay, sLocation, sDescription);
                }
                else {
                    Log.d("job", "wrong");

                }


            }
        });

    }

    private boolean checkInputs(String sJobTitle, String sStartTime, String sEndTime, String sDate, String sPay, String sLocation, String sDescription){
        Log.d(TAG, "checkInputs: checking inputs for null values.");

        if(sJobTitle.equals("") || sStartTime.equals("") || sEndTime.equals("") || sDate.equals("") || sPay.equals("") || sLocation.equals("") || sDescription.equals("")){
            //Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            Toast toast = Toast.makeText(mContext, "All fields must be filled out", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(Color.parseColor("#36454f"));
            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
            toast.show();
            return false;
        }
        return true;
    }

    private void addNewShift(String sJobTitle, String sStartTime, String sEndTime, String sDate, String sPay, String sLocation, String sDescription) {

        Shift shift = new Shift(sJobTitle, sStartTime, sEndTime, sDate, sPay, sLocation, sDescription, "no", "no", null, null, null);

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        mFirestore.setFirestoreSettings(settings);

        DocumentReference newShiftRef = mFirestore.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("shift").document(UUID.randomUUID().toString());

        newShiftRef.set(shift).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast toast = Toast.makeText(mContext, "Shift Added", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#36454f"));
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                    toast.show();
                    textChange.getText().clear();
                    startTime.getText().clear();
                    endTime.getText().clear();
                    jobTitle.getText().clear();
                    pay.getText().clear();
                    location.getText().clear();
                    description.getText().clear();
                } else {
                    Toast toast = Toast.makeText(mContext, "Failed to Add Shift", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#36454f"));
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                    toast.show();
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
