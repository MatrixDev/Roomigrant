package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class Table(
		@SerializedName("tableName")
		val name: String,

		@SerializedName("createSql")
		val createSql: String,

		@SerializedName("primaryKey")
		val primaryKey: PrimaryKey,

		@SerializedName("fields")
		val fields: List<Field>,

		@SerializedName("indices")
		val indices: List<Index>) {

	val fieldsMap by lazy { fields.associateBy { it.name } }

	constructor(table: Table, name: String) : this(
			name = name,
			createSql = table.createSql,
			primaryKey = table.primaryKey,
			fields = table.fields,
			indices = table.indices)

}