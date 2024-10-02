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

    /**TODO
     *  Projection constructor 가 있다면 그거로 바인딩 해야함.
     *  get() = entityClass.java.constructors[1].parameters.map { it.name }.toTypedArray()
     */
    private val java.lang.reflect.Field.fieldName: String
        get() {
            val hasFieldAnnotation = this.annotations.any { it is Field }
            val hasSpringDataIdAnnotation = this.annotations.any { it is Id }
            val hasJakartaIdAnnotation = this.annotations.any { it is jakarta.persistence.Id }

            return when {
                hasFieldAnnotation -> this.annotations
                    .filterIsInstance<Field>()
                    .first().value
                hasSpringDataIdAnnotation -> ID
                hasJakartaIdAnnotation -> ID
                else -> this.kotlinProperty?.toDotPath() ?: this.name
            }
        }
}