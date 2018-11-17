package com.spotgrab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RegisterActivity extends AppCompatActivity {

    Spinner spinnerAccount;
    String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerAccount = findViewById(R.id.accountTypeSpinner);
        accountType = spinnerAccount.getSelectedItem().toString();

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Account));
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(accountAdapter);


    }
}
