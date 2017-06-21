package com.amtee.camscanner.utilities.camera_utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.activities.drawer_activity.MainActivity;
import com.amtee.camscanner.activities.PdfDocumentCreationActivity;
import com.amtee.camscanner.utilities.ConnectionCheck;
import com.amtee.camscanner.utilities.DateAndTime;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.CustomProgressDialog;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
//import com.amtee.camscanner.volley_works.ConnectionCheck;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class ImageEditor extends Activity implements View.OnClickListener, PopupWindow.OnDismissListener {
    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GridViewDemo/";
    private static final String SHOWCASE_ID = "showcasei";
    ImageView editImage;
    Bundle extras;

    LinearLayout acceptImg;
    int dpWidth;
    int dpHeight;

    AdRequest adRequest;
    InterstitialAd interstitialAd;
    private Handler handler1 = new Handler();
    LinearLayout bannerAd;
    Bitmap croppedImageBitmap;
    CustomProgressDialog customProgressDialog;
    File dir = new File(Environment.getExternalStorageDirectory(),
            "/Amtee_CamScanner");
    MyPrefs myPrefs;
    CamScannerDatabase dbHelper;
    ProgressBar progressBar;
    private Uri picUri;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<String> listOfImagesPath;
    private static final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        myPrefs = new MyPrefs(ImageEditor.this);
        dbHelper = new CamScannerDatabase(ImageEditor.this);

        extras = getIntent().getExtras();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels;
        dpHeight = displayMetrics.heightPixels;
        initPalletes();

        customProgressDialog = new CustomProgressDialog(ImageEditor.this, R.mipmap.progress_img);

        listOfImagesPath = null;
        listOfImagesPath = RetriveCapturedImagePath();
        connectionCheck();
        adMobFullPageAd();
    }

        private void connectionCheck() {
        ConnectionCheck connectionCheck = new ConnectionCheck(getApplicationContext());
        interstitialAd = new InterstitialAd(getApplicationContext());
        if (connectionCheck.isConnectionAvailable()) {
            interstitialAd.setAdUnitId(myPrefs.getAddmob());
            adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
            addAdmobAdListner();
//            adMobBannerAd();
        }
    }

    private void addAdmobAdListner() {
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }

//    private void adMobBannerAd() {
//        bannerAd = (LinearLayout) findViewById(R.id.myAdd);
//        final AdView adView = new AdView(this);
//        adView.setAdUnitId(myprefs.getAddbanner());
//        adView.setAdSize(AdSize.BANNER);
//        bannerAd.addView(adView);
//        final AdListener listener = new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                adView.setVisibility(View.VISIBLE);
//                super.onAdLoaded();
//            }
//        };
//        adView.setAdListener(listener);
//        AdRequest adRequest1 = new AdRequest.Builder().build();
//        adView.loadAd(adRequest1);
//    }

    Runnable mShowFullPageAdTask = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (interstitialAd.isLoaded())
                        interstitialAd.show();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler1.postDelayed(mShowFullPageAdTask, 15 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler1.removeCallbacks(mShowFullPageAdTask);

    }

    private void adMobFullPageAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId((myPrefs.getAddmob()));
        requestNewInterstitial();
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }

    private void requestNewInterstitial() {
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }
    private void initPalletes() {
//        progressBar = (ProgressBar) findViewById(R.id.progressbar_ocr);
        editImage = (ImageView) findViewById(R.id.editImage);
        acceptImg = (LinearLayout) findViewById(R.id.accept);
        acceptImg.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
        editImage.setImageBitmap(bmp );
        System.out.println("dinesh"+editImage);
        setCroppedImageToEditImageView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                getFileName();
                break;
//            case R.id.cancel:
//                Intent intent=new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                break;

        }
    }

    private class AsynctaskOcr extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String filename = "bitmap.png";
            try {
                //Write file
                FileOutputStream stream = ImageEditor.this.openFileOutput(filename, Context.MODE_PRIVATE);
                croppedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                stream.close();
//                    croppedImageBitmap.recycle();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return filename;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
//            Intent in1 = new Intent(ImageEditor.this, OcrTextGetActivity.class);
//            in1.putExtra("image", s);
//            System.out.println("dineshs"+ s);
//            startActivity(in1);
        }
    }

    private void getFileName() {
        final Dialog dialog = new Dialog(ImageEditor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_doc);
        final EditText newDocName = (EditText) dialog.findViewById(R.id.newDocName);
        newDocName.setText("New Doc " + myPrefs.getDocCount());
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
                dialog.dismiss();
                new SaveEditedImageTask().execute(newDocName.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private Bitmap getBitmapFromImage() {
        return ((BitmapDrawable) editImage.getDrawable()).getBitmap();
    }

    BitmapWorkerTask bitmapWorkerTask;


    private Bitmap changeBitmapContrastBrightness(Bitmap croppedImageBitmap, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(croppedImageBitmap.getWidth(), croppedImageBitmap.getHeight(), croppedImageBitmap.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(croppedImageBitmap, 0, 0, paint);

        return ret;
    }


    private void setCroppedImageToEditImageView() {
//        Bundle extras = getIntent().getExtras();
//        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
//         editImage.setImageBitmap(bmp );
//        File f = new File("/sdcard/Amtee_CamScanner/myPicName.jpg");
//        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
////        grid.setVisibility(View.VISIBLE);
//        editImage.setImageBitmap(bmp);
    }


    @Override
    public void onDismiss() {

    }

    class BitmapWorkerTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private float contrast = 0.0f;
        private float brightness = 0.0f;

        public BitmapWorkerTask(float contrast, float brightness) {
            this.contrast = contrast;
            this.brightness = brightness;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            return changeBitmapContrastBrightness(params[0], contrast, brightness);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                editImage.setImageBitmap(bitmap);
            }
        }
    }

    class SaveEditedImageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            return saveImageBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(String docFileName) {
            super.onPostExecute(docFileName);
            customProgressDialog.dismiss();
            Toast.makeText(ImageEditor.this, "Image saved", Toast.LENGTH_SHORT).show();
            Intent intentDocCreation = new Intent(ImageEditor.this, PdfDocumentCreationActivity.class);
            intentDocCreation.putExtra("docPath", docFileName);
            startActivity(intentDocCreation);
            myPrefs.setDocCount(myPrefs.getDocCount() + 1);
            finish();
        }
    }

    private String saveImageBitmap(String name) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File imgFolder = new File(dir.getAbsolutePath(), "/.Images");
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        // String name=getFileName();
        String path = imgFolder + File.separator + name + ".jpg";
        try {
            FileOutputStream out = new FileOutputStream(path);
            getBitmapFromImage().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dbHelper.insertDoc(name, path, new DateAndTime().DateNTime(), "")) {
            System.out.println("image saved in db " + name);
        }
        return name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private List<String> RetriveCapturedImagePath() {
        List<String> tFileList = new ArrayList<String>();
        File f = new File(GridViewDemo_ImagePath);
        if (f.exists()) {
            File[] files = f.listFiles();
            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory())
                    continue;
                tFileList.add(file.getPath());
            }
        }
        return tFileList;
    }

}