package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author matrixdev
 */
@JsonClass(generateAdapter = true)
data class Field(
		@Json(name ="fieldPath")
		val fieldPath: String,

		@Json(name ="columnName")
		val name: String,

		@Json(name ="affinity")
		val affinity: String,

		@Json(name ="notNull")
		val notNull: Boolean) {

	val defaultSqlValue: String
		get() {
			if (!notNull) {
				return "NULL"
			}
			return when (affinity) {
				"INTEGER" -> "0"
				"REAL" -> "0.0"
				else -> "''"
			}
		}

	val definition: String
		get() {
			val sb = StringBuilder("`$name` $affinity")
			if (notNull) {
				sb.append(" NOT NULL")
			}
			return sb.append(" DEFAULT ").append(defaultSqlValue).toString()
		}

	override fun hashCode() = name.hashCode()
	override fun equals(other: Any?): Boolean {
		return other is Field &&
				notNull == other.notNull &&
				affinity == other.affinity &&
				name == other.name

	}
}
