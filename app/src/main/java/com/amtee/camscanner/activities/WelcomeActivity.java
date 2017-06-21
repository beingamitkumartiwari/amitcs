package com.amtee.camscanner.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;


/**
 * Created by DEVEN SINGH on 2/22/2015.
 */
public class WelcomeActivity extends Activity {

    MyPrefs myPrefs;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/andi_ttf.TTF");
        TextView appName = (TextView) findViewById(R.id.app_namee);
        appName.setTypeface(typeface);
        myPrefs = new MyPrefs(WelcomeActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (myPrefs.isRegister() == true) {
                        Intent intentLogin = new Intent(getApplicationContext(), LoginPageActivity.class);
                        startActivity(intentLogin);
                        finish();
                    } else {
                        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentMain);
                        finish();
                    }
                }
            }
        }).start();
    }
}


//{
//        if (myPrefs.isRegister()) {
//Intent intentLogin = new Intent(getApplicationContext(), LoginPageActivity.class);
//    startActivity(intentLogin);
//        finish();
//        } else {
//        Intent intentRegistration = new Intent(getApplicationContext(), RegistrationActivity.class);
//        startActivity(intentRegistration);
//        finish();
//
//        }
//        }
