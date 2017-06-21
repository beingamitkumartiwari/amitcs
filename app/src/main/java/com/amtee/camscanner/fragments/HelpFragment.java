package com.amtee.camscanner.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amtee.camscanner.R;
import com.amtee.camscanner.utilities.extra_utils.MyPrefs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


/**
 * Created by DEVEN SINGH on 1/13/2015.
 */

public class HelpFragment extends Fragment {
    MyPrefs myPrefs;
    LinearLayout myAdd;

    public HelpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        adMobBannerAd(rootView);
        return rootView;
    }

    private void adMobBannerAd(View view) {
        myPrefs = new MyPrefs(getActivity());
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
}
