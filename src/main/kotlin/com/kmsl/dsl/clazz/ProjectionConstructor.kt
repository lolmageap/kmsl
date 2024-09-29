package com.kmsl.dsl.clazz

import com.kmsl.dsl.clazz.FieldName.ID
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinProperty

data class ProjectionConstructor<T : Any>(
    val entityClass: KClass<T>,
) {
    val fieldNames
        get() = entityClass.java.declaredFields.map { it.fieldName }.toTypedArray()

    private val java.lang.reflect.Field.fieldName: String
        get() {
            val hasIdAnnotation = this.annotations.any { it is Id }
            return if (hasIdAnnotation) {
                val hasFieldAnnotation = this.annotations.any { it is Field }
                if (hasFieldAnnotation) this.annotations.filterIsInstance<Field>().first().value
                else ID
            } else this.kotlinProperty?.toDotPath() ?: this.name
        }
}