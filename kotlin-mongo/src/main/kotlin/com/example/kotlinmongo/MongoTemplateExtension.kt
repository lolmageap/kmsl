package com.example.kotlinmongo

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KClass

fun <T : Any> MongoTemplate.find(
    query: BasicQuery,
    pageable: Pageable,
    entityClass: KClass<T>,
): List<T> {
    val realQuery = query.with(pageable)
    return find(realQuery, entityClass.java)
}

fun <T : Any> MongoTemplate.count(
    query: BasicQuery,
    entityClass: KClass<T>,
): Long {
    return count(query, entityClass.java)
}