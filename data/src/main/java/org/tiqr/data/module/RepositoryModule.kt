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

package org.tiqr.data.module

import android.content.res.Resources
import androidx.annotation.VisibleForTesting
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.tiqr.data.api.TiqrApi
import org.tiqr.data.api.TokenApi
import org.tiqr.data.di.DefaultDispatcher
import org.tiqr.data.repository.AuthenticationRepository
import org.tiqr.data.repository.EnrollmentRepository
import org.tiqr.data.repository.IdentityRepository
import org.tiqr.data.repository.NotificationCacheRepository
import org.tiqr.data.repository.TokenRepository
import org.tiqr.data.repository.base.TokenRegistrarRepository
import org.tiqr.data.service.DatabaseService
import org.tiqr.data.service.PreferenceService
import org.tiqr.data.service.SecretService
import javax.inject.Singleton

/**
 * Module which serves the repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {
    @Provides
    @Singleton
    internal fun provideAuthenticationRepository(
        api: TiqrApi,
        resources: Resources,
        database: DatabaseService,
        secret: SecretService,
        preferences: PreferenceService,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ) = AuthenticationRepository(api, resources, database, secret, preferences, dispatcher)

    @Provides
    @Singleton
    internal fun provideEnrollmentRepository(
        api: TiqrApi,
        resources: Resources,
        database: DatabaseService,
        secret: SecretService,
        preferences: PreferenceService,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ) = EnrollmentRepository(api, resources, database, secret, preferences, dispatcher)

    @Provides
    @Singleton
    internal fun provideIdentityRepository(
        database: DatabaseService,
        secret: SecretService,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ) = IdentityRepository(database, secret, dispatcher)
}

/**
 * Module which serves the TokenRegistrarRepository implementation.
 * This is served in a separate module so it can be replaced in a unit test with a dummy implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
@VisibleForTesting
class TokenRepositoryModule {
    @Provides
    @Singleton
    internal fun provideTokenRepository(
        api: Lazy<TokenApi>,
        preferences: PreferenceService,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): TokenRegistrarRepository = TokenRepository(api, preferences, dispatcher)
}


@Module
@InstallIn(SingletonComponent::class)
@VisibleForTesting
class NotificationCacheRepositoryModule {
    @Provides
    @Singleton
    internal fun provideNotificationCacheRepository(
        preferences: PreferenceService,
    ): NotificationCacheRepository = NotificationCacheRepository(preferences)
}