package dev.matrix.roomigrant.compiler.rules

/**
 * @author matrixdev
 */
class Rules {

	private val fieldRules = HashMap<String, FieldRule>()
	private val beforeRules = HashMap<String, LifecycleRule>()
	private val afterRules = HashMap<String, LifecycleRule>()

	fun getFieldRule(version1: Int, version2: Int, table: String, field: String): FieldRule? {
		return fieldRules[packFieldRuleKey(version1, version2, table, field)]
	}

	fun putFieldRule(version1: Int, version2: Int, table: String, field: String, rule: FieldRule) {
		fieldRules[packFieldRuleKey(version1, version2, table, field)] = rule
	}

	fun getBeforeRule(version1: Int, version2: Int): LifecycleRule? {
		return beforeRules[packLifecycleRuleKey(version1, version2)]
	}

	fun putBeforeRule(version1: Int, version2: Int, rule: LifecycleRule) {
		beforeRules[packLifecycleRuleKey(version1, version2)] = rule
	}

	fun getAfterRule(version1: Int, version2: Int): LifecycleRule? {
		return afterRules[packLifecycleRuleKey(version1, version2)]
	}

	fun putAfterRule(version1: Int, version2: Int, rule: LifecycleRule) {
		afterRules[packLifecycleRuleKey(version1, version2)] = rule
	}

	private fun packLifecycleRuleKey(version1: Int, version2: Int) =
			"$version1-$version2"

	private fun packFieldRuleKey(version1: Int, version2: Int, table: String, field: String) =
			"$version1-$version2-$table-$field"

}
