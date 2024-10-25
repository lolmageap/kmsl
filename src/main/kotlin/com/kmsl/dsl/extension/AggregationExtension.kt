package com.kmsl.dsl.extension

import com.kmsl.dsl.clazz.DocumentProjection
import com.kmsl.dsl.clazz.Lookup
import com.kmsl.dsl.clazz.ProjectionBuilder
import com.kmsl.dsl.clazz.ProjectionConstructor

infix fun <T : Any, R: Any> Lookup<R>.projection(
    block: ProjectionBuilder.() -> ProjectionConstructor<T>,
): DocumentProjection<T, R> {
    val projectionBuilder = ProjectionBuilder()
    val projectionConstructor = projectionBuilder.block()
    return DocumentProjection(projectionConstructor, this)
}