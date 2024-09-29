package com.kmsl.dsl.clazz

data class DocumentProjection<T : Any>(
    val projectionConstructor: ProjectionConstructor<T>,
    val lookup: Lookup,
)