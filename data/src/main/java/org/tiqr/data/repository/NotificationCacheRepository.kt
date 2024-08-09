package org.tiqr.data.repository

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import org.tiqr.data.service.PreferenceService
import java.util.Date

class NotificationCacheRepository(
    private val preferenceService: PreferenceService
) {

    /**
     * Save the last notification data, to be used by the app if the user does not open the notification but the app instead.
     *
     * @param challenge: The challenge URL sent in the notification payload
     * @param timeoutSeconds: The timeout in seconds. If this elapses, the challenge has timed out and all the data supplied in this method cannot be used anymore.
     * @param notificationIdentifier: The identifier of the Android notification. This will be used to cancel the notification when we use this data.
     */
    fun saveLastNotificationData(challenge: String, timeoutSeconds: Int, notificationIdentifier: Int) {
        val timeoutEpoch = Date().time + timeoutSeconds * 1_000
        preferenceService.lastNotificationId = notificationIdentifier
        preferenceService.lastNotificationChallenge = challenge
        preferenceService.lastNotificationTimeoutEpochMs = timeoutEpoch
    }

    /**
     * Returns the last notification challenge, if it was valid.
     * After returning the challenge, it will clear all last notification data, and cancel the notification.
     */
    fun getLastNotificationChallenge(context: Context): String? {
        val timeoutEpoch = preferenceService.lastNotificationTimeoutEpochMs ?: return null
        var result: String? = null
        if (Date().time <= timeoutEpoch) {
            // Not timed out yet
            result = preferenceService.lastNotificationChallenge
            // Remove the local notification
            val notificationId = preferenceService.lastNotificationId
            if (notificationId != null) {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(notificationId)
            }
        }
        preferenceService.lastNotificationId = null
        preferenceService.lastNotificationChallenge = null
        preferenceService.lastNotificationTimeoutEpochMs = null
        return result
    }

    /**
     * Removes all data related to the last notification challenge.
     */
    fun clearLastNotificationChallenge() {
        preferenceService.lastNotificationId = null
        preferenceService.lastNotificationChallenge = null
        preferenceService.lastNotificationTimeoutEpochMs = null
    }

}