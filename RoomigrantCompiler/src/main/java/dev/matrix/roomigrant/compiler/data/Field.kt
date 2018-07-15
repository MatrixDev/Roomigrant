package dev.matrix.roomigrant.compiler.data

import com.google.gson.annotations.SerializedName

/**
 * @author matrixdev
 */
data class Field(
		@SerializedName("fieldPath")
		val fieldPath: String,

		@SerializedName("columnName")
		val name: String,

		@SerializedName("affinity")
		val affinity: String,

		@SerializedName("notNull")
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