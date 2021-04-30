package com.example.adsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ITEMS_PER_AD = 6;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/4177191030";
    private RecyclerView recyclerView;
    private List<Object> recyclerViewItems = new ArrayList<>();
    String[] str = {"Android_1", "Android_2", "Android_3", "Android_4", "Android_5", "Android_6", "Android_7",
            "Android_8", "Android_9", "Android_10", "Android_11", "Android_12", "Android_13", "Android_14",
            "Android_15", "Android_16", "Android_17", "Android_18", "Android_19", "Android_20",
            "Android_21", "Android_22", "Android_23", "Android_24", "Android_25"};

    private static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    Intent intent;
    InterstitialAd interstitialAd;
    Boolean isInterstial = false, isReward = false;

    private static final String REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private RewardedAd rewardedAd;
    boolean isLoading;
    Activity activity;
    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        loadAd();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        for (int i = 0; i < str.length; i++) {
            ModelClass modelClass = new ModelClass();
            modelClass.setName(str[i]);
            recyclerViewItems.add(modelClass);
        }

        Log.e("TAG", "onCreate: recyclerViewItems = "+recyclerViewItems.size());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, recyclerViewItems, new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                ModelClass modelClass = (ModelClass) recyclerViewItems.get(pos);

                intent = new Intent(MainActivity.this, MainActivity2.class);
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                String KEY_CLICK_COUNT = null;
                int clickCount = prefs.getInt(KEY_CLICK_COUNT, 0);
                if (clickCount % 5 == 0) {
                    if (isReward) {
                        rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                Log.e(TAG, "The user earned the reward.");
                            }
                        });
                    } else if (isInterstial) {
                        interstitialAd.show(MainActivity.this);
                    } else {
                        Log.e(TAG, "LoadRewardedAD: Ad not load  ");
                    }
                    intent.putExtra("EXTRA_SESSION_ID", modelClass.getName());
                } else {
                    intent.putExtra("EXTRA_SESSION_ID", modelClass.getName());
                    startActivity(intent);
                }
                clickCount++;
                prefs.edit().putInt(KEY_CLICK_COUNT, clickCount).apply();
            }
        });
        recyclerView.setAdapter(adapter);

        addBannerAds();
        loadBannerAds();
    }

    public void loadAd() {
        if (!isReward) {
            LoadRewardedAD();
        }
        if (!isInterstial) {
            interstitial();
        }
    }

    @Override
    protected void onResume() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();

    }

    private void addBannerAds() {
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(MainActivity.this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AD_UNIT_ID);
            recyclerViewItems.add(i, adView);
        }
    }

    private void loadBannerAds() {
        loadBannerAd(0);
    }

    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad" + " ad.");
        }

        final AdView adView = (AdView) item;

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        adView.loadAd(new AdRequest.Builder().build());
    }

    private void LoadRewardedAD() {

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, REWARDED_UNIT_ID, adRequest, new RewardedAdLoadCallback() {

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                isReward = true;
                MainActivity.this.rewardedAd = rewardedAd;
                Toast.makeText(MainActivity.this, "onrewardAdLoaded()", Toast.LENGTH_SHORT).show();

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        isReward = false;
                        Log.e(TAG, "LoadRewardedAD: Ad show  ");
                        Toast.makeText(MainActivity.this, "onAdShowedFullScreenContent",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        loadAd();
                        isReward = false;
                        Log.e(TAG, "LoadRewardedAD: AdError  " + adError);
                        Toast.makeText(MainActivity.this, "onAdFailedToShowFullScreenContent",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadAd();
                        isReward = false;
                        Log.e(TAG, "LoadRewardedAD: adclose  ");
                        Toast.makeText(MainActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });
//                Activity activityContext = MainActivity.this;
//                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
//                    @Override
//                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                        Log.e(Tag, "LoadRewardedAD: earn reward  ");
//                    }
//                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                loadAd();
                isReward = false;
                Log.e(TAG, "LoadRewardedAD: loadAdError  ");
                rewardedAd = null;
                MainActivity.this.isLoading = false;
                Toast.makeText(MainActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void interstitial() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, INTERSTITIAL_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                isInterstial = true;
                MainActivity.this.interstitialAd = interstitialAd;
                Toast.makeText(MainActivity.this, "onInterstitialAdLoaded()", Toast.LENGTH_SHORT).show();

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadAd();
                        Log.e(TAG, "interstitial: AdDismissed  ");
                        isInterstial = false;
                        MainActivity.this.interstitialAd = null;
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "onAdClosed()", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        loadAd();
                        Log.e(TAG, "interstitial: AdFailed  ");
                        isInterstial = false;
                        MainActivity.this.interstitialAd = null;
                        Toast.makeText(MainActivity.this, "onAdFailedToShoeFullScreen()", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.e(TAG, "interstitial: AdShow  ");
                        isInterstial = false;
                        Toast.makeText(MainActivity.this, "onAdShowFullScreen()", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                loadAd();
                Log.e(TAG, "interstitial: AdFailedToLoad  ");
                interstitialAd = null;
                isInterstial = false;

                String error = String.format("domain: %s, code: %d, message: %s",
                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                Toast.makeText(MainActivity.this, "onAdFailedToLoad() with error: "
                        + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}