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

        val oldIndicesNameMap = table1.indices.associateByTo(mutableMapOf()) {

			it.columns.sorted().map { columnName ->
				val field = table1.fieldsMap[columnName] ?: error("unable to find field $columnName")
				IndexColumnAffinity(field.name, field.affinity, field.notNull)
			}
		}

		for (newIndex in table2.indices) {

			val pairs = newIndex.columns.sorted().map { columnName ->
				val field = table2.fieldsMap[columnName] ?: error("unable to find field $columnName")

				IndexColumnAffinity(field.name, field.affinity, field.notNull)
			}

			val oldIndex = oldIndicesNameMap.remove(pairs)

			when (oldIndex) {
                null -> added.add(newIndex)
                newIndex -> same.add(newIndex)
				else     -> changed.add(newIndex)
			}
		}
		removed.addAll(oldIndicesNameMap.values)
	}

	data class IndexColumnAffinity(val name: String, val affinity: String, val notNull: Boolean)

}
