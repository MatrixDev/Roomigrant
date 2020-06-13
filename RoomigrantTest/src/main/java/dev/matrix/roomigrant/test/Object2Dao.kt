package dev.matrix.roomigrant.test

import androidx.room.Dao
import androidx.room.Insert

/**
 * @author matrixdev
 */
@Dao
abstract class Object2Dao {

	@Insert
	abstract fun insert(dbo: Object2Dbo)

}
