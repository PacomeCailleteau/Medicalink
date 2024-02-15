package dev.mobile.medicalink.db.local

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@Config(sdk = [29])
//@SmallTest
class AppDatabaseTest {

    @Test
    fun `test singleton`() {
        val db1 = AppDatabase.getInstance(ApplicationProvider.getApplicationContext())
        val db2 = AppDatabase.getInstance(ApplicationProvider.getApplicationContext())
        assertEquals(db1, db2)
    }

}