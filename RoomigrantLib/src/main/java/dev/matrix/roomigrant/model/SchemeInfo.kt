package dev.matrix.roomigrant.model

/**
 * @author matrixdev
 */
data class SchemeInfo(
		val version: Int,
		val tables: Map<String, TableInfo>)
