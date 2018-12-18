package dev.matrix.roomigrant.compiler.rules

/**
 * @author matrixdev
 */
interface RuleByVersion {
	val version1: Int
	val version2: Int
}

fun <T : RuleByVersion> T.checkVersion(version1: Int, version2: Int): Boolean {
	if (this.version1 != -1 && this.version1 != version1) return false
	if (this.version2 != -1 && this.version2 != version2) return false
	return true
}
