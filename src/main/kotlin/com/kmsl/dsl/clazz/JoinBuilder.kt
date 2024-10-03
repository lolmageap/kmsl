package com.kmsl.dsl.clazz

import com.kmsl.dsl.extension.fieldName
import org.bson.Document
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
            joinedClasses = listOf(T::class, K::class),
        )
    }

    val <T, V> KProperty1<T, V>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            return javaField.fieldName
        }
}