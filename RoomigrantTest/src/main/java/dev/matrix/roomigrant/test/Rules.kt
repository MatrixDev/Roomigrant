package dev.matrix.roomigrant.test

import dev.matrix.roomigrant.FieldMigrationRule

/**
 * @author matrixdev
 */
class Rules {

	@FieldMigrationRule(version1 = 3, version2 = 4, table = "Object1Dbo", field = "intValRenamed")
	fun migrate_3_4_Object1Dbo_intVal(): String {
		return "`Object1Dbo`.`intVal`"
	}

	@FieldMigrationRule(version1 = 3, version2 = 4, table = "Object1Dbo", field = "stringValRenamed")
	fun migrate_3_4_Object1Dbo_stringVal(): String {
		return "`Object1Dbo`.`stringVal`"
	}

}