/*
 * Copyright (c) 2010-2019 SURFnet bv
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of SURFnet bv nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.tiqr.core.util.extensions

import android.Manifest
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import com.fondesa.kpermissions.anyDenied
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.extension.checkPermissionsStatus
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tiqr.core.R
import org.tiqr.data.util.extension.openAppSystemSettings
import timber.log.Timber

/**
 * Check if permission is granted.
 *
 * @param permission A permission from [android.Manifest.permission]
 */
@CheckResult
fun FragmentActivity.hasPermission(permission: String): Boolean {
    return checkPermissionsStatus(permission).anyGranted()
}

/**
 * Check for camera permission
 */
@CheckResult
fun FragmentActivity.hasCameraPermission() = hasPermission(Manifest.permission.CAMERA)

/**
 * Check for [Manifest.permission.CAMERA] permission and invoke [onGranted],
 * otherwise ask the permission and handle the user interaction.
 */
inline fun FragmentActivity.doOnCameraPermission(crossinline onGranted: () -> Unit) {
    with(permissionsBuilder(Manifest.permission.CAMERA).build()) {
        if (checkStatus().anyGranted()) {
            onGranted.invoke()
        } else {
            send { result ->
                when {
                    result.anyGranted() -> onGranted.invoke()
                    result.anyDenied() -> Timber.i("User has denied Camera permission")
                    result.anyPermanentlyDenied() -> {
                        Timber.e("User has denied Camera permission permanently")
                        MaterialAlertDialogBuilder(this@doOnCameraPermission)
                            .setTitle(R.string.scan_permission_required_title)
                            .setMessage(R.string.scan_permission_required_message)
                            .setPositiveButton(R.string.scan_permission_required_settings) { _, _ ->
                                Timber.i("User opened app settings")
                                openAppSystemSettings()
                            }
                            .setNegativeButton(R.string.scan_permission_required_dismiss, null)
                            .show()
                    }
                }
            }
        }
    }
}