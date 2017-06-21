package com.amtee.camscanner.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amtee.camscanner.R;
import com.amtee.camscanner.custom_adapters.DocCreationListAdapter;
import com.amtee.camscanner.model_classes.MyDocItems;
import com.amtee.camscanner.utilities.ConnectionCheck;
import com.amtee.camscanner.utilities.cache.ImageLoader;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.CustomProgressDialog;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.amtee.camscanner.utilities.pdf_utils.PdfCreator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

/**
 * Created by DEVEN SINGH on 1/31/2015.
 */
public class PdfDocumentCreationActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    ListView docList;
    DocCreationListAdapter docCreationListAdapter;
    Bundle extras;
    MyDocItems myDocItems;
    String[] imagePath;
    ImageButton pdfCreatBt;
    CustomProgressDialog customProgressDialog;
    CamScannerDatabase dbHelper;
    String pdfDocName;
    private InterstitialAd interstitialAd;
    AdRequest adRequest;
    RelativeLayout myAppLayoutl;
    ProgressBar progressBar;
    MyPrefs myPrefs;
    private Handler addMobHandler = new Handler();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_creation);
         myPrefs = new MyPrefs(getApplicationContext());
        dbHelper = new CamScannerDatabase(PdfDocumentCreationActivity.this);
        myDocItems = new MyDocItems();
        extras = getIntent().getExtras();
        if (extras != null) {
            pdfDocName = extras.getString("docPath");
            myDocItems.listOfMyDoc = dbHelper.getAllDocsForDocCreation(pdfDocName);
        }
        initPalletes();
        docCreationListAdapter = new DocCreationListAdapter(PdfDocumentCreationActivity.this, myDocItems.listOfMyDoc);
        docList.setAdapter(docCreationListAdapter);
        customProgressDialog = new CustomProgressDialog(PdfDocumentCreationActivity.this, R.mipmap.progress_img);

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
        addMobHandler.postDelayed(mShowFullPageAdTask, 8 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        addMobHandler.removeCallbacks(mShowFullPageAdTask);

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
        docList = (ListView) findViewById(R.id.listViewDoc);
        docList.setOnItemClickListener(this);
        pdfCreatBt = (ImageButton) findViewById(R.id.creatPdfButton);
        pdfCreatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Amtee_CamScanner/Pdf_Doc" + File.separator + pdfDocName + ".pdf");
                if (file.exists()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        startActivity(intent);
                    } catch (ActivityNotFoundException activityNotFoundException) {
                        Toast.makeText(PdfDocumentCreationActivity.this, "There is no app present to open pdf", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                } else {
                    imagePath = new String[myDocItems.listOfMyDoc.size()];
                    for (int i = 0; i < myDocItems.listOfMyDoc.size(); i++) {
                        final MyDocItems item = myDocItems.listOfMyDoc.get(i);
                        imagePath[i] = item.getMyDocImgPathName();
                    }
                    new PdfCreationTask(pdfDocName).execute(imagePath);
                }
            }
        });
        docList.setOnScrollListener(mScrollListener);
    }
     AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
         @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    docCreationListAdapter.setFlagBusy(true);
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    docCreationListAdapter.setFlagBusy(false);
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    docCreationListAdapter.setFlagBusy(false);
                    break;
                default:
                    break;
            }
            docCreationListAdapter.notifyDataSetChanged();
        }
         @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        }
    };
     @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        imagePath = new String[myDocItems.listOfMyDoc.size()];
        final MyDocItems item = myDocItems.listOfMyDoc.get(position);
        imagePath[position] = item.getMyDocImgPathName();
        Intent intent = new Intent(getApplicationContext(), SingleItemClick.class);
        intent.putExtra("keyPosition", imagePath[position]);
        startActivity(intent);
    }
     private class PdfCreationTask extends AsyncTask<String, Void, String> {
         String pdfDocumentNAme;

        PdfCreationTask(String pdfDocumentNAme) {
            this.pdfDocumentNAme = pdfDocumentNAme;
        }
         @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }
         @Override
        protected String doInBackground(String... params) {
             return new PdfCreator(PdfDocumentCreationActivity.this).createPdf(params, pdfDocumentNAme + ".pdf");
        }
         @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            customProgressDialog.dismiss();
            try {
                File pdfFile = new File(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                startActivity(intent);
            } catch (ActivityNotFoundException activityNotFoundException) {
                Toast.makeText(PdfDocumentCreationActivity.this, "There is no app present to open pdf", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        }
    }
     @Override
    protected void onDestroy() {
        ImageLoader imageLoader = docCreationListAdapter.getImageLoader();
        if (imageLoader != null) {
            imageLoader.clearCache();
        }
        super.onDestroy();
    }
 }
