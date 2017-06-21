package com.scanlibrary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class MyPrefs {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    private final String DATABASE_NAME = "CamScannerPrefs";
    private final String DOC_COUNT = "docCount";
    private String IS_REGISTER = "isRegister";
    private String PASSWORD = "password";
    private String SECURITY_QUESTION = "securityQuestion";
    private String SEQURITY_ANSWER = "securityAnswer";
    private String TOG_CHECKED = "togchecked";
    private String FIRST_TIME = "firsttime";
    private String INPUT_TYPE_ALPHA_NUMERIC = "isInputTypeAlphaNumeric";
    private final String POLICY_READ = "policyRead";
    private final String ADDMOB = "addmob";
    private final String ADDBUDDIES = "addbuddies";
    private final String ADDBANNER = "addbanner";
    private final String PROMOTEDAPPIMAGE="promotedappimage";
    private final String PROMOTEDAPPDESC="promotedappdesc";
    private final String PROMOTEDAPPURL="promotedappurl";
    private final String SHOWBACKADS="showbackads";
    private final String BACKCAMPAIGNAPPID="backCampaignAppId";
    public MyPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }


    public String getAddmob() {
        return sharedPreferences.getString(ADDMOB, "ca-app-pub-3223616616608757/5685909026");
    }

    public void setAddmob(String addmob) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(ADDMOB, addmob);
        spEditor.commit();
    }


    public String getAddbuddies() {
        return sharedPreferences.getString(ADDBUDDIES, "5651fe0d-00b3-422b-81f4-7e26d38853b3");
    }

    public void setAddbuddies(String addbuddies) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(ADDBUDDIES, addbuddies);
        spEditor.commit();
    }


    public String getAddbanner() {
        return sharedPreferences.getString(ADDBANNER, "ca-app-pub-3223616616608757/4209175821");
    }

    public void setAddbanner(String addbanner) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(ADDBANNER, addbanner);
        spEditor.commit();
    }

    public int getDocCount() {
        return sharedPreferences.getInt(DOC_COUNT, 1);
    }

    public void setDocCount(int docCount) {
        spEditor = sharedPreferences.edit();
        spEditor.putInt(DOC_COUNT, docCount);
        spEditor.commit();
    }

    public boolean isRegister() {
        return sharedPreferences.getBoolean(IS_REGISTER, false);
    }

    public void setRegister(boolean isRegister) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(IS_REGISTER, isRegister);
        spEditor.commit();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(FIRST_TIME, true);
    }

    public void setFirstTime(boolean firstTime) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(FIRST_TIME, firstTime);
        spEditor.commit();
    }

    public boolean isTogChecked() {
        return sharedPreferences.getBoolean(TOG_CHECKED, false);
    }

    public void setTogChecked(boolean togChecked) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(TOG_CHECKED, togChecked);
        spEditor.commit();
    }

    public boolean isInputTypeAlphaNumeric() {
        return sharedPreferences.getBoolean(INPUT_TYPE_ALPHA_NUMERIC, false);
    }

    public void setInputTypeAlphaNumeric(boolean inputTypeAlphaNumeric) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(INPUT_TYPE_ALPHA_NUMERIC, inputTypeAlphaNumeric);
        spEditor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, "");
    }

    public void setPassword(String password) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(PASSWORD, password);
        spEditor.commit();
    }

    public String getQuestion() {
        return sharedPreferences.getString(SECURITY_QUESTION, "");
    }

    public void setQuestion(String question) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SECURITY_QUESTION, question);
        spEditor.commit();
    }

    public String getAnswer() {
        return sharedPreferences.getString(SEQURITY_ANSWER, "");
    }

    public void setAnswer(String answer) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SEQURITY_ANSWER, answer);
        spEditor.commit();
    }

    public boolean isPolicyRead() {
        return sharedPreferences.getBoolean(POLICY_READ, false);
    }

    public void setPolicyRead(boolean policyRead) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(POLICY_READ, policyRead);
        spEditor.commit();
    }

    public String getShowbackads() {
        return sharedPreferences.getString(SHOWBACKADS, "");
    }

    public void setShowbackads(String showbackads) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SHOWBACKADS, showbackads);
        spEditor.commit();
    }
    public String getPromotedappimage() {
        return sharedPreferences.getString(PROMOTEDAPPIMAGE, "http://mpaisa.info/images/bannercallrecorder.png");
    }

    public void setPromotedappimage(String promotedappimage) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(PROMOTEDAPPIMAGE, promotedappimage);
        spEditor.commit();
    }
    public String getPromotedappdesc() {
        return sharedPreferences.getString(PROMOTEDAPPDESC, "For recording your calls,Download the app Now!");

    }

    public void setPromotedappdesc(String promotedappdesc) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(PROMOTEDAPPDESC, promotedappdesc);
        spEditor.commit();
    }

    public String getPromotedappurl() {
        return sharedPreferences.getString(PROMOTEDAPPURL, "https://play.google.com/store/apps/details?id=com.mimi.callrecorder&hl=en");

    }

    public void setPromotedappurl(String promotedappurl) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(PROMOTEDAPPURL, promotedappurl);
        spEditor.commit();
    }

    public String getBackCampaignAppId() {
        return sharedPreferences.getString(BACKCAMPAIGNAPPID, "1");

    }

    public void setBackCampaignAppId(String backCampaignAppId) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(BACKCAMPAIGNAPPID, backCampaignAppId);
        spEditor.commit();
    }
}
