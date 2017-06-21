package com.amtee.camscanner.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;


public class RegistrationActivity extends Activity {

    TextView tv_heading;
    EditText passcode;
    EditText confirmPasscode;
    Button submit;
    MyPrefs sharedPrefs;
    CheckBox inputType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        sharedPrefs = new MyPrefs(this);
        initializePalletes();
        if (sharedPrefs.isInputTypeAlphaNumeric()) {
            inputType.setChecked(true);
            passcode.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasscode.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            inputType.setChecked(false);
            passcode.setInputType(InputType.TYPE_CLASS_NUMBER);
            passcode.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            confirmPasscode.setInputType(InputType.TYPE_CLASS_NUMBER);
            confirmPasscode
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
        }

        inputType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    sharedPrefs.setInputTypeAlphaNumeric(true);
                    passcode.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasscode.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    sharedPrefs.setInputTypeAlphaNumeric(false);
                    passcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    passcode.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    confirmPasscode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    confirmPasscode
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                }
            }
        });

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void initializePalletes() {
        tv_heading= (TextView) findViewById(R.id.textview_heading);
        passcode = (EditText) findViewById(R.id.passcode);
        confirmPasscode = (EditText) findViewById(R.id.confirm_passcode);
        submit = (Button) findViewById(R.id.submit_button);
        inputType = (CheckBox) findViewById(R.id.input_type);
        if(getIntent().getStringExtra("key1").equals("defaultpassword")){
            tv_heading.setText("Reset Password");
        }else{
            tv_heading.setText("Registration");
        }
    }

    private void registerUser() {
        if (passcode.getText().toString().equals("")) {
            passcode.setError("Field should not be empty.");
        } else if (confirmPasscode.getText().toString().equals("")) {
            confirmPasscode.setError("Field should not be empty.");
        } else if (!confirmPasscode.getText().toString()
                .equals(passcode.getText().toString())) {
            passcode.setText("");
            confirmPasscode.setText("");
            confirmPasscode.setError("Password did not match.");
        } else {
            sharedPrefs.setPassword(passcode.getText().toString());
            Toast.makeText(getApplicationContext(),
                    "Registration successfully completed.", Toast.LENGTH_LONG)
                    .show();
            finish();
            Intent intentSecurity = new Intent(getApplicationContext(),
                    SecurityQActivity.class);
            startActivity(intentSecurity);
        }
    }
}
