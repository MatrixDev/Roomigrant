package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class Index(
        @Json(name = "name")
        val name: String,

        @Json(name = "unique")
        val unique: Boolean,

        @Json(name = "createSql")
        val createSqlTemplate: String,

        @Json(name = "columnNames")
        val columns: List<String>) {

    fun createSql(tableName: String) = createSqlTemplate.replace("\${TABLE_NAME}", tableName)

}
