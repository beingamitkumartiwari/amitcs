package com.amtee.camscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;


/**
 * Created by DEVEN SINGH on 2/25/2015.
 */
public class PrivacyPolicyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
    }

    @Override
    public void onBackPressed() {
        Intent iMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iMain);
        finish();
    }
}
