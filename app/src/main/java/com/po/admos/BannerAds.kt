package com.po.admos

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

@Composable
fun BannerAds(
    modifier: Modifier,
    adListener: AdListener? = null
) {
    val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp
    val padding = 1
    var i by remember { mutableStateOf(0) }
    var containerWidth by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val adUnitId = "ca-app-pub-3940256099942544/6300978111"
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(color = Color.White)
            .onSizeChanged {
                containerWidth = it.width
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = {
            AdView(it)
        }, modifier = modifier, update = {
            if (it.adSize == null)
                it.setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        deviceCurrentWidth - padding * 2
                    )
                )
            try {
                it.adUnitId = adUnitId //Can only be set once
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (adListener != null)
                it.adListener = adListener

            it.loadAd(AdRequest.Builder().build())
        })
    }
}


