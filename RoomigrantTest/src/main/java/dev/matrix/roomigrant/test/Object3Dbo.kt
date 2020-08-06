package dev.matrix.roomigrant.test

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("prop1", "prop2")])
data class Object3Dbo(val prop1: Int,
                      val prop2: Int,
                      @PrimaryKey(autoGenerate = false) val id: Int)
