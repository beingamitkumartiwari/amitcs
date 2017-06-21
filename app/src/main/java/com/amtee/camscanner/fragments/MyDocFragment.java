package com.amtee.camscanner.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.PdfDocumentCreationActivity;
import com.amtee.camscanner.activities.PicCaptureActivity;
import com.amtee.camscanner.activities.PrivacyPolicyActivity;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;
import com.amtee.camscanner.custom_adapters.MyDocsAdapter;
import com.amtee.camscanner.listners.AdapterListner;
import com.amtee.camscanner.model_classes.MyDocItems;
import com.amtee.camscanner.utilities.DeleteDocAsyncTask;
import com.amtee.camscanner.utilities.MergeDocsAsyncTask;
import com.amtee.camscanner.utilities.SharePdfFileAsyncTask;
import com.amtee.camscanner.utilities.cache.ImageLoader;
import com.amtee.camscanner.utilities.camera_utils.ImageEditor;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.amtee.camscanner.volley_works.ApplicationFL;
import com.amtee.camscanner.volley_works.CustomJsonObjectRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MyDocFragment extends Fragment implements View.OnClickListener, AdapterListner {
    String urlJsonArray = "http://www.mpaisa.info/MtechAppsPromotion.asmx/appsAppId?appName=CamScanner";
    CheckBox checkMerge;

    public MyDocFragment() {
    }

    private static final int REQUEST_CODE = 99;
    AdView adView;
    ImageButton captureButton;
    ImageButton checkButton;
    ImageButton galleryButton;
    ImageButton shareDocBt;
    ImageButton mergeDocBt;
    ImageButton deleteDocBt, backbutton;
    ImageButton fabShare;
    LinearLayout llNewDoc;
    LinearLayout llMergeDoc;
    LinearLayout noDoc;
    int GALLERY_INTENT_CALLED = 1;
    int GALLERY_KITKAT_INTENT_CALLED = 2;
    ListView myDocList;
    MyDocsAdapter myDocsAdapter;
    MyDocItems myDocItems;
    CamScannerDatabase dbHelper;
    boolean isMultiClick;
    MyPrefs myPrefs;
    boolean isFragmentResumed;
    boolean isBackPressed;
    Dialog myDialog;
    private InterstitialAd mInterstitialAd;
    private Handler handler1 = new Handler();
    LinearLayout myAdd;
    String filePath = null;
    private ImageView scannedImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_doc, container, false);
        myPrefs = new MyPrefs(getActivity());
        initPallets(rootView);
        requestforAdMobID();
        adMobBannerAd(rootView);
        if (!myPrefs.isPolicyRead()) {
            policyAlert();
        }
        adMobFullPageAd();
        isFragmentResumed = true;
        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (myDocsAdapter.isAnySelected()) {
                    } else {
                        getActivity().onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
        return rootView;

    }

    private void requestforAdMobID() {
        ApplicationFL.getInstance().addToRequestQueue(requestforAdmob(urlJsonArray), "GETID");
    }

    private CustomJsonObjectRequest requestforAdmob(String url) {
        CustomJsonObjectRequest customJsonobjectRequest = new CustomJsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String AdBuddizFullPage = response.getString("AdBuddizFullPage");
                    myPrefs.setAddbuddies(AdBuddizFullPage);
                    String AdmobFullPage = response.getString("AdmobFullPage");
                    myPrefs.setAddmob(AdmobFullPage);
                    String BannerAd = response.getString("BannerAd");
                    myPrefs.setAddbanner(BannerAd);
                    myPrefs.setShowbackads(response.getString("ShowNativAds"));
                    if (response.getString("ShowNativAds").equals("1")) {
                        myPrefs.setBackCampaignAppId(response.getString("promotedappid"));
                        myPrefs.setPromotedappimage(response.getString("promotedappimage"));
                        myPrefs.setPromotedappdesc(response.getString("promotedappdesc"));
                        myPrefs.setPromotedappurl(response.getString("promotedappurl"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        customJsonobjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        return customJsonobjectRequest;
    }

    private void adMobBannerAd(View view) {
        myAdd = (LinearLayout) view.findViewById(R.id.myAdd);
        final AdView adView = new AdView(getActivity());
        adView.setAdUnitId(myPrefs.getAddbanner());
        adView.setAdSize(AdSize.BANNER);
        myAdd.addView(adView);
        final AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        };
        adView.setAdListener(listener);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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

    private void policyAlert() {
        myDialog = new Dialog(getActivity());
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.policy_dialog);
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final CheckBox doNotShowAgain = (CheckBox) myDialog
                .findViewById(R.id.dontshowagainpolicy);
        TextView policyRead = (TextView) myDialog.findViewById(R.id.policy_tv);
        String text = "When you click on advertisements delivered by AMTEE Apps, you will be directed to a third party’s webpage and we may pass certain of your information to the third parties operating or hosting these pages, including your email address, phone number and a list of the apps on your device. This policy will tell you what data we collect and how we use it for our games and apps. For more information on how we collect, use and share your data please read Privacy policy. If you do not wish to receive these ads delivered by AMTEE Apps you can delete the apps any time.";
        int start = text.indexOf("Privacy policy");
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new MyClickableSpan(), start, start + 14,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
        spannableString.setSpan(italicSpan, start, start + 14,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, start, start + 14,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyRead.setText(spannableString);
        policyRead.setMovementMethod(LinkMovementMethod.getInstance());

        Button accept = (Button) myDialog.findViewById(R.id.acceptPolicy);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (doNotShowAgain.isChecked()) {
                    myPrefs.setPolicyRead(true);
                }
            }
        });

        Button decline = (Button) myDialog.findViewById(R.id.declinePolicy);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                getActivity().finish();
            }
        });
    }

    private class MyClickableSpan extends ClickableSpan {
        public void onClick(View textView) {
            myDialog.dismiss();
            Intent iPolicy = new Intent(getActivity(),
                    PrivacyPolicyActivity.class);
            startActivity(iPolicy);
            getActivity().finish();
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setColor(Color.BLUE);
            textPaint.setUnderlineText(true);
        }
    }

//    private void onBackPressed(View rootView) {
//        //    rootView.setFocusableInTouchMode(true);
//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                    if (myDocsAdapter.isAnySelected()) {
//                    } else {
//                        getActivity().onBackPressed();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//    }


    private void initPallets(View rootView) {
        myPrefs = new MyPrefs(getActivity());
        dbHelper = new CamScannerDatabase(getActivity());
        myDocItems = new MyDocItems();
        myDocList = (ListView) rootView.findViewById(R.id.myDocsList);
        captureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);
        checkButton = (ImageButton) rootView.findViewById(R.id.checkBt);
        checkMerge = (CheckBox) rootView.findViewById(R.id.checkMerge);
        galleryButton = (ImageButton) rootView.findViewById(R.id.mediaButton);
        scannedImageView = (ImageView) rootView.findViewById(R.id.scannedImage);
        shareDocBt = (ImageButton) rootView.findViewById(R.id.share_doc_bt);
        mergeDocBt = (ImageButton) rootView.findViewById(R.id.merge_doc_bt);
        deleteDocBt = (ImageButton) rootView.findViewById(R.id.delete_doc_bt);
        fabShare = (ImageButton) rootView.findViewById(R.id.fabShare);
        llNewDoc = (LinearLayout) rootView.findViewById(R.id.ll_new_doc);
        llMergeDoc = (LinearLayout) rootView.findViewById(R.id.ll_merge);
        noDoc = (LinearLayout) rootView.findViewById(R.id.ll_no_doc);
        backbutton = (ImageButton) rootView.findViewById(R.id.backbutton);
        captureButton.setOnClickListener(this);
        captureButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        galleryButton.setOnClickListener(this);
        galleryButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        checkButton.setOnClickListener(this);
        shareDocBt.setOnClickListener(this);
        mergeDocBt.setOnClickListener(this);
        deleteDocBt.setOnClickListener(this);
        fabShare.setOnClickListener(this);
//        checkMerge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(checkMerge.isChecked()){
////                 if (myDocItems.listOfMyDoc.size() > 0) {
//                    llNewDoc.setVisibility(View.GONE);
//                    llMergeDoc.setVisibility(View.VISIBLE);
//                    fabShare.setVisibility(View.VISIBLE);
//                    isMultiClick = true;
//                    myDocsAdapter.setMultiplePick(true);
//                    myDocsAdapter.changeSelection(0);
//                    isBackPressed = true;
//                }
//            }
//        });
        backbutton.setOnClickListener(this);
        myDocItems.listOfMyDoc = dbHelper.getAllDocs();
        myDocsAdapter = new MyDocsAdapter(getActivity(), myDocItems.listOfMyDoc);
        myDocList.setAdapter(myDocsAdapter);
        myDocList.setOnItemLongClickListener(mItemLongClickListner);
        myDocList.setOnItemClickListener(mItemMulClickListener);
        myDocsAdapter.setMultiplePick(false);
        if (myDocItems.listOfMyDoc.size() == 0) {
            noDoc.setVisibility(View.VISIBLE);
        }
    }

    AdapterView.OnItemLongClickListener mItemLongClickListner = new AdapterView.OnItemLongClickListener() {
         @Override
        public boolean onItemLongClick(AdapterView<?> l, View view, int position, long id) {
             changeName(position);
            return true;
        }
    };

    private void changeName(final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_doc);
        final EditText newDocName = (EditText) dialog.findViewById(R.id.newDocName);
        final String docName = myDocItems.listOfMyDoc.get(position).getMyDocName();
        final String docPath = myDocItems.listOfMyDoc.get(position).getMyDocImgPathName();
        newDocName.setText("" + myDocItems.listOfMyDoc.get(position).getMyDocName());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        Button ok = (Button) dialog.findViewById(R.id.ok_bt);
        Button cancel = (Button) dialog.findViewById(R.id.cancel_bt);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDocItems.listOfMyDoc.get(position).setMyDocName(newDocName.getText().toString());
                dbHelper.updateDoc(docName, newDocName.getText().toString());
                myDocsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Toast.makeText(getActivity(), "Long Pressed" + position, Toast.LENGTH_SHORT).show();
    }

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View view, int position, long id) {
            if (isMultiClick) {
                myDocsAdapter.changeSelection(position);
            } else {
                Intent intentDocCreationActivity = new Intent(getActivity(), PdfDocumentCreationActivity.class);
                intentDocCreationActivity.putExtra("docPath", myDocsAdapter.getDocsName(position));
                startActivity(intentDocCreationActivity);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cameraButton:
                new ScanButtonClickListener(ScanConstants.OPEN_CAMERA);
//                Intent iCamera = new Intent(getActivity(), PicCaptureActivity.class);
//                startActivity(iCamera);
                break;
            case R.id.checkBt:
                if (myDocItems.listOfMyDoc.size() > 0) {
                    llNewDoc.setVisibility(View.GONE);
                    llMergeDoc.setVisibility(View.VISIBLE);
                    fabShare.setVisibility(View.GONE);
                    isMultiClick = true;
                    myDocsAdapter.setMultiplePick(true);
                    myDocsAdapter.changeSelection(0);
                    isBackPressed = true;
                } else {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                    Toast.makeText(getActivity(), "There is no document present.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.mediaButton:
                new ScanButtonClickListener(ScanConstants.OPEN_MEDIA);
//                getImageFromGallery();
                break;
            case R.id.share_doc_bt:
                if (myDocsAdapter.isAnySelected()) {
                    shareDocPdf();
                } else {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                    Toast.makeText(getActivity(), "Select at least one document.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.merge_doc_bt:
                if (myDocsAdapter.isAtleastTwoSelected()) {
                    mergeDocs();
                } else {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                    Toast.makeText(getActivity(), "Select at least two documents.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fabShare:
                if (myDocsAdapter.isAnySelected()) {
                    shareDocPdf();
                } else {
                    Toast.makeText(getActivity(), "Select at least one document.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete_doc_bt:
                if (myDocsAdapter.isAnySelected()) {
                    deleteDocs();
                } else {
                    Toast.makeText(getActivity(), "Select at least one document.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.backbutton:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
//                 myDocsAdapter.changeSelection(0);
//                llNewDoc.setVisibility(View.VISIBLE);
//                llMergeDoc.setVisibility(View.GONE);
//                llMergeDoc.setVisibility(View.GONE);

        }
    }

    private class ScanButtonClickListener implements View.OnClickListener {
        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                getActivity().getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);
                System.out.println("dinesh" + scannedImageView);

                scannedImageView.buildDrawingCache();
                Bitmap image = scannedImageView.getDrawingCache();

                Bundle extras = new Bundle();
                Intent intent = new Intent(getActivity(), ImageEditor.class);
                extras.putParcelable("imagebitmap", image);
                intent.putExtras(extras);
                startActivity(intent);
//                Bitmap bm = scannedImageView.getDrawingCache();
//                OutputStream fOut = null;
//                try {
//                    File root = new File(Environment.getExternalStorageDirectory()
//                            + File.separator + "Amtee_CamScanner" + File.separator);
//                    root.mkdirs();
//                    File sdImageMainDirectory = new File(root, "myPicName.jpg");
//                    Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
//                    fOut = new FileOutputStream(sdImageMainDirectory);
//
//                } catch (Exception e) {
////                    Toast.makeText(getActivity(), "Error occured. Please try again later.",
////                            Toast.LENGTH_SHORT).show();
//                }
//                try {
//                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//                    fOut.flush();
//                    fOut.close();
//                } catch (Exception e) {
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        Intent intent = new Intent(getActivity(), ImageEditor.class);
//        startActivity(intent);
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void deleteDocs() {
        String docsName[] = new String[myDocsAdapter.getSelected().size()];
        int i = 0;
        Iterator iterator = myDocsAdapter.getSelected().iterator();
        while (iterator.hasNext()) {
            docsName[i] = ((MyDocItems) iterator.next()).getMyDocName().toString();
            i++;
        }
        new DeleteDocAsyncTask(getActivity(), dbHelper, myDocsAdapter, docsName, this).execute();
    }

    private void mergeDocs() {
        final Dialog dialog = new Dialog(new ContextThemeWrapper(getActivity(), R.style.DevenTheme));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_doc);
        final EditText newDocName = (EditText) dialog.findViewById(R.id.newDocName);
        newDocName.setText("New Doc" + myPrefs.getDocCount());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        Button ok = (Button) dialog.findViewById(R.id.ok_bt);
        Button cancel = (Button) dialog.findViewById(R.id.cancel_bt);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new MergeDocsAsyncTask(getActivity(), dbHelper, myDocsAdapter, newDocName.getText().toString()).execute();
                myPrefs.setDocCount(myPrefs.getDocCount() + 1);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void shareDocPdf() {
        new SharePdfFileAsyncTask(getActivity(), myDocsAdapter, dbHelper).execute();
    }

    private void getImageFromGallery() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    GALLERY_INTENT_CALLED);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }


    }


//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && null != data) {
//            //String filePath = null;
//            Cursor cursor = null;
//            Uri originalUri = null;
//            if (requestCode == GALLERY_INTENT_CALLED) {
//                originalUri = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                cursor = getActivity().getContentResolver().query(originalUri,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                filePath = cursor.getString(columnIndex);
//                cursor.close();
//            } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED && data != null) {
//                originalUri = data.getData();
//                int takeFlags = data.getFlags()
//                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
//                filePath = getPath(getActivity(), originalUri);
//            }
//            Intent intent = new Intent(getActivity(),
//                    ImageEditor.class);
//            intent.putExtra("ImagePath", filePath);
//            startActivity(intent);
//        }
//    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    @Override
    public void onDestroy() {
        ImageLoader imageLoader = myDocsAdapter.getImageLoader();
        if (imageLoader != null) {
            imageLoader.clearCache();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler1.removeCallbacks(mShowFullPageAdTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentResumed) {
            refreshAdapter();
        }
        isFragmentResumed = false;
        handler1.postDelayed(mShowFullPageAdTask, 20 * 1000);
    }

    private void refreshAdapter() {
        myDocItems.listOfMyDoc.clear();
        myDocsAdapter.notifyDataSetInvalidated();
        myDocItems.listOfMyDoc = dbHelper.getAllDocs();
        myDocsAdapter = new MyDocsAdapter(getActivity(), myDocItems.listOfMyDoc);
        myDocList.setAdapter(myDocsAdapter);
        myDocsAdapter.setMultiplePick(false);
        myDocsAdapter.notifyDataSetChanged();
        llNewDoc.setVisibility(View.VISIBLE);
        llMergeDoc.setVisibility(View.GONE);
        fabShare.setVisibility(View.GONE);
        isMultiClick = false;
        if (myDocItems.listOfMyDoc.size() != 0) {
            noDoc.setVisibility(View.GONE);
        }
    }

    @Override
    public void listenerAdapter() {
        refreshAdapter();
    }
}


