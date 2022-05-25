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

package org.tiqr.data.repository.base

import android.content.res.Resources
import org.tiqr.data.api.TiqrApi
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.ChallengeCompleteFailure
import org.tiqr.data.model.ChallengeCompleteRequest
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.model.ChallengeParseFailure
import org.tiqr.data.model.ChallengeParseResult
import org.tiqr.data.model.Identity
import org.tiqr.data.model.IdentityProvider
import org.tiqr.data.model.SecretType
import org.tiqr.data.service.DatabaseService
import org.tiqr.data.service.PreferenceService
import org.tiqr.data.service.SecretService

/**
 * Base Repository for handling [Challenge]'s
 */
abstract class ChallengeRepository<T: Challenge> {
    protected abstract val api: TiqrApi
    protected abstract val resources: Resources
    protected abstract val database: DatabaseService
    protected abstract val secretService: SecretService
    protected abstract val preferences: PreferenceService

    /**
     * The scheme to distinguish between challenge types.
     */
    protected abstract val challengeScheme: String

    /**
     * Contains a valid challenge?
     */
    abstract fun isValidChallenge(rawChallenge: String): Boolean

    /**
     * Parse the raw challenge.
     */
    abstract suspend fun parseChallenge(rawChallenge: String): ChallengeParseResult<T, ChallengeParseFailure>

    /**
     * Complete the challenge.
     */
    abstract suspend fun completeChallenge(request: ChallengeCompleteRequest<T>) : ChallengeCompleteResult<ChallengeCompleteFailure>

    /**
     * Upgrade [identity] to use biometric authentication
     */
    suspend fun upgradeBiometric(identity: Identity, identityProvider: IdentityProvider, pin: String) {
        // Get the identity from db, so we are sure it has a valid id
        database.getIdentity(identity.identifier, identityProvider.identifier)?.let {
            // Check secret for pin
            val pinSession = secretService.encryption.keyFromPassword(pin)
            val pinSecretId = secretService.createSecretIdentity(it, SecretType.PIN)
            val pinSecret = secretService.load(pinSecretId, sessionKey = pinSession)
            // Create biometric
            val secretId = secretService.createSecretIdentity(it, SecretType.BIOMETRIC)
            val sessionKey = secretService.createSessionKey(SecretType.BIOMETRIC.key)
            secretService.save(secretId, pinSecret, sessionKey)
            // Do not offer to upgrade biometric anymore
            it.copy(biometricInUse = true, biometricOfferUpgrade = false).run {
                database.updateIdentity(this)
            }
        }
    }

    /**
     * Upgrade [identity] to not use biometric
     */
    suspend fun stopOfferBiometric(identity: Identity) {
        identity.copy(biometricOfferUpgrade = false).run {
            database.updateIdentity(this)
        }
    }
}