package dev.matrix.roomigrant.test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author matrixdev
 */
@Entity(tableName = "special-chars")
class SpecialCharsDbo {
	@PrimaryKey
	@ColumnInfo(name = "primary-id")
	var id = ""
}
