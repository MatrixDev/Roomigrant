package dev.matrix.roomigrant.test

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author matrixdev
 */
@Entity(tableName = "indices", indices = [Index(name = "id_index", value = ["id"])])
class IndicesDbo {
    @PrimaryKey
    var id = ""
}
