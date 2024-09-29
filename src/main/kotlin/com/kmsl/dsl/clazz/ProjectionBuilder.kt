package com.kmsl.dsl.clazz

import kotlin.reflect.KClass

class ProjectionBuilder {
    infix fun <T : Any> constructor(
        kClass: KClass<T>,
    ) =
        ProjectionConstructor(kClass)
}