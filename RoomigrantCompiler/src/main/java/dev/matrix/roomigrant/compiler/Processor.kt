package dev.matrix.roomigrant.compiler

import com.google.gson.Gson
import com.squareup.kotlinpoet.asClassName
import dev.matrix.roomigrant.GenerateRoomMigrations
import dev.matrix.roomigrant.compiler.data.Root
import java.io.File
import java.io.InputStreamReader
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import android.arch.persistence.room.Database as DatabaseAnnotation

/**
 * @author matrixdev
 */
class Processor : AbstractProcessor() {

	override fun getSupportedSourceVersion() = SourceVersion.latestSupported()!!
	override fun getSupportedAnnotationTypes() = mutableSetOf(GenerateRoomMigrations::class.java.name)

	override fun process(annotations: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
		val schemaLocation = processingEnv.options["room.schemaLocation"] ?: return true
		val elements = roundEnvironment.getElementsAnnotatedWith(GenerateRoomMigrations::class.java)
				.filterIsInstance<TypeElement>()

		for (element in elements) {
			if (element.getAnnotation(DatabaseAnnotation::class.java) == null) {
				throw Exception("$element is not annotated with ${DatabaseAnnotation::class.simpleName}")
			}
			processDatabase(schemaLocation, element)
		}
		return true
	}

	private fun processDatabase(schemaLocation: String, element: TypeElement) {
		val folder = File(schemaLocation, element.asClassName().toString())
		val schemes = folder.listFiles().mapNotNull { readScheme(it) }.sortedBy { it.version }

		val database = Database(processingEnv, element)
		for (index in 1 until schemes.size) {
			database.addMigration(schemes[index - 1], schemes[index]).generate()
		}
		database.generate()
	}

	private fun readScheme(file: File) = try {
		InputStreamReader(file.inputStream()).use {
			Gson().fromJson(it, Root::class.java).scheme
		}
	} catch (e: Exception) {
		null
	}
}