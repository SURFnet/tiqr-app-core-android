/*
 * Copyright (c) 2010-2020 SURFnet bv
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

package org.tiqr.core.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import org.tiqr.core.R
import org.tiqr.core.base.BaseFragment
import org.tiqr.core.databinding.FragmentAboutBinding
import java.security.Security


/**
 * Fragment to show the about screen.
 */
class AboutFragment : BaseFragment<FragmentAboutBinding>() {
    @LayoutRes
    override val layout = R.layout.fragment_about

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val capabilities = generateSecurityCapabilities()
        binding.security.setOnClickListener {
            sendEmail(it.context, capabilities)
        }
        val clipboardManager: ClipboardManager =
            requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        binding.security.setOnLongClickListener { it ->
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied", capabilities))
            Toast.makeText(it.context, R.string.about_copied, Toast.LENGTH_SHORT).show()
            true
        }
        binding.securityData.text = capabilities
    }

    private fun sendEmail(context: Context, emailBody: String) {
        val versionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName, PackageManager.PackageInfoFlags.of(0)
            ).versionName
        } else {
            context.packageManager.getPackageInfo(
                context.packageName, 0
            ).versionName
        }
        val appName = getString(R.string.app_name)
        val title = getString(R.string.about_label_version, appName, versionName)

        val uri = Uri.parse("mailto:tiqr@surf.nl")
            .buildUpon()
            .appendQueryParameter("to", "tiqr@surf.nl")
            .appendQueryParameter("subject", title)
            .appendQueryParameter("body", emailBody)
            .build()
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    private fun generateSecurityCapabilities(): String {
        val capabilities = StringBuilder()
        val providers = Security.getProviders()
        providers.forEach { provider ->
            capabilities.append("Provider ${provider.name}/${provider.version}. Services:\n")
            provider.services.forEachIndexed { index, service ->
                capabilities.append("\t$index. ${service.type} - ${service.algorithm}\n")
            }
            capabilities.append("End provider ${provider.name}/${provider.version}\n")
        }
        return capabilities.toString()
    }
}