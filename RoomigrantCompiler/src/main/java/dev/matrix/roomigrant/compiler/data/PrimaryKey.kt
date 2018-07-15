package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class PrimaryKey(
		@SerializedName("autoGenerate")
		val autoGenerate: Boolean,

		@SerializedName("columnNames")
		val columnNames: List<String>)