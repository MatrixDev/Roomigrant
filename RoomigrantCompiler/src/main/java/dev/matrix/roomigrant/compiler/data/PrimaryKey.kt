package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class PrimaryKey(
		@Json(name ="autoGenerate")
		val autoGenerate: Boolean,

		@Json(name ="columnNames")
		val columnNames: List<String>)
