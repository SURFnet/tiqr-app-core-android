package org.tiqr.data.util.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.biometric.BiometricManager
import timber.log.Timber

/**
 * Check if Biometrics is available and can be used
 */
fun Context.biometricUsable(): Boolean {
    return BiometricManager.from(this)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}

/**
 * Open the app settings for this app.
 * Can be used to redirect the user to enable permissions when denied permanently.
 */
fun Context.openAppSystemSettings() {
    try {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        })
    } catch (e: ActivityNotFoundException) {
        Timber.e(e, "Failed to open app system settings")
    }
}
