package dev.matrix.roomigrant.compiler.rules

import com.squareup.kotlinpoet.asTypeName
import dev.matrix.roomigrant.rules.FieldMigrationRule
import dev.matrix.roomigrant.GenerateRoomMigrations
import dev.matrix.roomigrant.compiler.Database
import dev.matrix.roomigrant.rules.OnMigrationEndRule
import dev.matrix.roomigrant.rules.OnMigrationStartRule
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypesException

/**
 * @author matrixdev
 */
class Rules(private val database: Database, element: TypeElement) {

    private val providers = ArrayList<RulesProviderField>()
    private val onEndRules = ArrayList<InvokeRule>()
    private val onStartRules = ArrayList<InvokeRule>()
    private val fieldRules = HashMap<String, HashMap<String, ArrayList<FieldRule>>>()

    init {
        try {
            // TODO handle properly without exception
            element.getAnnotation(GenerateRoomMigrations::class.java).rules[0]
            throw NotImplementedError("MirroredTypesException was not thrown")
        } catch (e: MirroredTypesException) {
            e.typeMirrors.asSequence().filterIsInstance<DeclaredType>().forEach { parseRulesClass(it) }
        }
    }

    private fun parseRulesClass(rulesClass: DeclaredType) {
        val field = RulesProviderField(database, name = "rule" + providers.size, type = rulesClass.asTypeName())

        for (method in rulesClass.asElement().enclosedElements) {
            if (method.kind != ElementKind.METHOD) {
                continue
            }

            method.getAnnotation(FieldMigrationRule::class.java)?.also {
                fieldRules.getOrPut(it.table) { HashMap() }.getOrPut(it.field) { ArrayList() }
                        .add(FieldRule(it.version1, it.version2, field, method.simpleName.toString()))
            }

            method.getAnnotation(OnMigrationEndRule::class.java)?.also {
                onEndRules.add(InvokeRule(it.version1, it.version2, field, method.simpleName.toString()))
            }

            method.getAnnotation(OnMigrationStartRule::class.java)?.also {
                onStartRules.add(InvokeRule(it.version1, it.version2, field, method.simpleName.toString()))
            }
        }

        providers.add(field)
    }

    fun getProvidersFields(): List<RulesProviderField> {
        return providers
    }

    fun getFieldRule(version1: Int, version2: Int, table: String, field: String): FieldRule? {
        return fieldRules[table]?.get(field)?.find { it.checkVersion(version1, version2) }
    }

    fun getOnEndRules(version1: Int, version2: Int): Sequence<InvokeRule> {
        return onEndRules.asSequence().filter { it.checkVersion(version1, version2) }
    }

    fun getOnStartRules(version1: Int, version2: Int): Sequence<InvokeRule> {
        return onStartRules.asSequence().filter { it.checkVersion(version1, version2) }
    }

}
