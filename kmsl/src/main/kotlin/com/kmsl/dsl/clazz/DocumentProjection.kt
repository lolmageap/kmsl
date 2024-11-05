package com.kmsl.dsl.clazz

data class DocumentProjection<T : Any, R: Any>(
    val projectionConstructor: ProjectionConstructor<T>,
    val lookup: Lookup<R>,
)