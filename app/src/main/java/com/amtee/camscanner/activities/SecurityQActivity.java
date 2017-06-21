package com.amtee.camscanner.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;


public class SecurityQActivity extends Activity {

    MyPrefs sharedPrefs;
    Button ok;
    Spinner queSpinner;
    EditText ansQue;
    String[] ques = {"What was your first school?",
            "What was your childhood nickname?",
            "What is your monther's maiden name?",
            "Who was your childhood hero?",
            "What is your lover's middle name?",};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sec_q);
        sharedPrefs = new MyPrefs(this);
        initializePalletes();
        addListners();
    }

    private void initializePalletes() {
        ok = (Button) findViewById(R.id.ok_button);
        ansQue = (EditText) findViewById(R.id.security_ans);
        queSpinner = (Spinner) findViewById(R.id.spinner_ques);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multiline_spinner_textview, ques);
        adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        queSpinner.setAdapter(adapter);
    }

    private void addListners() {
        queSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View arg1, int pos,
                                       long arg3) {
                sharedPrefs.setQuestion(av.getItemAtPosition(pos).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ansQue.getText().toString().equals("")) {
                    ansQue.setError("Please provide any answer!");
                } else {
                    sharedPrefs.setAnswer(ansQue.getText().toString());
                    sharedPrefs.setRegister(true);
                    sharedPrefs.setFirstTime(false);
                    sharedPrefs.setTogChecked(true);
                    Toast.makeText(SecurityQActivity.this, "Lock Activated", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intentMain = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intentMain);
                }
            }
        });
    }
}
