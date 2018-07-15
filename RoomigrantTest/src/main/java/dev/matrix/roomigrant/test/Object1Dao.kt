package dev.matrix.roomigrant.test

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

/**
 * @author matrixdev
 */
@Dao
abstract class Object1Dao {

	@Insert
	abstract fun insert(dbo: Object1Dbo)

}