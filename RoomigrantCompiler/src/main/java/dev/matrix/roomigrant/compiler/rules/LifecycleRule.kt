package dev.matrix.roomigrant.compiler.rules

import dev.matrix.roomigrant.compiler.Database
import com.squareup.kotlinpoet.CodeBlock

/**
 * @author matrixdev
 */
data class LifecycleRule(
		val state: Database,
		val holder: RulesHolder,
		val methodName: String) {

	fun getInvokeCode(databaseArgName: String) = CodeBlock.of("%T.%L.%L($databaseArgName)\n", state.migrationListClassName, holder.name, methodName)

}
