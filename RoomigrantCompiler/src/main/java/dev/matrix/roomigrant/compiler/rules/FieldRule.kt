package dev.matrix.roomigrant.compiler.rules

import com.squareup.kotlinpoet.CodeBlock

/**
 * @author matrixdev
 */
data class FieldRule(
		override val version1: Int,
		override val version2: Int,
		val field: RulesProviderField,
		val methodName: String) : RuleByVersion {

    val invokeCode = CodeBlock.of("%T.%L.%L()", field.database.migrationListClassName, field.name, methodName)
}
