package com.infusiblecoder.cryptotracker.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.infusiblecoder.cryptotracker.R;

public class AdsManager {

    //TODO change this if you want to limit interstishal ads
    public static int NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION = 10;
    //TODO dont touch this
    public static int NUMBER_OF_INTERSTISHAL_ADS_SHOWN = 0;


    //TODO change this if you want to limit banner ads
    public static int NUMBER_OF_BANNER_ADS_PER_SESSION = 5;
    //TODO dont touch this
    public static int NUMBER_OF_BANNER_ADS_SHOWN = 0;


    private static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private static String nn = "nnn";

    public static void loadInterstitialAd(final Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
                Context.MODE_PRIVATE);
        nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.

        if (nn.equals("nnn")) {

            if (Utils.Companion.getShow_Ads()) {
                loadAdmobInterstitialAd(activity);
            }
        }

    }


    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer) {

        SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
                Context.MODE_PRIVATE);
        nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.

        if (nn.equals("nnn")) {

            if (Utils.Companion.getShow_Ads()) {

                if (AdsManager.NUMBER_OF_BANNER_ADS_SHOWN < AdsManager.NUMBER_OF_BANNER_ADS_PER_SESSION) {
                    AdsManager.NUMBER_OF_BANNER_ADS_SHOWN++;

                    com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(activity);
                    adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
                    adView.setAdUnitId(activity.getResources().getString(R.string.AdmobBanner));
                    adView.loadAd(new AdRequest.Builder().build());
                    adContainer.addView(adView);

                } else {
                    adContainer.setVisibility(View.GONE);
                }

            }else {
                adContainer.setVisibility(View.GONE);
            }
        }
    }


    public static void loadAdmobInterstitialAd(final Context context) {


        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(context, context.getString(R.string.AdmobInterstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                if (AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN < AdsManager.NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION) {
                    mInterstitialAd = interstitialAd;
                    if (mInterstitialAd != null) {
                        AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN++;
                        mInterstitialAd.show((Activity) context);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });


    }
}
