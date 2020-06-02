package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Field
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class FieldDiff(val oldTable: Table, val newTable: Table, val oldField: Field, val newField: Field) {
	val affinityChanged = oldField.affinity != newField.affinity
	val nullabilityChanged = !oldField.notNull && newField.notNull

	val copySql: String
		get() = "`${oldTable.name}`.`${oldField.name}`"

	val castSql: String
		get() = "CAST(`${oldTable.name}`.`${oldField.name}` AS ${newField.affinity})"

	val toNotNullableSql: String
		get() = "IFNULL(`${oldTable.name}`.`${newField.name}`, ${newField.defaultSqlValue})"
}
