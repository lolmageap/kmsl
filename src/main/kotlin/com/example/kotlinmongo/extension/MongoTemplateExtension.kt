package com.example.kotlinmongo.extension

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KClass

fun <T : Any> MongoTemplate.find(
    query: BasicQuery,
    pageable: Pageable,
    entityClass: KClass<T>,
): List<T> {
    return find(
        query.limit(pageable.pageSize)
            .skip(pageable.offset)
            .with(pageable.sort),
        entityClass.java,
    )
}

fun <T : Any> MongoTemplate.find(
    query: BasicQuery,
    entityClass: KClass<T>,
): List<T> {
    return find(query, entityClass.java)
}

fun <T : Any> MongoTemplate.count(
    query: BasicQuery,
    entityClass: KClass<T>,
): Long {
    return count(query, entityClass.java)
}

fun MongoTemplate.sumOfSingle(
    aggregation: Aggregation,
    inputType: KClass<*>,
    alias: String = "total",
): Long {
    return aggregate(aggregation, inputType.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            (result[alias] as? Number)?.toLong() ?: 0L
        } ?: 0L
}

fun MongoTemplate.sumOfGroup(
    aggregation: Aggregation,
    inputType: KClass<*>,
    alias: String = "total",
): Map<String, Long> {
    return aggregate(aggregation, inputType.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = result["_id"] as String
            val value = (result[alias] as? Number)?.toLong() ?: 0L
            key to value
        }
}