package dev.matrix.roomigrant.test

import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import android.arch.persistence.room.Database as DatabaseAnnotation

/**
 * @author matrixdev
 */
class DatabaseTest {

	private val assetFolder = Database::class.java.canonicalName

	@get:Rule
	val migrationHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), assetFolder)

	@Test
	fun testMigration() {
		val name = "migration_test.db"
		val db = migrationHelper.createDatabase(name, 1).let {
			migrationHelper.runMigrationsAndValidate(name, 5, true, *Database_Migrations.build())
		}
		db.close()
	}

}