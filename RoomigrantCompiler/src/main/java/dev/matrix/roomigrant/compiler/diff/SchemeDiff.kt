package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Scheme
import dev.matrix.roomigrant.compiler.data.Table
import dev.matrix.roomigrant.compiler.data.View
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class SchemeDiff(val old: Scheme, val new: Scheme) {

	val sameTables = ArrayList<TableDiff>()
	val addedTables = ArrayList<TableDiff>()
	val removedTables = ArrayList<Table>()
	val changedTables = ArrayList<TableDiff>()

	val sameViews = ArrayList<ViewDiff>()
	val addedViews = ArrayList<ViewDiff>()
	val removedViews = ArrayList<View>()
	val changedViews = ArrayList<ViewDiff>()

	val wasChanged: Boolean
		get() = addedTables.isEmpty()
				&& removedTables.isEmpty()
				&& changedTables.isEmpty()
				&& addedViews.isEmpty()
				&& removedViews.isEmpty()
				&& changedViews.isEmpty()

	init {
		val oldTableMap = old.tables.associateByTo(HashMap()) { it.name.toLowerCase(Locale.getDefault()) }
		for (newTable in new.tables) {
			val oldTable = oldTableMap.remove(newTable.name.toLowerCase(Locale.getDefault()))
			if (oldTable == null) {
				addedTables.add(TableDiff(null, newTable))
				continue
			}
			val diff = TableDiff(oldTable, newTable)
			if (diff.wasChanged) {
				changedTables.add(diff)
			} else {
				sameTables.add(TableDiff(null, newTable))
			}
		}
		removedTables.addAll(oldTableMap.values)

		val newViewMap = old.views.associateByTo(HashMap()) { it.name.toLowerCase(Locale.getDefault()) }
		for (newView in new.views) {
			val oldView = newViewMap.remove(newView.name.toLowerCase(Locale.getDefault()))
			if (oldView == null) {
				addedViews.add(ViewDiff(null, newView))
				continue
			}
			val diff = ViewDiff(oldView, newView)
			if (diff.wasChanged) {
				changedViews.add(diff)
			} else {
				sameViews.add(ViewDiff(null, newView))
			}
		}
		removedViews.addAll(newViewMap.values)
	}
}
