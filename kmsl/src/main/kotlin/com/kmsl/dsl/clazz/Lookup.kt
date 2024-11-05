package com.kmsl.dsl.clazz

import com.kmsl.dsl.extension.fieldName
import org.bson.Document
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

data class Lookup<T : Any>(
    val from: String,
    val localField: String,
    val foreignField: String,
    val alias: String,
    val matchDocument: Document,
    val joinedClass: KClass<T>,
) {
    val duplicatedFieldNames
        get() =
            joinedClass.java.declaredFields.mapNotNull {
                if (it.name != COMPANION) it.fieldName else null
            }.groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
                .keys
                .toList()

    val firstClassName
        get() = joinedClass.className()

    val lastClassName
        get() = joinedClass.className()

    private fun KClass<*>.className() =
        this.findAnnotations<org.springframework.data.mongodb.core.mapping.Document>().first().collection

    private fun List<String>.mangling(): Array<String> {
        val fieldCountMap = mutableMapOf<String, Int>()

        return this.map { fieldName ->
            val count = fieldCountMap.getOrDefault(fieldName, 0)
            fieldCountMap[fieldName] = count + 1
            if (count == 0) fieldName else "${fieldName}_$count"
        }.toTypedArray()
    }

    companion object {
        const val COMPANION = "Companion"
    }
}