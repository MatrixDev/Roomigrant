package dev.matrix.roomigrant.test

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.matrix.roomigrant.GenerateRoomMigrations

/**
 * @author matrixdev
 *
 * 7  - Add ObjectDboView
 * 8  - Add twoId to ObjectDboView
 */
@Database(
        version = 8,
        entities = [Object1Dbo::class, Object2Dbo::class],
        views = [ObjectDboView::class]
)
@GenerateRoomMigrations(Rules::class)
abstract class Database : RoomDatabase() {
    abstract val object1Dao: Object1Dao
    abstract val object2Dao: Object2Dao
}
