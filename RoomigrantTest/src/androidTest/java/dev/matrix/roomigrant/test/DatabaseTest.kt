package dev.matrix.roomigrant.test

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import androidx.room.Database as DatabaseAnnotation

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
			migrationHelper.runMigrationsAndValidate(name, 6, true, *Database_Migrations.build())
		}
		db.close()
	}

}