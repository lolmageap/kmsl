package com.kmsl.dsl.clazz

import org.springframework.data.mongodb.core.aggregation.LookupOperation
import kotlin.reflect.KClass

data class DocumentProjection<T : Any>(
    val projectionConstructor: ProjectionConstructor<T>,
    val lookupOperation: LookupOperation,
)

data class ProjectionConstructor<T: Any>(
    val kClass: KClass<T>,
)