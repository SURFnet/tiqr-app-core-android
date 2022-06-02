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

package org.tiqr.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.tiqr.data.BuildConfig

/**
 * Sealed class for the possible [Challenge]'s
 */
sealed class Challenge {
    abstract val protocolVersion: Int
    abstract val identityProvider: IdentityProvider
    abstract val identity: Identity?
    abstract val returnUrl: String?
}

/**
 * The result for the [EnrollmentChallenge]
 */
@Parcelize
data class EnrollmentChallenge(
        override val protocolVersion: Int = TiqrConfig.protocolVersion,
        override val identityProvider: IdentityProvider,
        override val identity: Identity,
        override val returnUrl: String?,
        val enrollmentUrl: String,
        val enrollmentHost: String
) : Challenge(), Parcelable

/**
 * The result for the [AuthenticationChallenge]
 */
@Parcelize
data class AuthenticationChallenge(
        override val protocolVersion: Int = TiqrConfig.protocolVersion,
        override val identityProvider: IdentityProvider,
        override val identity: Identity?,
        override val returnUrl: String?,
        val identities: List<Identity> = emptyList(),
        val sessionKey: String,
        val challenge: String,
        val isStepUpChallenge: Boolean = false,
        val serviceProviderDisplayName: String,
        val serviceProviderIdentifier: String
) : Challenge(), Parcelable {
    /**
     * Property to indicate if this [Challenge] contains multiple identities.
     */
    val hasMultipleIdentities: Boolean get() = identities.isNotEmpty()
}
