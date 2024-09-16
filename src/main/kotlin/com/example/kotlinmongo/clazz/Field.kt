package com.example.kotlinmongo.clazz

import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class Field<T, R>(
    val key: KProperty1<T, R>,
    val documents: MutableList<Document> = mutableListOf(),
) {
    fun <T : Any> getClassName(
        clazz: KClass<T>,
    ): String? {
        val className = clazz.simpleName
        return className?.replaceFirstChar { it.lowercase() }
    }

    val KProperty1<T, R>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            val hasIdAnnotation = javaField.annotations.any { it is Id }
            return if (hasIdAnnotation) {
                val hasFieldAnnotation = javaField.annotations.any { it is Field }
                if (hasFieldAnnotation) javaField.annotations.filterIsInstance<Field>().first().value
                else "_id"
            } else this.toDotPath()
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

fun <T, R> DocumentOperatorBuilder.field(
    key: KProperty1<T, R>,
) = Field(key, this.documents)

fun <T, R> Group<T, R>.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Group.Sum.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Group.Average.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Group.Max.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Group.Min.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Group.Count.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.Sum.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.Average.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.Max.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.Min.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> EmptyGroup.Count.field(
    key: KProperty1<T, R>,
) = Field(key)

fun <T, R> Order.field(
    key: KProperty1<T, R>,
) = Field(key)