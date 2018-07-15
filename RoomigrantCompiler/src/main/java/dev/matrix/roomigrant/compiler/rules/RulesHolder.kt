package dev.matrix.roomigrant.compiler.rules

import dev.matrix.roomigrant.compiler.Database
import com.squareup.kotlinpoet.TypeName

/**
 * @author matrixdev
 */
data class RulesHolder(
		val state: Database,
		val name: String,
		val className: TypeName)