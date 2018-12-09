package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlanActivity extends AppCompatActivity {

    private static final String TAG = "PlanActivity";

    private Context mContext;

    private Spinner spinnerAccount;
    private EditText mCard, mCode, mName, mMonth, mYear;
    private String card, code, name, month, year, plan;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mContext = PlanActivity.this;

        Log.d(TAG, "\nonCreate: started.\n");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        init();
    }

    private void init() {
        // Dropdown Menu Created
        spinnerAccount = findViewById(R.id.accountTypeSpinner);
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(PlanActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Plan));
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(accountAdapter);


        nextButton = findViewById(R.id.nextButton);

        mCard = findViewById(R.id.etCreditCardNum);
        mName = findViewById(R.id.etNameOnCard);
        mCode = findViewById(R.id.etSecurityCode);
        mMonth = findViewById(R.id.etExpirationMonth);
        mYear = findViewById(R.id.etExpirationYear);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plan = spinnerAccount.getSelectedItem().toString();
                card = mCard.getText().toString();
                name = mName.getText().toString();
                code = mCode.getText().toString();
                month = mMonth.getText().toString();
                year = mYear.getText().toString();

                if (checkInputs(plan, card, name, month, year, code)) {
                    Toast toast = Toast.makeText(mContext, "Welcome to SpotGrab", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#36454f"));
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                    toast.show();
                    Intent intent = new Intent(PlanActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });




    }

    private boolean checkInputs(String plan, String card, String name, String month, String year, String code){
        Log.d(TAG, "checkInputs: checking inputs for null values.");

        if(plan.equals("") || card.equals("") || name.equals("") || month.equals("") || year.equals("") || code.equals("")){
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
}
