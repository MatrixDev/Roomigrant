package dev.matrix.roomigrant

/**
 * @author matrixdev
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class AfterMigrationRule(
		val version1: Int,
		val version2: Int)
