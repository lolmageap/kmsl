package com.kmsl.dsl.clazz

import com.kmsl.dsl.clazz.FieldName.ID
import org.bson.Document
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class JoinBuilder(
    val document: Document,
) {
    lateinit var alias: String

    inline infix fun <reified T : Any, R, reified K : Any> Field<T, R>.eq(
        targetField: Field<K, R>,
    ): Lookup {
        val from = targetField.getClassName(K::class) ?: error("has no class name")
        val fieldName = key.fieldName
        val targetFieldName = targetField.key.fieldName
        alias = UUID.randomUUID().toString().substring(0, 8)

        return Lookup(
            from = from,
            localField = fieldName,
            foreignField = targetFieldName,
            alias = alias,
            matchDocument = document,
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
                else ID
            } else this.toDotPath()
        }
}
