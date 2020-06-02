package dev.matrix.roomigrant.test

import androidx.room.DatabaseView

@DatabaseView("SELECT item.id AS oneId, (SELECT id FROM Object2Dbo WHERE id = item.id) AS twoId  FROM Object1Dbo item")
data class ObjectDboView(
        val oneId: Int,
        val twoId: Int
)
