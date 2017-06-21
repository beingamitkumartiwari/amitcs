package com.amtee.camscanner.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amtee.camscanner.R;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by DEVEN SINGH on 1/13/2015.
 */

public class AboutFragment extends Fragment {


    private InterstitialAd mInterstitialAd;
    private Handler handler1 = new Handler();
    MyPrefs myPrefs;
    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initViews(){
        myPrefs=new MyPrefs(getActivity());
        adMobFullPageAd();
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
    public void onResume() {
        super.onResume();
        handler1.postDelayed(mShowFullPageAdTask, 3 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(handler1!=null){
            handler1.removeCallbacks(mShowFullPageAdTask);
        }
    }
}
