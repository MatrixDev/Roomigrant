package dev.matrix.roomigrant.rules

/**
 * @author matrixdev
 *
 * Function must take following arguments:
 * fun onEvent(database: SupportSQLiteDatabase, version1: Int, version2: Int)
 *
 * @param version1 source migration version (-1 for wildcard)
 * @param version2 target migration version (-1 for wildcard)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnMigrationStartRule(
		val version1: Int = -1,
		val version2: Int = -1)
