package com.amtee.camscanner.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.RegistrationActivity;
import com.amtee.camscanner.utilities.extra_utils.AppConstants;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by DEVEN SINGH on 1/13/2015.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {
    LinearLayout llPassSetting;
    LinearLayout llQueSetting;
    LinearLayout llFeedBack;
    LinearLayout llRecommend, llChngaePass;
    CheckBox tog_Btn;
    MyPrefs myPrefs;
    private InterstitialAd mInterstitialAd;
    private Handler handler1 = new Handler();
    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initPalletes(rootView);
        adMobFullPageAd();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initPalletes(View rootView) {
        myPrefs = new MyPrefs(getActivity());
        tog_Btn = (CheckBox) rootView.findViewById(R.id.tog_btn);
        llPassSetting = (LinearLayout) rootView.findViewById(R.id.ll_pass_setting);
        llQueSetting = (LinearLayout) rootView.findViewById(R.id.ll_ques_setting);
        llChngaePass = (LinearLayout) rootView.findViewById(R.id.ll_change_pass);
        llFeedBack = (LinearLayout) rootView.findViewById(R.id.ll_feedback);
        llRecommend = (LinearLayout) rootView.findViewById(R.id.ll_recommend);
        llPassSetting.setOnClickListener(this);
        llQueSetting.setOnClickListener(this);
        llRecommend.setOnClickListener(this);
        llChngaePass.setOnClickListener(this);
        llFeedBack.setOnClickListener(this);
        tog_Btn.setChecked(myPrefs.isTogChecked());
    }
    private void adMobFullPageAd() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(myPrefs.getAddmob());
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
    Runnable mShowFullPageAdTask = new Runnable() {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pass_setting:
                if (!tog_Btn.isChecked()) {
                    if (myPrefs.isFirstTime()) {
                        Intent intentRegistration = new Intent(getActivity(), RegistrationActivity.class);
                        intentRegistration.putExtra("key1","Setting");
                        startActivity(intentRegistration);
                        tog_Btn.setChecked(true);
                        getActivity().finish();
                    } else {
                        tog_Btn.setChecked(true);
                        myPrefs.setTogChecked(true);
                        myPrefs.setRegister(true);
                        Toast.makeText(getActivity(), "Lock Activated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tog_Btn.setChecked(false);
                    myPrefs.setTogChecked(false);
                    myPrefs.setRegister(false);
                    Toast.makeText(getActivity(), "Lock Deactivated", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_change_pass:
                changePassword();
                break;
            case R.id.ll_ques_setting:
                changeQues();
                break;
            case R.id.ll_feedback:
                feedBack();
                break;
            case R.id.ll_recommend:
                recommend();
                break;
        }
    }

    private void recommend() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "This application is awesome you should try it." + "\n" + "https://play.google.com/store/apps/details?id=com.atmee.camscanner";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void feedBack() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"support@mtechnovation.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email,
                "Choose an Email client:"));
    }

    private void changeQues() {
        String[] ques = {"What was your first school?",
                "What was your childhood nickname?",
                "What is your monther's maiden name?",
                "Who was your childhood hero?",
                "What is your lover's middle name?",};
        final String ans[] = new String[1];
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_que);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button save = (Button) dialog.findViewById(R.id.save_bt);
        final EditText ansQue = (EditText) dialog.findViewById(R.id.security_ans_change);
        final Spinner queSpinner = (Spinner) dialog.findViewById(R.id.spinner_ques_change);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.multiline_spinner_textview, ques);
        adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        queSpinner.setAdapter(adapter);

        queSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> av, View arg1, int pos,
                                       long arg3) {
                ans[0] = av.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ansQue.getText().toString().equals("")) {
                    ansQue.setError("Please provide any answer!");
                } else {
                    myPrefs.setQuestion(ans[0]);
                    myPrefs.setAnswer(ansQue.getText().toString());
                    Toast.makeText(getActivity(), "Security question successfully changed.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void changePassword() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_change);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        Button save = (Button) dialog.findViewById(R.id.save_button);
        final EditText oldPass = (EditText) dialog.findViewById(R.id.old_passcode);
        final EditText newPass = (EditText) dialog.findViewById(R.id.new_passcode);
        final EditText confrmPass = (EditText) dialog.findViewById(R.id.confirm_new_passcode);
        CheckBox inputType = (CheckBox) dialog.findViewById(R.id.input_type_set);
        inputType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    oldPass.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPass.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confrmPass.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    oldPass.setInputType(InputType.TYPE_CLASS_NUMBER);
                    oldPass
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    newPass.setInputType(InputType.TYPE_CLASS_NUMBER);
                    newPass
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    confrmPass.setInputType(InputType.TYPE_CLASS_NUMBER);
                    confrmPass
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (oldPass.getText().toString().equals("")) {
                    oldPass.setError("Field should not be empty.");
                } else if (newPass.getText().toString().equals("")) {
                    newPass.setError("Field should not be empty.");
                } else if (confrmPass.getText().toString().equals("")) {
                    confrmPass.setError("Field should not be empty.");
                } else if (!oldPass.getText().toString()
                        .equals(myPrefs.getPassword())
                        && !oldPass.getText().toString()
                        .equals(AppConstants.DEFAULT_PASSWORD)) {
                    oldPass.setText("");
                    newPass.setText("");
                    confrmPass.setText("");
                    oldPass.setError("Old password is wrong.");
                } else if (!confrmPass.getText().toString()
                        .equals(newPass.getText().toString())) {
                    newPass.setText("");
                    confrmPass.setText("");
                    confrmPass.setError("Password did not match.");
                } else {
                    myPrefs.setPassword(newPass.getText().toString());
                    Toast.makeText(getActivity(),
                            "Password successfully changed.", Toast.LENGTH_LONG).show();
                    oldPass.setText("");
                    newPass.setText("");
                    confrmPass.setText("");
                    dialog.dismiss();
                }

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(handler1!=null){
            handler1.removeCallbacks(mShowFullPageAdTask);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler1.postDelayed(mShowFullPageAdTask, 3 * 1000);
    }
}
