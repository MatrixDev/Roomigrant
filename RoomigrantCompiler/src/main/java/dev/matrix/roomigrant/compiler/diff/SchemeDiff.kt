package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Scheme
import dev.matrix.roomigrant.compiler.data.Table
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class SchemeDiff(val scheme1: Scheme, val scheme2: Scheme) {

	val same = ArrayList<TableDiff>()
	val added = ArrayList<TableDiff>()
	val removed = ArrayList<Table>()
	val changed = ArrayList<TableDiff>()

	val wasChanged: Boolean
		get() = added.isEmpty() && removed.isEmpty() && changed.isEmpty()

	init {
		val tables1Map = scheme1.tables.associateByTo(HashMap()) { it.name.toLowerCase(Locale.getDefault()) }
		for (table2 in scheme2.tables) {
			val table1 = tables1Map.remove(table2.name.toLowerCase(Locale.getDefault()))
			if (table1 == null) {
				added.add(TableDiff(null, table2))
				continue
			}
			val diff = TableDiff(table1, table2)
			if (diff.wasChanged) {
				changed.add(diff)
			} else {
				same.add(TableDiff(null, table2))
			}
		}
		removed.addAll(tables1Map.values)
	}
}
