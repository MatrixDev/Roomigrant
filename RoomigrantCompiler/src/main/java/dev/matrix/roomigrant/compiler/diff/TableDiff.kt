package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TableDiff(val old: Table?, val new: Table) {
	val fieldsDiff = FieldsDiff(old, new)
	val indicesDiff = IndicesDiff(old, new)
	val nameChanged = old?.name != new.name
	val primaryKeyChanged = old?.primaryKey != new.primaryKey

	val wasChanged: Boolean
		get() = primaryKeyChanged || nameChanged || fieldsDiff.wasChanged || indicesDiff.wasChanged
}

