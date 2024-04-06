package com.example.kotlinmongo

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance

inline fun <reified T : Any> criteria(
    function: Criteria.(T) -> Unit,
): Criteria {
    return Criteria().apply {
        function(T::class.createInstance())
    }
}

fun Criteria.with(
    pageable: Pageable,
): Query {
    val query = Query()
    query.with(pageable)
    return query
}

fun <T> Criteria.and(
    key: KProperty<T>,
): Criteria {
    return and(key.name)
}

fun <T> Criteria.where(
    key: KProperty<T>,
): Criteria {
    return Criteria.where(key.name)
}

fun Criteria.query(): Query {
    return Query(this)
}

fun Criteria.query(pageable: Pageable): Query {
    return Query(this).with(pageable)
}

fun Criteria.elemMatch(
    function: Criteria.() -> Unit,
): Criteria {
    return Criteria().apply {
        function()
    }
}