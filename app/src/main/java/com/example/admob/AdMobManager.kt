package com.example.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.model.AppSettings
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Production ad units specified by client
    const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-6596350151078116/3154086375"
    const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-6596350151078116/2848807925"
    const val PROD_APP_OPEN_AD_UNIT_ID = "ca-app-pub-6596350151078116/5690593680"

    // Standard official Google AdMob test IDs (Guaranteed to return test ads)
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

    private var isInitialized = false

    // Cached ads
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false

    // Ad scheduling and policy counters
    private var sessionActionCount = 0
    private var sessionInterstitialImpressions = 0
    private var lastFullScreenAdTimestamp: Long = 0L
    private var sessionStartTimestamp: Long = System.currentTimeMillis()

    // Configurable settings
    private var settings: AppSettings = AppSettings()

    // Observables for UI
    private val _isRewardedAdReady = MutableStateFlow(false)
    val isRewardedAdReady: StateFlow<Boolean> = _isRewardedAdReady.asStateFlow()

    fun isEmulator(): Boolean {
        val fp = android.os.Build.FINGERPRINT.lowercase()
        val model = android.os.Build.MODEL.lowercase()
        val mfr = android.os.Build.MANUFACTURER.lowercase()
        val hw = android.os.Build.HARDWARE.lowercase()
        val prod = android.os.Build.PRODUCT.lowercase()
        val dev = android.os.Build.DEVICE.lowercase()

        return fp.startsWith("generic") ||
                fp.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("emulator") ||
                model.contains("android sdk built for x86") ||
                mfr.contains("genymotion") ||
                hw.contains("goldfish") ||
                hw.contains("ranchu") ||
                prod.contains("sdk_google") ||
                prod.contains("google_sdk") ||
                prod.contains("sdk") ||
                prod.contains("vbox86p") ||
                prod.contains("emulator") ||
                dev.contains("generic")
    }

    fun isTestAdUnitId(adUnitId: String): Boolean {
        val trimmed = adUnitId.trim()
        return trimmed.startsWith("ca-app-pub-3940256099942544/") ||
                trimmed == TEST_BANNER_AD_UNIT_ID ||
                trimmed == TEST_INTERSTITIAL_AD_UNIT_ID ||
                trimmed == TEST_REWARDED_AD_UNIT_ID ||
                trimmed == TEST_APP_OPEN_AD_UNIT_ID
    }

    fun initialize(context: Context, appSettings: AppSettings = AppSettings()) {
        if (isInitialized) return
        this.settings = appSettings

        // Cloud emulator safety: In emulator preview where Google Play Services AdServices daemon
        // is absent, skip initialization to prevent ServiceUnavailableException logs and protect account from invalid traffic.
        if (isEmulator()) {
            Log.i(TAG, "Running on emulator environment. AdMob background measurement disabled for container stability.")
            isInitialized = true
            return
        }

        try {
            // Register emulator as safe test device so Google AdMob serves test ads without policy flags
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            // Run initialization on background thread as recommended by official Google Mobile Ads SDK docs
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    MobileAds.initialize(context) { initializationStatus ->
                        Log.d(TAG, "Google Mobile Ads initialized successfully: $initializationStatus")
                        isInitialized = true
                        preloadAds(context)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "AdMob background init notice: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization failed", e)
        }
    }

    fun updateSettings(newSettings: AppSettings, context: Context) {
        this.settings = newSettings
        if (isInitialized) {
            preloadAds(context)
        }
    }

    fun preloadAds(context: Context) {
        preloadInterstitial(context)
        preloadRewarded(context)
        preloadAppOpen(context)
    }

    fun getBannerAdUnitId(): String {
        if (!settings.admobBannerEnabled) return ""
        val configured = settings.admobBannerAdUnitId.trim()
        return if (configured.isNotEmpty() && !isTestAdUnitId(configured)) configured else PROD_BANNER_AD_UNIT_ID
    }

    // --- INTERSTITIAL ADS ---
    fun preloadInterstitial(context: Context, onLoaded: ((Boolean) -> Unit)? = null) {
        val configured = settings.admobInterstitialAdUnitId.trim()
        val adUnitId = if (configured.isNotEmpty() && !isTestAdUnitId(configured)) configured else ""
        if (adUnitId.isEmpty() || (!settings.admobInterstitialEnabled && onLoaded == null)) {
            onLoaded?.invoke(false)
            return
        }
        if (interstitialAd != null) {
            onLoaded?.invoke(true)
            return
        }
        if (isInterstitialLoading) return

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully ($adUnitId)")
                    onLoaded?.invoke(true)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load ($adUnitId): code=${error.code}, message=${error.message}")
                    onLoaded?.invoke(false)
                }
            }
        )
    }

    fun showInterstitialNow(activity: Activity, onAdClosed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastFullScreenAdTimestamp = System.currentTimeMillis()
                    onAdClosed()
                    preloadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdClosed()
                    preloadInterstitial(activity)
                }
            }
            ad.show(activity)
        } else {
            val configured = settings.admobInterstitialAdUnitId.trim()
            val adUnitId = if (configured.isNotEmpty()) configured else TEST_INTERSTITIAL_AD_UNIT_ID
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: InterstitialAd) {
                        interstitialAd = loadedAd
                        showInterstitialNow(activity, onAdClosed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Direct interstitial ad load failed: code=${error.code}, message=${error.message}")
                        onAdClosed()
                    }
                }
            )
        }
    }

    fun recordNavigationAction(activity: Activity) {
        sessionActionCount++
        checkAndShowInterstitial(activity)
    }

    private fun checkAndShowInterstitial(activity: Activity) {
        val now = System.currentTimeMillis()
        val configured = settings.admobInterstitialAdUnitId.trim()
        val isTestMode = isTestAdUnitId(configured) || isEmulator()

        if (!settings.admobInterstitialEnabled) {
            return
        }

        if (!isTestMode) {
            // Production policy checks:
            val sessionDurationMinutes = (now - sessionStartTimestamp) / (60 * 1000)
            if (sessionDurationMinutes < settings.interstitialMinIntervalMinutes) {
                return
            }

            if (sessionInterstitialImpressions >= settings.interstitialMaxPerSession) {
                return
            }

            if (sessionActionCount < settings.interstitialMinActions) {
                return
            }

            val cooldownMillis = settings.interstitialCooldownSeconds * 1000L
            if (now - lastFullScreenAdTimestamp < cooldownMillis) {
                return
            }
        }

        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastFullScreenAdTimestamp = System.currentTimeMillis()
                    sessionActionCount = 0
                    sessionInterstitialImpressions++
                    preloadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                }
            }
            ad.show(activity)
        } ?: run {
            preloadInterstitial(activity)
        }
    }

    // --- REWARDED ADS ---
    fun preloadRewarded(context: Context, onLoaded: ((Boolean) -> Unit)? = null) {
        val configured = settings.admobRewardedAdUnitId.trim()
        val adUnitId = if (configured.isNotEmpty() && !isTestAdUnitId(configured)) configured else PROD_REWARDED_AD_UNIT_ID
        if (!settings.admobRewardedEnabled && onLoaded == null) {
            return
        }
        if (rewardedAd != null) {
            _isRewardedAdReady.value = true
            onLoaded?.invoke(true)
            return
        }
        if (isRewardedLoading) return

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    _isRewardedAdReady.value = true
                    Log.d(TAG, "Rewarded ad loaded successfully ($adUnitId)")
                    onLoaded?.invoke(true)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    _isRewardedAdReady.value = false
                    Log.w(TAG, "Rewarded ad failed to load ($adUnitId): code=${error.code}, message=${error.message}")
                    onLoaded?.invoke(false)
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdClosed: () -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    _isRewardedAdReady.value = false
                    lastFullScreenAdTimestamp = System.currentTimeMillis()
                    onAdClosed()
                    preloadRewarded(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    _isRewardedAdReady.value = false
                    onAdClosed()
                    preloadRewarded(activity)
                }
            }
            ad.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward()
            }
        } else {
            // If ad is not yet cached, attempt to load immediately with production ID
            val configured = settings.admobRewardedAdUnitId.trim()
            val adUnitId = if (configured.isNotEmpty() && !isTestAdUnitId(configured)) configured else PROD_REWARDED_AD_UNIT_ID
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                activity,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: RewardedAd) {
                        rewardedAd = loadedAd
                        _isRewardedAdReady.value = true
                        showRewardedAd(activity, onUserEarnedReward, onAdClosed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Rewarded ad fallback load failed: ${error.message}")
                        // Reward user anyway so UX flow is not blocked
                        onUserEarnedReward()
                        onAdClosed()
                    }
                }
            )
        }
    }

    fun showRewardedNow(
        activity: Activity,
        onUserEarnedReward: () -> Unit = {},
        onAdClosed: () -> Unit = {}
    ) {
        showRewardedAd(activity, onUserEarnedReward, onAdClosed)
    }

    // --- APP OPEN ADS ---
    fun preloadAppOpen(context: Context, onLoaded: ((Boolean) -> Unit)? = null) {
        val configured = settings.admobAppOpenAdUnitId.trim()
        val adUnitId = if (configured.isNotEmpty() && !isTestAdUnitId(configured)) configured else PROD_APP_OPEN_AD_UNIT_ID
        if (!settings.admobAppOpenEnabled && onLoaded == null) {
            return
        }
        if (appOpenAd != null) {
            onLoaded?.invoke(true)
            return
        }
        if (isAppOpenLoading) return

        isAppOpenLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adUnitId,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenLoading = false
                    Log.d(TAG, "App Open ad loaded ($adUnitId)")
                    onLoaded?.invoke(true)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    isAppOpenLoading = false
                    Log.w(TAG, "App Open ad failed to load ($adUnitId): code=${error.code}, message=${error.message}")
                    onLoaded?.invoke(false)
                }
            }
        )
    }

    fun showAppOpenAdIfAvailable(activity: Activity) {
        val now = System.currentTimeMillis()
        if (!settings.admobAppOpenEnabled) return

        val configured = settings.admobAppOpenAdUnitId.trim()
        val isTestMode = isTestAdUnitId(configured) || isEmulator()

        if (!isTestMode) {
            val cooldownMillis = settings.interstitialCooldownSeconds * 1000L
            if (now - lastFullScreenAdTimestamp < cooldownMillis) return
        }

        appOpenAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    lastFullScreenAdTimestamp = System.currentTimeMillis()
                    preloadAppOpen(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    preloadAppOpen(activity)
                }
            }
            ad.show(activity)
        } ?: run {
            preloadAppOpen(activity)
        }
    }

    fun showAppOpenNow(activity: Activity, onAdClosed: () -> Unit = {}) {
        val ad = appOpenAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    lastFullScreenAdTimestamp = System.currentTimeMillis()
                    onAdClosed()
                    preloadAppOpen(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    onAdClosed()
                    preloadAppOpen(activity)
                }
            }
            ad.show(activity)
        } else {
            val configured = settings.admobAppOpenAdUnitId.trim()
            val adUnitId = if (configured.isNotEmpty()) configured else TEST_APP_OPEN_AD_UNIT_ID
            val adRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                activity,
                adUnitId,
                adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: AppOpenAd) {
                        appOpenAd = loadedAd
                        showAppOpenNow(activity, onAdClosed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Direct App Open load failed: code=${error.code}, message=${error.message}")
                        onAdClosed()
                    }
                }
            )
        }
    }
}
