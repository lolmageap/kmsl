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
): List<T> =
    find(
        query.limit(pageable.pageSize)
            .skip(pageable.offset)
            .with(pageable.sort),
        entityClass.java,
    )

fun <T : Any> MongoTemplate.find(
    query: BasicQuery,
    entityClass: KClass<T>,
): List<T> = find(query, entityClass.java)

fun <T : Any> MongoTemplate.count(
    query: BasicQuery,
    entityClass: KClass<T>,
) = count(query, entityClass.java)

inline fun <reified T : Any> MongoTemplate.sum(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "total",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .uniqueMappedResult?.let { result ->
        T::class.java.cast(
            result[alias],
        )
    }

fun MongoTemplate.sumOfGroup(
    aggregation: Aggregation,
    inputType: KClass<*>,
    alias: String = "total",
) =
    aggregate(aggregation, inputType.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = result["_id"] as String
            val value = inputType.java.cast(
                result[alias],
            )
            key to value
        }

inline fun <reified T : Any> MongoTemplate.avg(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "avg",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .uniqueMappedResult?.let { result ->
        T::class.java.cast(
            result[alias],
        )
    }

inline fun <reified T : Any> MongoTemplate.avgOfGroup(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "avg",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .mappedResults.associate { result ->
        val key = result["_id"] as String
        val value = T::class.java.cast(
            result[alias],
        )
        key to value
    }

inline fun <reified T : Any> MongoTemplate.max(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "max",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .uniqueMappedResult?.let { result ->
        T::class.java.cast(
            result[alias],
        )
    }

inline fun <reified T : Any> MongoTemplate.maxOfGroup(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "max",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .mappedResults.associate { result ->
        val key = result["_id"] as String
        val value = T::class.java.cast(
            result[alias],
        )
        key to value
    }

inline fun <reified T : Any> MongoTemplate.min(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "min",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .uniqueMappedResult?.let { result ->
        T::class.java.cast(
            result[alias],
        )
    }

inline fun <reified T : Any> MongoTemplate.minOfGroup(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "min",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .mappedResults.associate { result ->
        val key = result["_id"] as String
        val value = T::class.java.cast(
            result[alias],
        )
        key to value
    }

inline fun <reified T : Any> MongoTemplate.countOfGroup(
    aggregation: Aggregation,
    inputType: KClass<T>,
    alias: String = "count",
) = aggregate(aggregation, inputType.java, Map::class.java)
    .mappedResults.associate { result ->
        val key = result["_id"] as String
        val value = T::class.java.cast(
            result[alias],
        )
        key to value
    }