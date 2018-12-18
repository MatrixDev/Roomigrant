package dev.matrix.roomigrant.rules

/**
 * @author matrixdev
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class FieldMigrationRule(
		val version1: Int,
		val version2: Int,
		val table: String,
		val field: String)
