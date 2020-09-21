package dev.matrix.roomigrant.test

import androidx.room.Dao
import androidx.room.Insert

/**
 * @author matrixdev
 */
@Dao
abstract class Object1Dao {

	@Insert
	abstract fun insert(dbo: Object1Dbo)

}