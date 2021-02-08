package dev.matrix.roomigrant.compiler.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class View(
		@Json(name = "viewName")
		val name: String,

		@Json(name = "createSql")
		val createSqlTemplate: String) {

	fun createSql(viewName: String = name) = createSqlTemplate.replace("\${VIEW_NAME}", viewName)
}
