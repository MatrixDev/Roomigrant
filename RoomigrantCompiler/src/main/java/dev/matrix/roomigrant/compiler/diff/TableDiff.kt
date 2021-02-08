package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.Table

/**
 * @author matrixdev
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TableDiff(val table1: Table?, val table2: Table) {
    val fieldsDiff = FieldsDiff(table1, table2)
    val indicesDiff = IndicesDiff(table1, table2)
    val primaryKeyChanged = table1?.primaryKey != table2.primaryKey

    val wasChanged: Boolean
        get() = primaryKeyChanged || fieldsDiff.wasChanged || indicesDiff.wasChanged
}