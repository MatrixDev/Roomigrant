package dev.matrix.roomigrant.compiler.rules

import com.squareup.kotlinpoet.CodeBlock

/**
 * @author matrixdev
 */
data class InvokeRule(
		override val version1: Int,
		override val version2: Int,
		val field: RulesProviderField,
		val methodName: String) : RuleByVersion {

    fun getInvokeCode(databaseArgName: String, version1: Int, version2: Int) =
            CodeBlock.of("%T.%L.%L($databaseArgName, $version1, $version2)", field.database.migrationListClassName, field.name, methodName)
}
