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

package org.tiqr.core.identity

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.tiqr.core.R
import org.tiqr.core.base.BaseFragment
import org.tiqr.core.databinding.FragmentIdentityDetailBinding
import org.tiqr.core.util.extensions.biometricUsable
import org.tiqr.data.model.Identity
import org.tiqr.data.viewmodel.IdentityViewModel

/**
 * Fragment to display the [Identity] details
 */
@AndroidEntryPoint
class IdentityDetailFragment : BaseFragment<FragmentIdentityDetailBinding>() {
    private val viewModel by hiltNavGraphViewModels<IdentityViewModel>(R.id.identity_nav)
    private val args by navArgs<IdentityDetailFragmentArgs>()

    @LayoutRes
    override val layout = R.layout.fragment_identity_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.model = args.identity

        viewModel.getIdentity(args.identity.identity.identifier) // Get again to have the flow-livedata active
        viewModel.identity.observe(viewLifecycleOwner) {
            it?.let { identity ->
                binding.model = identity
                binding.hasBiometric = requireContext().biometricUsable()
                binding.hasBiometricSecret = viewModel.hasBiometricSecret(identity.identity)
                binding.executePendingBindings()
            } ?: findNavController().popBackStack()
        }

        binding.biometric.setOnCheckedChangeListener { toggle, isChecked ->
            if (toggle.isPressed) {
                viewModel.useBiometric(args.identity.identity, isChecked)
            }
        }

        binding.biometricUpgrade.setOnCheckedChangeListener { toggle, isChecked ->
            if (toggle.isPressed) {
                viewModel.upgradeToBiometric(args.identity.identity, isChecked)
            }
        }

        binding.buttonDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.identity_delete_title)
                .setMessage(R.string.identity_delete_message)
                .setNegativeButton(R.string.button_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_delete) { _, _ ->
                    deleteAndClose()
                }.show()
        }
    }

    private fun deleteAndClose() {
        viewModel.deleteIdentity(args.identity.identity)
        findNavController().popBackStack()
    }

}