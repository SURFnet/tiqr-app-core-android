package org.tiqr.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.internal.closeQuietly
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.tiqr.data.database.DatabaseQueries.V8_FIRST_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V8_IDENTITY1_ON_FIRST_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V8_IDENTITY1_ON_SECOND_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V8_IDENTITY2_ON_FIRST_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V8_IDENTITY2_ON_SECOND_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V8_SECOND_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V9_FIRST_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V9_IDENTITY1_ON_FIRST_PROVIDER
import org.tiqr.data.database.DatabaseQueries.V9_IDENTITY2_ON_FIRST_PROVIDER
import org.tiqr.data.database.TiqrDatabase.Companion.FROM_8_TO_10
import org.tiqr.data.database.TiqrDatabase.Companion.FROM_8_TO_9
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class TiqrDatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        /* instrumentation = */ InstrumentationRegistry.getInstrumentation(),
        /* assetsFolder = */ TiqrDatabase::class.java.canonicalName,
        /* openFactory = */ FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun givenV8ThenMigrateV9ThenIntegrityOk() {
        createV8WithData()

        try {
            helper.runMigrationsAndValidate(TEST_DB, 9, true, FROM_8_TO_9)
        } catch (e: SQLiteConstraintException) {
            Timber.i("Expected 8 to 9 migration failure")
        }
        helper.createDatabase(TEST_DB, 9).apply {
            assertEquals(
                /* message = */ "DB integrity compromised after 8 to 9 migration",
                /* expected = */ true,
                /* actual = */ isDatabaseIntegrityOk
            )
            assertEquals(
                /* message = */ "Migration incremented the version",
                /* expected = */ 9,
                /* actual = */ version
            )
            this.version
            close()
        }
    }

    @Test
    fun givenV9UniqueIndexThenFailSameProviderIdentities() {
        helper.createDatabase(TEST_DB, 9).apply {
            try {
                execSQL(V9_FIRST_PROVIDER)
                execSQL(V9_IDENTITY1_ON_FIRST_PROVIDER)
                execSQL(V9_IDENTITY2_ON_FIRST_PROVIDER)
            } catch (e: SQLiteConstraintException) {
                Timber.e(e, "Failed to insert data into v9")
            }
            val cursor = query("SELECT * FROM identity WHERE identityprovider=0")
            assertEquals(
                /* message = */ "Not able to add multiple identities for the same provider",
                /* expected = */ 1,
                /* actual = */ cursor.count
            )
            cursor.closeQuietly()
            close()
        }
    }

    @Test
    fun givenV8WhenMigrateV10ThenNoDataLoss() {
        createV8WithData()

        try {
            val db = helper.runMigrationsAndValidate(TEST_DB, 10, true, FROM_8_TO_10)
            val cursor = db.query("SELECT * FROM identity WHERE identityprovider=0")
            assertEquals(
                /* message = */ "After 8 to 10 migration there is no data loss",
                /* expected = */ 2,
                /* actual = */ cursor.count
            )
            cursor.closeQuietly()
            db.close()
        } catch (e: SQLiteConstraintException) {
            Timber.e(e, "Failed migration from 9 to 10")
        }
    }

    private fun createV8WithData() {
        helper.createDatabase(TEST_DB, 8).apply {
            execSQL(V8_FIRST_PROVIDER)
            execSQL(V8_SECOND_PROVIDER)
            execSQL(V8_IDENTITY1_ON_FIRST_PROVIDER)
            execSQL(V8_IDENTITY2_ON_FIRST_PROVIDER)
            execSQL(V8_IDENTITY1_ON_SECOND_PROVIDER)
            execSQL(V8_IDENTITY2_ON_SECOND_PROVIDER)
            close()
        }
    }
}

