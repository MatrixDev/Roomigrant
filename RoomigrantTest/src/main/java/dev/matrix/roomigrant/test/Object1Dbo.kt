package dev.matrix.roomigrant.test

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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