package com.example.kotlinmongo.clazz

import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class Field<T, R>(
    val key: KProperty1<T, R>,
    val documents: MutableList<Document>,
) {
    val KProperty1<T, R>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            val hasIdAnnotation = javaField.annotations.any { it is Id }
            return if (hasIdAnnotation) {
                val hasFieldAnnotation = javaField.annotations.any { it is Field }
                if (hasFieldAnnotation) javaField.annotations.filterIsInstance<Field>().first().value
                else "_id"
            } else this.name
        }

    fun R.convertIfId(): Any? {
        val javaField = key.javaField!!
        javaField.isAccessible = true

        val hasIdAnnotation = javaField.annotations.any { it is Id }
        return if (hasIdAnnotation) ObjectId(this.toString())
        else if (this is Enum<*>) this.name
        else this
    }
}