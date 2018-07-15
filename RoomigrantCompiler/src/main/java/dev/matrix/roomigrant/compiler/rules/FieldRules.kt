package dev.matrix.roomigrant.compiler.rules

/**
 * @author matrixdev
 */
class FieldRules {

	private val map = HashMap<String, FieldRule>()

	fun get(version1: Int, version2: Int, table: String, field: String): FieldRule? {
		return map[pack(version1, version2, table, field)]
	}

	fun put(version1: Int, version2: Int, table: String, field: String, rule: FieldRule) {
		map[pack(version1, version2, table, field)] = rule
	}

	private fun pack(version1: Int, version2: Int, table: String, field: String) =
			"$version1-$version2-$table-$field"

}