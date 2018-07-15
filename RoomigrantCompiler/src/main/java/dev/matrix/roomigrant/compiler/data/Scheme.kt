package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class Scheme(
		@SerializedName("version")
		val version: Int,

		@SerializedName("entities")
		val tables: List<Table>)