package dev.matrix.roomigrant.test.utils

import android.database.Cursor
import androidx.annotation.Keep
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Rostyslav.Lesovyi
 */
@Suppress("unused")
@Keep
object SqlUtils {
    fun dumpMasterInfo(database: SupportSQLiteDatabase): String{
        val json = JSONArray()
        val cursor = database.query("SELECT * FROM sqlite_master")

        for (index in 0 until cursor.count) {
            val record = JSONObject().also(json::put)

            cursor.moveToPosition(index)

            for (columnIndex in 0 until cursor.columnCount) {
                when (cursor.getType(columnIndex)) {
                    Cursor.FIELD_TYPE_INTEGER -> {
                        record.put(cursor.getColumnName(columnIndex), cursor.getInt(columnIndex))
                    }
                    Cursor.FIELD_TYPE_FLOAT -> {
                        record.put(cursor.getColumnName(columnIndex), cursor.getFloat(columnIndex))
                    }
                    Cursor.FIELD_TYPE_STRING -> {
                        record.put(cursor.getColumnName(columnIndex), cursor.getString(columnIndex))
                    }
                    Cursor.FIELD_TYPE_NULL -> {
                        record.put(cursor.getColumnName(columnIndex), "null")
                    }
                    Cursor.FIELD_TYPE_BLOB -> {
                        val size = cursor.getBlob(columnIndex).size
                        record.put(cursor.getColumnName(columnIndex), "bloc($size)")
                    }
                }
            }
        }
        return json.toString(4)
    }
}
