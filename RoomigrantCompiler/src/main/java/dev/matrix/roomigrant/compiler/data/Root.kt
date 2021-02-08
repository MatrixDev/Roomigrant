package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class Root(
		@Json(name = "formatVersion")
		val formatVersion: Int,

		@Json(name = "database")
		val scheme: Scheme
)
