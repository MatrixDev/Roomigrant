package dev.matrix.roomigrant.test

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.matrix.roomigrant.GenerateRoomMigrations

/**
 * @author matrixdev
 */
@Database(version = 5, entities = [Object1Dbo::class])
@GenerateRoomMigrations(Rules::class)
abstract class Database : RoomDatabase() {
	abstract val object1Dao: Object1Dao
}
