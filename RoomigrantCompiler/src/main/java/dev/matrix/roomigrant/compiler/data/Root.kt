package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class Root(
		@SerializedName("formatVersion")
		val formatVersion: Int,

		@SerializedName("database")
		val scheme: Scheme
)