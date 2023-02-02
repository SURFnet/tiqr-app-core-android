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
import androidx.room.*
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Identity model and entity.
 */
@Entity(tableName = "identity",
        indices = [
                Index(value = ["_id"], name = "index_identity_id", unique = true),
                Index(value = ["identifier"], name = "index_identity_identifier"),
                Index(value = ["identityProvider"], name = "index_identity_identityProvider")
        ],
        foreignKeys = [
                ForeignKey(
                    entity = IdentityProvider::class,
                    childColumns = ["identityProvider"],
                    parentColumns = ["_id"],
                    onDelete = ForeignKey.RESTRICT
                )
        ]
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Identity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Long = 0L,

        @ColumnInfo(name = "displayName")
        val displayName: String,

        @ColumnInfo(name = "identifier")
        val identifier: String,

        @ColumnInfo(name = "identityProvider")
        val identityProvider: Long = -1L,

        @ColumnInfo(name = "blocked", defaultValue = "0")
        val blocked: Boolean = false,

        @ColumnInfo(name = "sortIndex")
        val sortIndex: Int = 0,

        @ColumnInfo(name = "biometricInUse", defaultValue = "0")
        val biometricInUse: Boolean = false,

        @ColumnInfo(name = "biometricOfferUpgrade", defaultValue = "1")
        val biometricOfferUpgrade: Boolean = true
) : Parcelable