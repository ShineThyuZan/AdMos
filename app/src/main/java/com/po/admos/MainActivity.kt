package com.po.admos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.po.admos.ui.theme.AdMosTheme

class MainActivity : ComponentActivity() {
    var mInterstitialAd: InterstitialAd? = null
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        setContent {
            AppContent()
        }
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppContent() {
        AdMosTheme {
            Scaffold(topBar = {
                BannerAds(modifier = Modifier.fillMaxWidth())

            }, content = {
                Modifier.padding(it)
                InterstitialAdView()
            })
        }
    }

    @Composable
    fun InterstitialAdView() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    mInterstitialAd = p0
                    mInterstitialAd?.show(this@MainActivity)
                }
            })
    }

}