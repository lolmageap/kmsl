package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.EmptyGroup
import com.example.kotlinmongo.clazz.Group
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Field
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

fun <T : Any> MongoTemplate.findAll(
    query: BasicQuery,
    pageable: Pageable,
    entityClass: KClass<T>,
): Page<T> {
    val data = find(
        query.limit(pageable.pageSize)
            .skip(pageable.offset)
            .with(pageable.sort),
        entityClass.java,
    )
    val count = count(query, entityClass.java)
    return PageImpl(data, pageable, count)
}

fun <T : Any> MongoTemplate.count(
    query: BasicQuery,
    entityClass: KClass<T>,
): Long = count(query, entityClass.java)

fun <T : Any, R : Any> MongoTemplate.count(
    group: Group<T, R>,
    entityClass: KClass<T>,
): Map<String, *> =
    this.aggregate(
        group.toAggregation(),
        entityClass.java,
        Map::class.java,
    ).uniqueMappedResult!!.map {
        it.key.toString() to it.value
    }.toMap()

fun <T : Any> MongoTemplate.count(
    group: EmptyGroup.GroupOperationWrapper,
    entityClass: KClass<T>,
): Map<String, *> =
    this.aggregate(
        group.toAggregation(),
        entityClass.java,
        Map::class.java,
    ).uniqueMappedResult!!.map {
        it.key.toString() to it.value
    }.toMap()

fun <T : Any> MongoTemplate.aggregate(
    group: Group.GroupOperationWrapper,
    entityClass: KClass<T>,
): List<Map<String, *>> =
    this.aggregate(
        group.toAggregation(),
        entityClass.java,
        Map::class.java,
    ).mappedResults.map { results ->
        results.map {
            it.key.toString() to it.value
        }.toMap()
    }

fun <T : Any> MongoTemplate.aggregate(
    group: EmptyGroup.GroupOperationWrapper,
    entityClass: KClass<T>,
): Map<String, *> =
    this.aggregate(
        group.toAggregation(),
        entityClass.java,
        Map::class.java,
    ).uniqueMappedResult!!.map {
        it.key.toString() to it.value
    }.toMap()

val KClass<*>.fieldName
    get() = this.java.declaredFields.first { it.isAnnotationPresent(Id::class.java) }
        ?.run {
            isAccessible = true
            val hasFieldAnnotation = annotations.any { it is Field }
            if (hasFieldAnnotation) annotations.filterIsInstance<Field>().first().value
            else "_id"
        }
        ?: this.simpleName!!