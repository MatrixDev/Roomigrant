package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Field
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class FieldsDiff(val old: Table?, val new: Table) {

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
		if (old == null) {
			added.addAll(new.fields)
			return
		}

		val fields1Map = old.fields.associateByTo(HashMap()) { it.name }
		for (field2 in new.fields) {
			val field1 = fields1Map.remove(field2.name)
			if (field1 == null) {
				added.add(field2)
				continue
			}

			val diff = FieldDiff(old, new, field1, field2)
			when {
				diff.affinityChanged -> affinityChanged.add(diff)
				diff.nullabilityChanged -> nullabilityChanged.add(diff)
				else -> same.add(diff)
			}
		}
		removed.addAll(fields1Map.values)
	}

}
