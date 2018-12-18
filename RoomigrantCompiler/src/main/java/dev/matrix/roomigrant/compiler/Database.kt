package dev.matrix.roomigrant.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.roomigrant.AfterMigrationRule
import dev.matrix.roomigrant.BeforeMigrationRule
import dev.matrix.roomigrant.FieldMigrationRule
import dev.matrix.roomigrant.GenerateRoomMigrations
import dev.matrix.roomigrant.compiler.data.Scheme
import dev.matrix.roomigrant.compiler.rules.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypesException
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

	val rules = Rules()
	val migrations = ArrayList<Migration>()
	val rulesHolderList = ArrayList<RulesHolder>()

	init {
		try {
			// TODO handle properly without exception
			element.getAnnotation(GenerateRoomMigrations::class.java).rules[0]
		} catch (e: MirroredTypesException) {
			for (ruleClass in e.typeMirrors.filterIsInstance(DeclaredType::class.java)) {
				val holder = RulesHolder(this, "rule" + rulesHolderList.size, ruleClass.asTypeName())

				for (method in ruleClass.asElement().enclosedElements) {
					if (method.kind != ElementKind.METHOD) {
						continue
					}

					method.getAnnotation(FieldMigrationRule::class.java)?.also {
						val rule = FieldRule(this, holder, method.simpleName.toString())
						rules.putFieldRule(it.version1, it.version2, it.table, it.field, rule)
					}

					method.getAnnotation(BeforeMigrationRule::class.java)?.also {
						val rule = LifecycleRule(this, holder, method.simpleName.toString())
						rules.putBeforeRule(it.version1, it.version2, rule)
					}

					method.getAnnotation(AfterMigrationRule::class.java)?.also {
						val rule = LifecycleRule(this, holder, method.simpleName.toString())
						rules.putAfterRule(it.version1, it.version2, rule)
					}
				}

				rulesHolderList.add(holder)
			}
		}
	}

	fun addMigration(database1: Scheme, database2: Scheme): Migration {
		return Migration(this, database1, database2).also { migrations.add(it) }
	}

	fun generate() {
		val typeSpec = TypeSpec.objectBuilder(migrationListClassName)

		for (holder in rulesHolderList) {
			typeSpec.addProperty(PropertySpec.builder(holder.name, holder.className)
					.initializer("%T()", holder.className)
					.build())
		}

		val funcSpec = FunSpec.builder("build").addStatement("val list = %T()", migrationListType)
		for (migration in migrations) {
			funcSpec.addStatement("list.add(%T)", migration.className)
		}
		funcSpec.returns(migrationArrayType).addStatement("return list.toTypedArray()")

		val fileSpec = FileSpec.builder(packageName, migrationListClassName.simpleName())
				.addType(typeSpec.addFunction(funcSpec.build()).build())
				.build()

		environment.filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, "${migrationListClassName.simpleName()}.kt")
				.openWriter()
				.use {
					fileSpec.writeTo(it)
				}
	}

}