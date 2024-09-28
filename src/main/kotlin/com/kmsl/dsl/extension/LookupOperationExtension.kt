package com.kmsl.dsl.extension

import com.kmsl.dsl.clazz.DocumentProjection
import com.kmsl.dsl.clazz.ProjectionConstructor
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import kotlin.reflect.KClass

infix fun <T: Any> LookupOperation.projection(
    block: ProjectionBuilder.() -> DocumentProjection<T>,
): DocumentProjection<T> {
    val projectionBuilder = ProjectionBuilder()
    return projectionBuilder.block()
}

class ProjectionBuilder {
    infix fun <T : Any> constructor(
        kClass: KClass<T>,
    ): ProjectionConstructor<T> {
        TODO("construct 로 받은 kClass 를 이용하여 projection 을 만들어야 함.")
    }

    infix fun <T : Any> ProjectionConstructor<T>.alias(
        alias: String,
    ): DocumentProjection<T> {
        TODO("alias 로 받은 alias 를 이용하여 projection 을 만들어야 함.")
    }
}