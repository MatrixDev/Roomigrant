package dev.matrix.roomigrant.test

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * @author matrixdev
 */
@Entity(tableName = "Object2DBO")
data class Object2Dbo(@PrimaryKey var id: String = "")
