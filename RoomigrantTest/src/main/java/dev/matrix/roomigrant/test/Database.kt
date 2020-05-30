package dev.matrix.roomigrant.test

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.matrix.roomigrant.GenerateRoomMigrations

/**
 * @author matrixdev
 */
@Database(version = 7, entities = [Object1Dbo::class, Object2Dbo::class])
@GenerateRoomMigrations(Rules::class)
abstract class Database : RoomDatabase() {
    abstract val object1Dao: Object1Dao
    abstract val object2Dao: Object2Dao
}
