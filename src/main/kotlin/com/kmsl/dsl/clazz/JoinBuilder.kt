package com.kmsl.dsl.clazz

import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import java.util.UUID
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class JoinBuilder {
    lateinit var alias: String

    inline infix fun <reified T : Any, R, reified K : Any> Field<T, R>.eq(
        targetField: Field<K, R>,
    ): LookupOperation {
        val from = targetField.getClassName(K::class) ?: error("has no class name")
        val fieldName = key.fieldName
        val targetFieldName = targetField.key.fieldName
        alias = UUID.randomUUID().toString().substring(0, 8)

        return Aggregation.lookup(
            from,
            fieldName,
            targetFieldName,
            alias,
        )
    }

    val <T, V> KProperty1<T, V>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            val hasIdAnnotation = javaField.annotations.any { it is Id }
            return if (hasIdAnnotation) {
                val hasFieldAnnotation =
                    javaField.annotations.any { it is org.springframework.data.mongodb.core.mapping.Field }
                if (hasFieldAnnotation) javaField.annotations.filterIsInstance<org.springframework.data.mongodb.core.mapping.Field>()
                    .first().value
                else "_id"
            } else this.toDotPath()
        }
}
