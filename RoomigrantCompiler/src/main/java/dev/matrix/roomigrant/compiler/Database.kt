package dev.matrix.roomigrant.compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "FunctionName", "DEPRECATION")
class Database(val environment: ProcessingEnvironment, element: TypeElement) {

    val migrationType = ClassName("androidx.room.migration", "Migration")
    val sqLiteDatabaseType = ClassName("androidx.sqlite.db", "SupportSQLiteDatabase")
    val migrationListType = ArrayList::class.asClassName().parameterizedBy(migrationType)
    val migrationArrayType = ClassName("kotlin", "Array").parameterizedBy(migrationType)

    val packageName = element.asClassName().packageName
    val elementClassName = element.asClassName().simpleName
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
                .also {
                    schemes.forEach { scheme -> it.addFunction(generate_buildSchemeInfo(scheme)) }
                }

        val fileSpec = FileSpec.builder(packageName, migrationListClassName.simpleName)
                .addType(typeSpec.build())
                .build()

        val fileName = "${migrationListClassName.simpleName}.kt"
        environment.filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName)
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
                .returns(Map::class.parameterizedBy(Int::class, SchemeInfo::class))

        val schemesMap = "schemesMap"
        val schemesType = HashMap::class.parameterizedBy(Int::class, SchemeInfo::class)
        code.addStatement("val %L = %T()", schemesMap, schemesType)

        for (scheme in schemes) {
            val funName = "buildSchemeInfo_${scheme.version}"
            code.addStatement("%L.put(%L, %L())", schemesMap, scheme.version, funName)
        }

        code.addStatement("return %L", schemesMap)

        return code.build()
    }

    private fun generate_buildSchemeInfo(scheme: Scheme): FunSpec {
        val code = FunSpec.builder("buildSchemeInfo_${scheme.version}")
                .addModifiers(KModifier.PRIVATE)
                .returns(SchemeInfo::class)

        val tablesMap = "tables"
        val tablesType = HashMap::class.parameterizedBy(String::class, TableInfo::class)
        code.addStatement("val %L = %T()", tablesMap, tablesType)

        val schemeInfo = "schemeInfo"
        code.addStatement("val %L = %T(%L, %L)", schemeInfo, SchemeInfo::class, scheme.version, tablesMap)

        code.addStatement("")

        for (table in scheme.tables) {
            val tableInfo = "tableInfo_${table.name}"
            val indices = "indices_${table.name}"

            val indicesType = HashMap::class.parameterizedBy(String::class, IndexInfo::class)
            code.addStatement("val %L = %T()", indices, indicesType)
            code.addStatement("")

            code.addStatement("val %L = %T(%L, %S, %S, %L)", tableInfo, TableInfo::class, schemeInfo, table.name, table.createSql(), indices)
            code.addStatement("%L.put(%S, %L)", tablesMap, table.name, tableInfo)
            code.addStatement("")

            for (index in table.indices) {
                code.addStatement("%L.put(%S, %T(%L, %S, %S))", indices, index.name, IndexInfo::class, tableInfo, index.name, index.createSql(table.name))
            }
        }

        code.addStatement("return %L", schemeInfo)

        return code.build()
    }
}
