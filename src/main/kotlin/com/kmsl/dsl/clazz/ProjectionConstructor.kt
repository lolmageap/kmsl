package com.kmsl.dsl.clazz

import kotlin.reflect.KClass

data class ProjectionConstructor<T : Any>(
    val entityClass: KClass<T>,
)