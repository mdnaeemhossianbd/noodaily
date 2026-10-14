package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AppSettings
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SettingsRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    fun observeSettings(): Flow<AppSettings> {
        return db.collection("appSettings").document("default")
            .snapshots()
            .map { snapshot ->
                val raw = snapshot.toObject(AppSettings::class.java) ?: AppSettings()
                raw.copy(
                    admobBannerAdUnitId = if (raw.admobBannerAdUnitId.isBlank() || raw.admobBannerAdUnitId.startsWith("ca-app-pub-3940256099942544")) {
                        "ca-app-pub-6596350151078116/3154086375"
                    } else raw.admobBannerAdUnitId,
                    admobRewardedAdUnitId = if (raw.admobRewardedAdUnitId.isBlank() || raw.admobRewardedAdUnitId.startsWith("ca-app-pub-3940256099942544")) {
                        "ca-app-pub-6596350151078116/2848807925"
                    } else raw.admobRewardedAdUnitId,
                    admobAppOpenAdUnitId = if (raw.admobAppOpenAdUnitId.isBlank() || raw.admobAppOpenAdUnitId.startsWith("ca-app-pub-3940256099942544")) {
                        "ca-app-pub-6596350151078116/5690593680"
                    } else raw.admobAppOpenAdUnitId
                )
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.GET, "appSettings/default")
                emit(AppSettings())
            }
    }

    suspend fun updateSettings(settings: AppSettings): Result<Unit> = runCatching {
        val updated = settings.copy(updatedAt = Timestamp.now())
        db.collection("appSettings").document("default").set(updated).await()
    }
}
