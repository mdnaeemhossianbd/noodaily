package com.example.admob

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    val configuredAdUnitId = AdMobManager.getBannerAdUnitId()
    if (configuredAdUnitId.isEmpty() || AdMobManager.isEmulator()) return

    // Use client's configured AdMob banner ad unit ID
    var hasFailedCompletely by remember(configuredAdUnitId) { mutableStateOf(false) }

    if (hasFailedCompletely) {
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .testTag("banner_ad_container"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    setAdUnitId(configuredAdUnitId)
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("BannerAdView", "Banner ad loaded successfully with ID: $configuredAdUnitId")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.w(
                                "BannerAdView",
                                "Banner ad failed to load ($configuredAdUnitId): code=${error.code}, message=${error.message}"
                            )
                            hasFailedCompletely = true
                        }
                    }
                    try {
                        loadAd(AdRequest.Builder().build())
                    } catch (e: Exception) {
                        Log.e("BannerAdView", "loadAd exception", e)
                    }
                }
            }
        )
    }
}
