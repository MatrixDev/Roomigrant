package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class Index(
		@SerializedName("name")
		val name: String,

		@SerializedName("unique")
		val unique: Boolean,

		@SerializedName("createSql")
		val createSql: String,

		@SerializedName("columnNames")
		val columns: List<String>)