package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Index
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class IndicesDiff(val table1: Table?, val table2: Table) {

	val same = ArrayList<Index>()
	val added = ArrayList<Index>()
	val removed = ArrayList<Index>()
	val changed = ArrayList<Index>()

	val wasChanged: Boolean
		get() = added.isNotEmpty() || removed.isNotEmpty() || changed.isNotEmpty()

	init {
		init()
	}

	fun init() {
		if (table1 == null) {
			added.addAll(table2.indices)
			return
		}

		val indexes1Map = table1.indices.associateByTo(HashMap()) { it.name }
		for (index2 in table2.indices) {
			val index1 = indexes1Map.remove(index2.name)
			when (index1) {
				null -> added.add(index2)
				index2 -> same.add(index2)
				else -> changed.add(index2)
			}
		}
		removed.addAll(indexes1Map.values)
	}

}