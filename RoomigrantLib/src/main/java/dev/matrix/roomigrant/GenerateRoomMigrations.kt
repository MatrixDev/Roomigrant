package dev.matrix.roomigrant

import kotlin.reflect.KClass

/**
 * @author matrixdev
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateRoomMigrations(vararg val rules: KClass<*>)
