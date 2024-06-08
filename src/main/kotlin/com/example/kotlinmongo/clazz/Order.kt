package com.example.kotlinmongo.clazz

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

data class Order(
    val basicQuery: BasicQuery,
    val key: KProperty1<*, *>,
) {
    fun asc(): BasicQuery {
        basicQuery.with(Sort.by(Sort.Direction.ASC, key.name))
        return basicQuery
    }

    fun desc(): BasicQuery {
        basicQuery.with(Sort.by(Sort.Direction.DESC, key.name))
        return basicQuery
    }
}