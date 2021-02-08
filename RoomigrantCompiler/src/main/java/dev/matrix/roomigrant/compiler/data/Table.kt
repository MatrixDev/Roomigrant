package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class Table(
		@Json(name = "tableName")
		val name: String,

		@Json(name = "createSql")
		val createSqlTemplate: String,

		@Json(name = "primaryKey")
		val primaryKey: PrimaryKey,

		@Json(name = "fields")
		val fields: List<Field>,

		@Json(name = "indices")
		val indices: List<Index>) {

    val fieldsMap by lazy { fields.associateBy { it.name } }
    fun createSql(tableName: String = name) = createSqlTemplate.replace("\${TABLE_NAME}", tableName)

    constructor(table: Table, name: String) : this(
			name = name,
			createSqlTemplate = table.createSqlTemplate,
			primaryKey = table.primaryKey,
			fields = table.fields,
			indices = table.indices)

}
