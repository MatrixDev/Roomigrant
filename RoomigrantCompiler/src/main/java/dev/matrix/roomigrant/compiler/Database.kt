package dev.matrix.roomigrant.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.roomigrant.compiler.data.Scheme
import dev.matrix.roomigrant.compiler.rules.Rules
import dev.matrix.roomigrant.model.IndexInfo
import dev.matrix.roomigrant.model.SchemeInfo
import dev.matrix.roomigrant.model.TableInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

/**
 * @author matrixdev
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "FunctionName")
class Database(val environment: ProcessingEnvironment, element: TypeElement) {

	val migrationType = ClassName("androidx.room.migration", "Migration")
	val sqLiteDatabaseType = ClassName("androidx.sqlite.db", "SupportSQLiteDatabase")
	val migrationListType = ParameterizedTypeName.get(ArrayList::class.asClassName(), migrationType)
	val migrationArrayType = ParameterizedTypeName.get(ClassName("kotlin", "Array"), migrationType)

	val packageName = element.asClassName().packageName()
	val elementClassName = element.asClassName().simpleName()
	val migrationListClassName = ClassName(packageName, "${elementClassName}_Migrations")

	val rules = Rules(this, element)
	val schemes = ArrayList<Scheme>()
	val migrations = ArrayList<Migration>()

	fun addScheme(scheme: Scheme) {
		schemes.add(scheme)
	}

	fun addMigration(database1: Scheme, database2: Scheme): Migration {
		return Migration(this, database1, database2).also { migrations.add(it) }
	}

	fun generate() {
		val typeSpec = TypeSpec.objectBuilder(migrationListClassName)
				.addProperties(generate_rules())
				.addFunction(generate_build())
				.addFunction(generate_buildScheme())

		val fileSpec = FileSpec.builder(packageName, migrationListClassName.simpleName())
				.addType(typeSpec.build())
				.build()

		environment.filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, "${migrationListClassName.simpleName()}.kt")
				.openWriter()
				.use { fileSpec.writeTo(it) }
	}

	private fun generate_rules() = rules.getProvidersFields().map {
		PropertySpec.builder(it.name, it.type)
				.initializer("%T()", it.type)
				.build()
	}

	private fun generate_build(): FunSpec {
		val funcSpec = FunSpec.builder("build").addStatement("val list = %T()", migrationListType)
		for (migration in migrations) {
			funcSpec.addStatement("list.add(%T)", migration.className)
		}
		funcSpec.returns(migrationArrayType).addStatement("return list.toTypedArray()")
		return funcSpec.build()
	}

	private fun generate_buildScheme(): FunSpec {
		val code = FunSpec.builder("buildScheme")
				.returns(ParameterizedTypeName.get(Map::class, Int::class, SchemeInfo::class))

		var varIndex = 0

		val schemesVar = "schemes"
		val schemesType = ParameterizedTypeName.get(HashMap::class, Int::class, SchemeInfo::class)
		code.addStatement("val %L = %T()", schemesVar, schemesType)

		val schemeVar = "scheme"
		val schemeType = SchemeInfo::class
		code.addStatement("var %L: %T", schemeVar, schemeType)

		val tablesVar = "tables"
		val tablesType = ParameterizedTypeName.get(HashMap::class, String::class, TableInfo::class)
		code.addStatement("var %L: %T", tablesVar, tablesType)

		val tableVar = "table"
		val tableType = TableInfo::class
		code.addStatement("var %L: %T", tableVar, tableType)

		val indicesVar = "indices"
		val indicesType = ParameterizedTypeName.get(HashMap::class, String::class, IndexInfo::class)
		code.addStatement("var %L: %T", indicesVar, indicesType)

		for (scheme in schemes) {
			code.addStatement("%L = %T()", tablesVar, tablesType)
			code.addStatement("%L = %T(%L, %L)", schemeVar, SchemeInfo::class, scheme.version, tablesVar)
			code.addStatement("%L.put(%L, %L)", schemesVar, scheme.version, schemeVar)

			for (table in scheme.tables) {
				code.addStatement("%L = %T()", indicesVar, indicesType)
				code.addStatement("%L = %T(%L, %S, %S, %L)", tableVar, TableInfo::class, schemeVar, table.name, table.createSql(), indicesVar)
				code.addStatement("%L.put(%S, %L)", tablesVar, table.name, tableVar)

				for (index in table.indices) {
					code.addStatement("%L.put(%S, %T(%L, %S, %S))", indicesVar, index.name, IndexInfo::class, tableVar, index.name, index.createSql(table.name))
				}
			}
		}

		code.addStatement("return %L", schemesVar)

		return code.build()
	}

}
