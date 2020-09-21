package dev.matrix.roomigrant.test

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author matrixdev
 */
@Entity
class Object1Dbo {

	@PrimaryKey
	var id = ""

	var intValRenamed = 0

	var stringValRenamed = ""

	var nullIntVal: Int? = 0

	var nullStringVal: String = ""

}