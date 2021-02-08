package dev.matrix.roomigrant.compiler.diff

import dev.matrix.roomigrant.compiler.data.View

@Suppress("MemberVisibilityCanBePrivate")
class ViewDiff(val old: View?, val new: View) {
    val nameChanged = old?.name != new.name
    val sqlChanged = old?.createSqlTemplate != new.createSqlTemplate

    val wasChanged: Boolean
        get() = sqlChanged || nameChanged
}
