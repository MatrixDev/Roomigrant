package dev.matrix.roomigrant.model

/**
 * @author matrixdev
 */
data class TableInfo(
		val scheme: SchemeInfo,
		val name: String,
		val createSql: String,
		val indices: Map<String, IndexInfo>)
