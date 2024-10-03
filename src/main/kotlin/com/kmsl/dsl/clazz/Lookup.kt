package com.kmsl.dsl.clazz

import com.kmsl.dsl.extension.fieldName
import org.bson.Document
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

data class Lookup(
    val from: String,
    val localField: String,
    val foreignField: String,
    val alias: String,
    val matchDocument: Document,
    val joinedClasses: List<KClass<*>> = emptyList(),
) {
    val joinedClassesNonDuplicatedFieldNames: Array<String>
        get() =
            joinedClasses
                .map { clazz ->
                    clazz.java.declaredFields.mapNotNull { field ->
                        if (field.name != COMPANION) field.fieldName else null
                    }
                }
                .flatten()
                .groupingBy { it }
                .eachCount()
                .filter { it.value == 1 }
                .keys
                .toList()
                .toTypedArray()

    val duplicatedFieldNames
        get() =
            joinedClasses
                .map { clazz ->
                    clazz.java.declaredFields.mapNotNull { field ->
                        if (field.name != COMPANION) field.fieldName else null
                    }
                }
                .flatten()
                .groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
                .keys
                .toList()

    val firstClassName
        get() = joinedClasses.first().className()

    val lastClassName
        get() = joinedClasses.last().className()

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