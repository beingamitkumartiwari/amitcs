package com.amtee.camscanner.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;
import com.amtee.camscanner.utilities.extra_utils.AppConstants;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;


public class LoginPageActivity extends Activity {

	MyPrefs myPrefs;
	EditText password;
	Button ok;
	Button forgotPassword;
	CheckBox inputType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		myPrefs = new MyPrefs(this);
		initializePalletes();
		if (myPrefs.isInputTypeAlphaNumeric()) {
			inputType.setChecked(true);
			password.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			inputType.setChecked(false);
			password.setInputType(InputType.TYPE_CLASS_NUMBER);
			password.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}

		inputType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					myPrefs.setInputTypeAlphaNumeric(true);
					password.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				} else {
					myPrefs.setInputTypeAlphaNumeric(false);
					password.setInputType(InputType.TYPE_CLASS_NUMBER);
					password.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginUser();
			}
		});
		forgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				completeActionUsing();
			}
		});
	}

	private void initializePalletes() {
		password = (EditText) findViewById(R.id.passcode_login);
		ok = (Button) findViewById(R.id.passcode_ok);
		forgotPassword = (Button) findViewById(R.id.forgt_psswrd);
		inputType = (CheckBox) findViewById(R.id.input_type1);
	}

//	private void loginUser() {
//		if (password.getText().toString().equals("")) {
//			password.setError("Must enter password.");
//		} else if (password.getText().toString()
//				.equals(myPrefs.getPassword().trim())
//				|| password.getText().toString()
//						.equals(AppConstants.DEFAULT_PASSWORD)) {
//			finish();
//			Intent intentMainAct = new Intent(getApplicationContext(),
//					MainActivity.class);
//			startActivity(intentMainAct);
//		} else {
//			password.setText("");
//			password.setError("You entered wrong password.");
//		}
//	}

	private void loginUser() {
		if (password.getText().toString().equals("")) {
			password.setError("Must enter password.");
		} else if (password.getText().toString()
				.equals(myPrefs.getPassword().trim())) {
			finish();
			Intent intentMainAct = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intentMainAct);
		}else if(password.getText().toString().equals(AppConstants.DEFAULT_PASSWORD)){

			myPrefs.setPassword("");
			myPrefs.setAnswer("");
			myPrefs.setQuestion("");
			Intent intentRegistration = new Intent(LoginPageActivity.this, RegistrationActivity.class);
			intentRegistration.putExtra("key1","defaultpassword");
			startActivity(intentRegistration);
			finish();
		}
		else {
			password.setText("");
			password.setError("You entered wrong password.");
		}
	}
	private void completeActionUsing() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_action_using);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.show();
		Button secure = (Button) dialog.findViewById(R.id.secure_q);
		Button emailSupport = (Button) dialog.findViewById(R.id.gmail_using);
		secure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				securityDialog();
			}
		});
		emailSupport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "support@mtechnovation.com" });
				email.putExtra(Intent.EXTRA_SUBJECT, "Forgot Password Request.");
				email.putExtra(
						Intent.EXTRA_TEXT,
						Html.fromHtml("Dear Team,"
								+ "<br/>"+ "<br/>"
								+ "I have forgot my access password of Cam Scanner app. Request you to send me a new one."
								+ "<br/>" + "<br/>" + "Thanks"));
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email,
						"Choose an Email client:"));
			}
		});

	}

	private void securityDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_security);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.show();
		TextView secQues = (TextView) dialog.findViewById(R.id.sec_question);
		secQues.setText(myPrefs.getQuestion());
		Button ok = (Button) dialog.findViewById(R.id.confirm_ans);
		final EditText ans = (EditText) dialog.findViewById(R.id.sec_answer);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ans.getText().toString().equals("")) {
					ans.setError("This is not a valid answer!");
				} else if (ans.getText().toString()
						.equals(myPrefs.getAnswer())) {
					dialog.dismiss();
					Toast.makeText(getApplicationContext(),
							"Your Password is: " + myPrefs.getPassword(),
							Toast.LENGTH_LONG).show();
				} else {
					ans.setError("Try again! you entered wrong answer.");
				}
			}
		});
	}


}
