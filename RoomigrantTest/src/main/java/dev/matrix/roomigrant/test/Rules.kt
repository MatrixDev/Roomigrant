package dev.matrix.roomigrant.test

import android.arch.persistence.db.SupportSQLiteDatabase
import dev.matrix.roomigrant.rules.FieldMigrationRule
import dev.matrix.roomigrant.rules.OnMigrationEndRule
import dev.matrix.roomigrant.rules.OnMigrationStartRule

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

	@OnMigrationStartRule(version1 = 1, version2 = 2)
	fun migrate_1_2_before(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 1)
	}

	@OnMigrationStartRule(version1 = 1)
	fun migrate_1_n_before(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 1)
	}

	@OnMigrationEndRule(version1 = 1, version2 = 2)
	fun migrate_1_2_after(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 3)
	}

	@OnMigrationEndRule(version2 = 2)
	fun migrate_n_2_after(db: SupportSQLiteDatabase, version1: Int, version2: Int) {
		val cursor = db.query("pragma table_info(Object1Dbo)")
		assert(cursor.count == 3)
	}
}
