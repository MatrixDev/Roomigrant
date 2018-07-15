package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Field
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class FieldsDiff(val table1: Table?, val table2: Table) {

	val same = ArrayList<FieldDiff>()
	val added = ArrayList<Field>()
	val removed = ArrayList<Field>()
	val affinityChanged = ArrayList<FieldDiff>()
	val nullabilityChanged = ArrayList<FieldDiff>()

	val wasChanged: Boolean
		get() = added.isNotEmpty() || removed.isNotEmpty() || affinityChanged.isNotEmpty() || nullabilityChanged.isNotEmpty()

	val onlyAdded: Boolean
		get() = added.isNotEmpty() && removed.isEmpty() && affinityChanged.isEmpty() && nullabilityChanged.isEmpty()

	init {
		init()
	}

	fun init() {
		if (table1 == null) {
			added.addAll(table2.fields)
			return
		}

		val fields1Map = table1.fields.associateByTo(HashMap()) { it.name }
		for (field2 in table2.fields) {
			val field1 = fields1Map.remove(field2.name)
			if (field1 == null) {
				added.add(field2)
				continue
			}

			val diff = FieldDiff(table1, table2, field1, field2)
			when {
				diff.affinityChanged -> affinityChanged.add(diff)
				diff.nullabilityChanged -> nullabilityChanged.add(diff)
				else -> same.add(diff)
			}
		}
		removed.addAll(fields1Map.values)
	}

}