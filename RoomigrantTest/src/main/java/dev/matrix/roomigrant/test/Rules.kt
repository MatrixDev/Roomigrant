package dev.matrix.roomigrant.test

import android.arch.persistence.db.SupportSQLiteDatabase
import dev.matrix.roomigrant.AfterMigrationRule
import dev.matrix.roomigrant.BeforeMigrationRule
import dev.matrix.roomigrant.FieldMigrationRule

/**
 * @author matrixdev
 */
@Suppress("FunctionName")
class Rules {

	@FieldMigrationRule(version1 = 3, version2 = 4, table = "Object1Dbo", field = "intValRenamed")
	fun migrate_3_4_Object1Dbo_intVal(): String {
		return "`Object1Dbo`.`intVal`"
	}

	@FieldMigrationRule(version1 = 3, version2 = 4, table = "Object1Dbo", field = "stringValRenamed")
	fun migrate_3_4_Object1Dbo_stringVal(): String {
		return "`Object1Dbo`.`stringVal`"
	}

	@BeforeMigrationRule(version1 = 1, version2 = 2)
	fun migrate_1_2_before(db: SupportSQLiteDatabase) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 1)
	}

	@AfterMigrationRule(version1 = 1, version2 = 2)
	fun migrate_1_2_after(db: SupportSQLiteDatabase) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 3)
	}
}
