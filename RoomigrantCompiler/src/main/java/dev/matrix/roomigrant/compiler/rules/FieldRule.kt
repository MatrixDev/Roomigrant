package dev.matrix.roomigrant.compiler.rules

import dev.matrix.roomigrant.compiler.Database
import com.squareup.kotlinpoet.CodeBlock

/**
 * @author matrixdev
 */
data class FieldRule(
		val state: Database,
		val holder: RulesHolder,
		val methodName: String) {

	val invokeCode = CodeBlock.of("%T.%L.%L()", state.migrationListClassName, holder.name, methodName)
	val invokeCodeWrapped = "\${$invokeCode}"

}