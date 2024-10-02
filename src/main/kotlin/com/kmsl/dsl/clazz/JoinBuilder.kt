package com.kmsl.dsl.clazz

import com.kmsl.dsl.clazz.FieldName.ID
import org.bson.Document
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class JoinBuilder(
    val document: Document,
) {
    inline infix fun <reified T : Any, R, reified K : Any> Field<T, R>.eq(
        targetField: Field<K, R>,
    ): Lookup {
        val from = targetField.getClassName(K::class) ?: error("has no class name")
        val fieldName = key.fieldName
        val targetFieldName = targetField.key.fieldName

        return Lookup(
            from = from,
            localField = fieldName,
            foreignField = targetFieldName,
            alias = from,
            matchDocument = document,
        )
    }

    val <T, V> KProperty1<T, V>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            val hasFieldAnnotation = this.annotations.any { it is org.springframework.data.mongodb.core.mapping.Field }
            val hasSpringDataIdAnnotation = this.annotations.any { it is Id }
            val hasJakartaIdAnnotation = this.annotations.any { it is jakarta.persistence.Id }

            return when {
                hasFieldAnnotation -> javaField.annotations
                    .filterIsInstance<org.springframework.data.mongodb.core.mapping.Field>()
                    .first().value
                hasSpringDataIdAnnotation -> ID
                hasJakartaIdAnnotation -> ID
                else -> this.toDotPath()
            }
        }
}