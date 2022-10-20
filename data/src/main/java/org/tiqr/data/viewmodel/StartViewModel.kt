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

package org.tiqr.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import org.tiqr.data.R
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the start screen.
 */
@HiltViewModel
class StartViewModel @Inject constructor(db: DatabaseService) : ViewModel() {
    private val _identityCount: Flow<Int> = db.identityCount()
            .onStart { emit(value = 0) }
            .catch { ex ->
                Timber.w(ex, "Unable to determine identity count!")
                emit(value = 0)
            }

    private val _allIdentitiesBlocked: Flow<Boolean> = db.allIdentitiesBlocked()
            .onStart { emit(value = false) }
            .catch { ex ->
                Timber.w(ex, "Unable to fetch all identities!")
                emit(value = false)
            }

    /**
     * Current number of identities.
     */
    val identityCount: LiveData<Int> = _identityCount.asLiveData(viewModelScope.coroutineContext)

    /**
     * Are there any identities?
     */
    val hasIdentities
        get() = (identityCount.value ?: 0) > 0

    /**
     * What content text to show based on identity blocked state and identity count.
     */
    val contentType: LiveData<Int> = _allIdentitiesBlocked
            .zip(_identityCount) { blocked, count ->
                when {
                    blocked -> R.string.start_blocked
                    count > 0 -> R.string.start_instructions
                    else -> R.string.start_welcome
                }
            }
            .onStart { emit(value = R.string.start_welcome) }
            .catch { emit(value = R.string.start_welcome) }
            .asLiveData(viewModelScope.coroutineContext)
}