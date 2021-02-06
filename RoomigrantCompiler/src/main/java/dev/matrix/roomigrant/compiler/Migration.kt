package dev.matrix.roomigrant.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.roomigrant.compiler.data.*
import dev.matrix.roomigrant.compiler.diff.SchemeDiff
import dev.matrix.roomigrant.compiler.rules.FieldRule
import java.util.*
import javax.tools.StandardLocation
import kotlin.collections.set

/**
 * @author matrixdev
 */
class Migration(
		private val state: Database,
		private val scheme1: Scheme,
		private val scheme2: Scheme) {

	companion object {
		private const val databaseArgName = "database"
		private const val mergeTableSuffix = "_MERGE_TABLE"
	}

	val className = ClassName(state.packageName, "${state.elementClassName}_Migration_${scheme1.version}_${scheme2.version}")

	private val typeSpec: TypeSpec
	private val fileSpec: FileSpec
	private val funcSpecBuilder = FunSpec.builder("migrate")

	init {
		funcSpecBuilder.addModifiers(KModifier.OVERRIDE)
		funcSpecBuilder.addParameter(databaseArgName, state.sqLiteDatabaseType)

		state.rules.getOnStartRules(scheme1.version, scheme2.version).forEach {
			funcSpecBuilder.addStatement("%L", it.getInvokeCode(databaseArgName, scheme1.version, scheme2.version))
		}

		migrate()

		state.rules.getOnEndRules(scheme1.version, scheme2.version).forEach {
			funcSpecBuilder.addStatement("%L", it.getInvokeCode(databaseArgName, scheme1.version, scheme2.version))
		}

		typeSpec = TypeSpec.objectBuilder(className)
				.superclass(state.migrationType)
				.addSuperclassConstructorParameter("%L, %L", scheme1.version, scheme2.version)
				.addFunction(funcSpecBuilder.build())
				.build()

		fileSpec = FileSpec.builder(state.packageName, className.simpleName)
				.addType(typeSpec)
				.build()
	}

	fun generate() {
		state.environment.filer.createResource(StandardLocation.SOURCE_OUTPUT, state.packageName, "${className.simpleName}.kt")
				.openWriter()
				.use { fileSpec.writeTo(it) }
	}

	private fun migrate() {
		var variableIndex = 0
		val diff = SchemeDiff(scheme1, scheme2)

		for (tableDiff in diff.addedTables) {
			createTable(tableDiff.table2)
			createTableIndices(tableDiff.table2)
		}

		for (tableDiff in diff.changedTables) {
			val table1 = tableDiff.table1
			val table2 = tableDiff.table2

			if (!tableDiff.primaryKeyChanged && tableDiff.fieldsDiff.onlyAdded) {
				val sb = StringBuilder()
				for (field in tableDiff.fieldsDiff.added) {
					val rule = getFieldRule(table2, field)
					if (rule != null) {
						sb.append(" `${field.name}` = ${rule.invokeCode},")
					}
					execSql("ALTER TABLE `${table2.name}` ADD ${field.definition}")
				}
				if (sb.isNotEmpty()) {
					sb.insert(0, "UPDATE `${table2.name}` SET")
					sb.setLength(sb.length - 1)
					execSql(sb.toString())
				}
			} else if (tableDiff.fieldsDiff.wasChanged) {
				val tableMerge = Table(table2, table2.name + mergeTableSuffix)
				createTable(tableMerge)

				fun toSql(rule: FieldRule?, fallback: String): String {
					if (rule == null) {
						return fallback
					}
					val name = "var${++variableIndex}"
					funcSpecBuilder.addStatement("val %L = %L", name, rule.invokeCode)
					return "\$$name"
				}

				val fields = LinkedHashMap<String, String>()
				for (it in tableDiff.fieldsDiff.same) {
					fields[it.field2.name] = toSql(getFieldRule(table2, it.field2), it.copySql)
				}
				for (it in tableDiff.fieldsDiff.added) {
					fields[it.name] = toSql(getFieldRule(table2, it), it.defaultSqlValue)
				}
				for (it in tableDiff.fieldsDiff.affinityChanged) {
					fields[it.field2.name] = toSql(getFieldRule(table2, it.field2), it.castSql)
				}
				for (it in tableDiff.fieldsDiff.nullabilityChanged) {
					fields[it.field2.name] = toSql(getFieldRule(table2, it.field2), it.toNotNullableSql)
				}

				val sb = StringBuilder()
				sb.append("INSERT INTO `").append(tableMerge.name).append("` (")
				fields.keys.forEach { sb.append("`").append(it).append("`,") }

				sb.setLength(sb.length - 1)
				sb.append(") SELECT ")
				fields.values.forEach { sb.append(it).append(",") }

				sb.setLength(sb.length - 1)
				sb.append(" FROM `").append(table1!!.name).append("`")

				execSql(sb.toString())
				dropTable(table1.name)
				renameTable(tableMerge.name, table2.name)
				createTableIndices(table2)
			}

			tableDiff.indicesDiff.changed.forEach {
				dropTableIndex(it)
				createTableIndex(table2, it)
			}
			tableDiff.indicesDiff.added.forEach {
				createTableIndex(table2, it)
			}
			tableDiff.indicesDiff.removed.forEach {
				dropTableIndex(it)
			}
		}

        for (table in diff.removedTables) {
            dropTable(table.name)
        }

        for (viewDiff in diff.addedViews) {
            createView(viewDiff.new)
        }

        for (viewDiff in diff.changedViews) {
            val old = viewDiff.old
            val new = viewDiff.new

            old?.name?.let { dropView(it)}
            createView(new)
        }

        for (view in diff.removedViews) {
            dropView(view.name)
        }
    }

	private fun getFieldRule(table: Table, field: Field): FieldRule? {
		return state.rules.getFieldRule(scheme1.version, scheme2.version, table.name, field.name)
	}

	private fun execSql(query: String) {
		funcSpecBuilder.addStatement("%N.execSQL(%S)", databaseArgName, query)
	}

	private fun dropTable(tableName: String) {
		execSql("DROP TABLE IF EXISTS `$tableName`")
	}

	private fun renameTable(tableName1: String, tableName2: String) {
		execSql("ALTER TABLE `$tableName1` RENAME TO `$tableName2`")
	}

	private fun createTable(table: Table) {
		execSql(table.createSql(table.name))
	}

    private fun dropView(viewName: String) {
        execSql("DROP VIEW IF EXISTS `$viewName`")
    }

    private fun renameView(viewName1: String, viewName2: String) {
        execSql("ALTER VIEW `$viewName1` RENAME TO `$viewName2`")
    }

    private fun createView(view: View) {
        execSql(view.createSql(view.name))
    }

    private fun dropTableIndex(index: Index) {
        execSql("DROP INDEX IF EXISTS ${index.name}")
    }

	private fun createTableIndices(table: Table) {
		table.indices.forEach { createTableIndex(table, it) }
	}

	private fun createTableIndex(table: Table, index: Index) {
		execSql(index.createSql(table.name))
	}
}
