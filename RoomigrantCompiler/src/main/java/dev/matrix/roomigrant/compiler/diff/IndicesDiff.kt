package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Index
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class IndicesDiff(val old: Table?, val new: Table) {

    val same = ArrayList<Index>()
    val added = ArrayList<Index>()
    val removed = ArrayList<Index>()

    val wasChanged: Boolean
        get() = added.isNotEmpty() || removed.isNotEmpty()

    init {
        init()
    }

    fun init() {
        if (old == null) {
            added.addAll(new.indices)
            return
        }

        val oldIndicesNameMap = old.indices.associateByTo(mutableMapOf()) {

            it.columns.sorted().map { columnName ->
                val field = old.fieldsMap[columnName] ?: error("unable to find field $columnName")
                IndexColumnAffinity(field.name, field.affinity, field.notNull)
            }
        }

        for (newIndex in new.indices) {

            val pairs = newIndex.columns.sorted().map { columnName ->
                val field  = new.fieldsMap[columnName] ?: error("unable to find field $columnName")

                 IndexColumnAffinity(field.name, field.affinity, field.notNull)
            }

            val oldIndex = oldIndicesNameMap.remove(pairs)

            when (oldIndex) {
                null -> added.add(newIndex)
                newIndex -> same.add(newIndex)
                else -> {
                    added.add(newIndex)
                    removed.add(oldIndex)
                }
            }
        }
        removed.addAll(oldIndicesNameMap.values)
    }

    data class IndexColumnAffinity(val name: String, val affinity: String, val notNull: Boolean)

}
