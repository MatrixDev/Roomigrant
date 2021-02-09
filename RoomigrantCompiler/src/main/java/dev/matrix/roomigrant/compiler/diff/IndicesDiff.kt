package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Index
import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class IndicesDiff(val table1: Table?, val table2: Table) {

    private data class IndexIdentity(val fields: Set<IndexFieldIdentity>)
    private data class IndexFieldIdentity(val name: String, val affinity: String, val notNull: Boolean)

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

        val oldIndicesMap = table1.indices.associateByTo(HashMap()) {
            buildIndexIdentity(table1, it)
        }

        for (newIndex in table2.indices) {
            val newIndexIdentity = buildIndexIdentity(table2, newIndex)
            when (oldIndicesMap.remove(newIndexIdentity)) {
                null -> added.add(newIndex)
                newIndex -> same.add(newIndex)
                else -> changed.add(newIndex)
            }
        }
        removed.addAll(oldIndicesMap.values)
    }

    private fun buildIndexIdentity(table: Table, index: Index): IndexIdentity {
        val fields = index.columns.mapTo(HashSet()) {
            buildIndexFieldIdentity(table, it)
        }
        return IndexIdentity(fields = fields)
    }

    private fun buildIndexFieldIdentity(table: Table, columnName: String): IndexFieldIdentity {
        val field = checkNotNull(table.fieldsMap[columnName]) {
            "unable to find field ${table.name}.$columnName"
        }
        return IndexFieldIdentity(field.name, field.affinity, field.notNull)
    }
}
