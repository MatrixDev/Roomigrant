package dev.matrix.roomigrant.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.roomigrant.compiler.data.Scheme
import dev.matrix.roomigrant.compiler.rules.Rules
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

/**
 * @author matrixdev
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class Database(val environment: ProcessingEnvironment, element: TypeElement) {

	val migrationType = ClassName("android.arch.persistence.room.migration", "Migration")
	val sqLiteDatabaseType = ClassName("android.arch.persistence.db", "SupportSQLiteDatabase")
	val migrationListType = ParameterizedTypeName.get(ArrayList::class.asClassName(), migrationType)
	val migrationArrayType = ParameterizedTypeName.get(ClassName("kotlin", "Array"), migrationType)

	val packageName = element.asClassName().packageName()
	val elementClassName = element.asClassName().simpleName()
	val migrationListClassName = ClassName(packageName, "${elementClassName}_Migrations")

	val rules = Rules(this, element)
	val migrations = ArrayList<Migration>()

	fun addMigration(database1: Scheme, database2: Scheme): Migration {
		return Migration(this, database1, database2).also { migrations.add(it) }
	}

	fun generate() {
		// migration class
		val typeSpec = TypeSpec.objectBuilder(migrationListClassName)

		// "rules" fields
		for (holder in rules.getProvidersFields()) {
			typeSpec.addProperty(holder.propertySpec)
		}

		// "build" function
		val funcSpec = FunSpec.builder("build").addStatement("val list = %T()", migrationListType)
		for (migration in migrations) {
			funcSpec.addStatement("list.add(%T)", migration.className)
		}
		funcSpec.returns(migrationArrayType).addStatement("return list.toTypedArray()")
		typeSpec.addFunction(funcSpec.build())

		// writing to file
		val fileSpec = FileSpec.builder(packageName, migrationListClassName.simpleName())
				.addType(typeSpec.build())
				.build()

		environment.filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, "${migrationListClassName.simpleName()}.kt")
				.openWriter()
				.use { fileSpec.writeTo(it) }
	}

}
