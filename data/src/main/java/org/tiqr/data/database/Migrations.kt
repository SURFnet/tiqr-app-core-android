package org.tiqr.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

val FROM_4_TO_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Adds 2 columns.")

        database.run {
            execSQL("ALTER TABLE identity ADD COLUMN showFingerPrintUpgrade INTEGER NOT NULL DEFAULT 1;")
            execSQL("ALTER TABLE identity ADD COLUMN useFingerPrint INTEGER NOT NULL DEFAULT 0;")
        }
    }
}

val FROM_5_TO_7: Migration = object : Migration(5, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Changes LOGO from BLOB to TEXT.")

        database.run {
            execSQL("CREATE TABLE new_identityprovider (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, authenticationUrl TEXT NOT NULL, ocraSuite TEXT NOT NULL, infoUrl TEXT, logo TEXT);")
            execSQL("INSERT INTO new_identityprovider (_id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl) SELECT _id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl FROM identityprovider;")
            execSQL("DROP TABLE identityprovider;")
            execSQL("ALTER TABLE new_identityprovider RENAME TO identityprovider;")
        }
    }
}

// From version 8 onwards Room is being used
val FROM_7_TO_8: Migration = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Adds FK's and indexes.")

        database.run {
            execSQL("CREATE TABLE new_identity (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, identityProvider INTEGER NOT NULL, blocked INTEGER NOT NULL DEFAULT 0, sortIndex INTEGER NOT NULL, showFingerPrintUpgrade INTEGER NOT NULL DEFAULT 1, useFingerPrint INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(identityProvider) REFERENCES identityprovider(_id) ON UPDATE NO ACTION ON DELETE CASCADE);")
            execSQL("INSERT INTO new_identity (_id, displayName, identifier, identityProvider, blocked, sortIndex, showFingerPrintUpgrade, useFingerPrint) SELECT _id, displayName, identifier, identityProvider, blocked, sortIndex, showFingerPrintUpgrade, useFingerPrint FROM identity;")
            execSQL("DROP TABLE identity;")
            execSQL("ALTER TABLE new_identity RENAME TO identity;")
            execSQL("CREATE UNIQUE INDEX id_idx ON identity(_id);")
            execSQL("CREATE INDEX identifier_idx ON identity(identifier);")
            execSQL("CREATE INDEX identity_provider_idx ON identity(identityProvider);")

            execSQL("CREATE TABLE new_identityprovider (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, authenticationUrl TEXT NOT NULL, ocraSuite TEXT NOT NULL, infoUrl TEXT, logo TEXT);")
            execSQL("INSERT INTO new_identityprovider (_id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl, logo) SELECT _id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl, logo FROM identityprovider;")
            execSQL("DROP TABLE identityprovider;")
            execSQL("ALTER TABLE new_identityprovider RENAME TO identityprovider;")
            execSQL("CREATE INDEX ip_identifier_idx ON identityprovider(identifier)")
        }
    }
}

@Deprecated("The migration from 8 to 9 is using a unique index on the identityprovider table which is wrong. Migrating to this version 9 can lead to data loss for users.")
val FROM_8_TO_9: Migration = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Renames fingerprint to biometric. Renames indexes.")

        database.run {
            execSQL("CREATE TABLE new_identity (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, identityProvider INTEGER NOT NULL, blocked INTEGER NOT NULL DEFAULT 0, sortIndex INTEGER NOT NULL, biometricInUse INTEGER NOT NULL DEFAULT 0, biometricOfferUpgrade INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(identityProvider) REFERENCES identityprovider(_id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
            execSQL("INSERT INTO new_identity (_id, displayName, identifier, identityProvider, blocked, sortIndex, biometricInUse, biometricOfferUpgrade) SELECT _id, displayName, identifier, identityProvider, blocked, sortIndex, useFingerPrint, showFingerPrintUpgrade FROM identity;")
            execSQL("DROP TABLE identity;")
            execSQL("ALTER TABLE new_identity RENAME TO identity;")
            execSQL("CREATE UNIQUE INDEX index_identity_id ON identity(_id);")
            execSQL("CREATE INDEX index_identity_identifier ON identity(identifier);")
            execSQL("CREATE INDEX index_identity_identityProvider ON identity(identityProvider);")

            execSQL("CREATE TABLE new_identityprovider (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, authenticationUrl TEXT NOT NULL, ocraSuite TEXT NOT NULL, infoUrl TEXT, logo TEXT);")
            execSQL("INSERT INTO new_identityprovider (_id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl, logo) SELECT _id, displayName, identifier, authenticationUrl, ocraSuite, infoUrl, logo FROM identityprovider;")
            execSQL("DROP TABLE identityprovider;")
            execSQL("ALTER TABLE new_identityprovider RENAME TO identityprovider;")
            execSQL("CREATE UNIQUE INDEX index_identityprovider_identifier ON identityprovider(identifier);")
        }
    }
}

val FROM_8_TO_10: Migration = object : Migration(8, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Renames fingerprint to biometric. Recreates indexes for identity table and renames it for identityprovider (ip_identifier_idx->index_identityprovider_identifier)")

        database.run {
            execSQL("CREATE TABLE new_identity (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, displayName TEXT NOT NULL, identifier TEXT NOT NULL, identityProvider INTEGER NOT NULL, blocked INTEGER NOT NULL DEFAULT 0, sortIndex INTEGER NOT NULL, biometricInUse INTEGER NOT NULL DEFAULT 0, biometricOfferUpgrade INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(identityProvider) REFERENCES identityprovider(_id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
            execSQL("INSERT INTO new_identity (_id, displayName, identifier, identityProvider, blocked, sortIndex, biometricInUse, biometricOfferUpgrade) SELECT _id, displayName, identifier, identityProvider, blocked, sortIndex, useFingerPrint, showFingerPrintUpgrade FROM identity;")
            execSQL("DROP TABLE identity;")
            execSQL("ALTER TABLE new_identity RENAME TO identity;")
            execSQL("CREATE UNIQUE INDEX index_identity_id ON identity(_id)")
            execSQL("CREATE INDEX index_identity_identifier ON identity(identifier)")
            execSQL("CREATE INDEX index_identity_identityProvider ON identity(identityProvider)")

            execSQL("DROP INDEX ip_identifier_idx;")
            execSQL("CREATE INDEX index_identityprovider_identifier ON identityprovider(identifier);")
        }
    }
}

/**
 * For the apps that have already migrated to version 9 and have already experienced data loss, we still have to drop the unique index
 * and create it as not unique.
 * */
val FROM_9_TO_10: Migration = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Migrating database from version $startVersion to version $endVersion.")
        Timber.d("Drops the unique index `index_identityprovider_identifier` and recreates it as not unique for table identityprovider.")

        database.run {
            execSQL("DROP INDEX index_identityprovider_identifier;")
            execSQL("CREATE INDEX index_identityprovider_identifier ON identityprovider(identifier);")
        }
    }
}

val ALL_VALID_MIGRATIONS = arrayOf(
    FROM_4_TO_5,
    FROM_5_TO_7,
    FROM_7_TO_8,
    FROM_8_TO_10,
    FROM_9_TO_10,
)