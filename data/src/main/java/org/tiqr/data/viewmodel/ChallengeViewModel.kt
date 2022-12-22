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

package org.tiqr.data.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.Identity
import org.tiqr.data.repository.base.ChallengeRepository
import timber.log.Timber

/**
 * Base ViewModel for [Challenge]
 */
abstract class ChallengeViewModel<C : Challenge, R : ChallengeRepository<*>>(
    savedStateHandle: SavedStateHandle, key: String
) : ViewModel() {
    @Suppress("PropertyName")
    protected val _challenge: MutableLiveData<C> = MutableLiveData<C>()
    val challenge: LiveData<C> get() = _challenge

    protected abstract val repository: R

    init {
        if (savedStateHandle.contains(key)) {
            _challenge.value = savedStateHandle[key]
        } else {
            Timber.e("SavedStateHandle does not contain key: $key. It includes: $savedStateHandle")
        }
    }

    /**
     * Upgrade [Identity] from [challenge] to use biometric authentication
     */
    fun upgradeBiometric(pin: String) {
        viewModelScope.launch {
            challenge.value?.let { challenge ->
                challenge.identity?.let {
                    repository.upgradeBiometric(it, challenge.identityProvider, pin)
                }
            }
        }
    }

    /**
     * Upgrade [Identity] from [challenge] to not use biometric
     */
    fun stopOfferBiometric() {
        viewModelScope.launch {
            challenge.value?.identity?.let {
                repository.stopOfferBiometric(it)
            }
        }
    }

}
