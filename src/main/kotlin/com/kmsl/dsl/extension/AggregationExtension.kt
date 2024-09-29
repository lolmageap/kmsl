package com.kmsl.dsl.extension

import com.kmsl.dsl.clazz.DocumentProjection
import com.kmsl.dsl.clazz.Lookup
import com.kmsl.dsl.clazz.ProjectionBuilder
import com.kmsl.dsl.clazz.ProjectionConstructor

infix fun <T : Any> Lookup.projection(
    block: ProjectionBuilder.() -> ProjectionConstructor<T>,
): DocumentProjection<T> {
    val projectionBuilder = ProjectionBuilder()
    val projectionConstructor = projectionBuilder.block()
    return DocumentProjection(projectionConstructor, this)
}