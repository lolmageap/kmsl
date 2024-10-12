package com.kmsl.dsl.extension

import com.kmsl.dsl.clazz.FieldName
import com.kmsl.dsl.clazz.JakartaAnnotation
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.jvm.kotlinProperty

val java.lang.reflect.Field.fieldName: String
    get() {
        val hasFieldAnnotation = this.annotations.any { it is Field }
        val hasSpringDataIdAnnotation = this.annotations.any { it is Id }
        val hasJakartaIdAnnotation = this.annotations.any {
            it.annotationClass.qualifiedName == JakartaAnnotation.ID
        }
        return when {
            hasFieldAnnotation -> this.annotations
                .filterIsInstance<Field>()
                .first().value
            hasSpringDataIdAnnotation -> FieldName.ID
            hasJakartaIdAnnotation -> FieldName.ID
            else -> this.kotlinProperty?.toDotPath() ?: this.name
        }
    }

fun java.lang.reflect.Field.hasJakartaIdAnnotation(): Boolean {
    return this.annotations.any {
        it.annotationClass.qualifiedName == JakartaAnnotation.ID
    }
}