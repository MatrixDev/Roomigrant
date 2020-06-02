package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class Scheme(
        @Json(name = "version")
        val version: Int,

        @Json(name = "entities")
        val tables: List<Table>)
