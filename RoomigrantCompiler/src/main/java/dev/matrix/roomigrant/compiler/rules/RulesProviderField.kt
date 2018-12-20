package dev.matrix.roomigrant.compiler.rules

import com.squareup.kotlinpoet.TypeName
import dev.matrix.roomigrant.compiler.Database

/**
 * @author matrixdev
 */
data class RulesProviderField(
		val database: Database,
		val name: String,
		val type: TypeName)
