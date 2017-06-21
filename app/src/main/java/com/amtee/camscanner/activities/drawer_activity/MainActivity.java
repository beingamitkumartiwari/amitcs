package com.amtee.camscanner.activities.drawer_activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amtee.camscanner.R;
import com.amtee.camscanner.custom_adapters.DrawerListAdapter;
import com.amtee.camscanner.fragments.AboutFragment;
import com.amtee.camscanner.fragments.HelpFragment;
import com.amtee.camscanner.fragments.MyDocFragment;
import com.amtee.camscanner.fragments.SettingsFragment;
import com.amtee.camscanner.model_classes.DrawerItem;
import com.amtee.camscanner.utilities.ConnectionCheck;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.amtee.camscanner.volley_works.ApplicationFL;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressLint("ResourceType")
public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuTitles;
    private TypedArray menuIcons;
    final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1;
    private ArrayList<DrawerItem> drawerItems;
    private DrawerListAdapter adapter;
    Fragment fragment;
    FragmentManager fragmentManager;
    private InterstitialAd interstitialAd1;
    AdRequest adRequest1;
    RelativeLayout myAppLayoutl;
    ProgressBar progressBar;
    MyPrefs myPrefs;
    private Handler addMobHandler = new Handler();
    AlertDialog alertDialog;
    View view;
    RelativeLayout rl_promotionback;
    ImageView iv_promotedappimage;
    TextView tv_promotedappdesc;
    Button bt_yes;
    Button bt_no;
    String playstorelink;
    String backcampid;
    private final static int BUFFER_SIZE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mTitle = mDrawerTitle = getTitle();
        myPrefs = new MyPrefs(getApplicationContext());
        menuTitles = getResources().getStringArray(R.array.drawer_items);
        menuIcons = getResources().obtainTypedArray(R.array.drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        drawerItems = new ArrayList<DrawerItem>();
        drawerItems.add(new DrawerItem(menuTitles[0], menuIcons.getResourceId(0, -1), true, "" + new CamScannerDatabase(MainActivity.this).numberOfRows()));
        drawerItems.add(new DrawerItem(menuTitles[1], menuIcons.getResourceId(1, -1)));
        drawerItems.add(new DrawerItem(menuTitles[2], menuIcons.getResourceId(2, -1)));
        drawerItems.add(new DrawerItem(menuTitles[3], menuIcons.getResourceId(3, -1)));
        drawerItems.add(new DrawerItem(menuTitles[4], menuIcons.getResourceId(4, -1)));
        menuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new DrawerListAdapter(getApplicationContext(),
                drawerItems);
        mDrawerList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            displayView(0);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        myAppLayoutl = (RelativeLayout) findViewById(R.id.myAppLayout);
        ConnectionCheck connectionCheck = new ConnectionCheck(getApplicationContext());
        interstitialAd1 = new InterstitialAd(getApplicationContext());
        if (connectionCheck.isConnectionAvailable()) {
            interstitialAd1.setAdUnitId(myPrefs.getAddmob());
            adRequest1 = new AdRequest.Builder().build();
            interstitialAd1.loadAd(adRequest1);
            addAdmobAdListner1();
            addMobHandler.postDelayed(mAddIsLoaded, 8 * 1000);
        } else {
            myAppLayoutl.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
        setimageForBack();
    }

    private void setimageForBack() {
        alertDialog = new AlertDialog.Builder(this).create();
        view = getLayoutInflater().inflate(R.layout.exit_dialog, null);
        alertDialog.setView(view);
        tv_promotedappdesc = (TextView) view.findViewById(R.id.textview_promoteddappdesc);
        iv_promotedappimage = (ImageView) view.findViewById(R.id.imageview_promotedappimage);
        rl_promotionback = (RelativeLayout) view.findViewById(R.id.relative_promotionback);
        bt_yes = (Button) view.findViewById(R.id.button_yes);
        bt_no = (Button) view.findViewById(R.id.button_no);
        ImageLoader m = ApplicationFL.getInstance().getImageLoader();
        m.get(myPrefs.getPromotedappimage(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                iv_promotedappimage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        playstorelink = myPrefs.getPromotedappurl();
        backcampid = myPrefs.getBackCampaignAppId();
        tv_promotedappdesc.setText(myPrefs.getPromotedappdesc());
    }

    Runnable mAddIsLoaded = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!interstitialAd1.isLoaded()) {
                        myAppLayoutl.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    private void addAdmobAdListner1() {
        interstitialAd1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd1.isLoaded()) {
                    interstitialAd1.show();
                } else {
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                myAppLayoutl.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                myAppLayoutl.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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

    @TargetApi(Build.VERSION_CODES.M)
    private void permissionCheck() {
        int hasCamera = checkSelfPermission(Manifest.permission.CAMERA);
        int hasWriteExter = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> permissions = new ArrayList<String>();
        if (hasCamera != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (hasWriteExter != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            savefile();
                        }
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionCheck();
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.rate_us) {
            Intent intentRate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.amtee.camscanner"));
            startActivity(intentRate);
            return true;
        }
        if (id == R.id.more_apps) {
            Intent intentMoreApp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AMTEE+Apps"));
            startActivity(intentMoreApp);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        addMobHandler.removeCallbacks(mAddIsLoaded);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.more_apps).setVisible(!drawerOpen);
        menu.findItem(R.id.rate_us).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                fragment = new MyDocFragment();
                break;
            case 1:
                fragment = new SettingsFragment();
                break;
            case 2:
                fragment = new HelpFragment();
                break;
            case 3:
                fragment = new AboutFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(menuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        } else {
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position == 4) {
                mDrawerLayout.closeDrawer(mDrawerList);
                alertExit();
            } else {
                displayView(position);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            alertExit();
        }
    }

    private void savefile() {
        try {
            AssetManager assetFiles = getAssets();
            InputStream in = null;
            OutputStream out = null;
            in = assetFiles.open("eng.traineddata");

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/ocrsample/tessdata");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "eng.traineddata");
            out = new FileOutputStream(file);
            copyAssetFiles(in, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void copyAssetFiles(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertExit() {
        if (myPrefs.getShowbackads().equals("0") || !new ConnectionCheck(MainActivity.this).isConnectionAvailable()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog));
            alertDialogBuilder.setTitle("Cam Scanner");
            alertDialogBuilder.setMessage("Do you want to exit ?")
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNeutralButton("More apps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent iMoreApps = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AMTEE+Apps"));
                            startActivity(iMoreApps);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            iv_promotedappimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = "http://mpaisa.info/MtechAppsPromotion.asmx/exitappcounter?appName=CamScanner&appid=" + backcampid;
                    Intent campaignApps = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(campaignApps);
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                    finish();
                }
            });
            bt_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
