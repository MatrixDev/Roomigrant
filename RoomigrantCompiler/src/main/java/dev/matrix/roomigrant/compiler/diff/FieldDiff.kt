package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Field
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class FieldDiff(val table1: Table, val table2: Table, val field1: Field, val field2: Field) {
	val affinityChanged = field1.affinity != field2.affinity
	val nullabilityChanged = field1.notNull && !field2.notNull

	val castSql: String
		get() = "CAST(`${table1.name}`.`${field1.name}` AS ${field2.affinity})"

	val toNotNullableSql: String
		get() = "IFNULL(`${table1.name}`.`${field2.name}`, ${field2.defaultSqlValue})"
}