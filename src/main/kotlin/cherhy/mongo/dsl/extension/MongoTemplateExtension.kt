package cherhy.mongo.dsl.extension

import cherhy.mongo.dsl.clazz.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

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

inline fun <reified T : Any, reified R : Any> MongoTemplate.sum(
    query: BasicQuery,
    property: KProperty1<T, R>,
    alias: String = "total",
): R {
    val sumOfAll = query sum { field(property) alias alias }

    return aggregate(sumOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified C : Any> MongoTemplate.sum(
    query: BasicQuery,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "total",
): C {
    val sumOfAll = query sum { field(property) type castType alias alias }

    return aggregate(sumOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified K : Any> MongoTemplate.sum(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    alias: String = "total",
): Map<K, R> {
    val sumOfGroup = group sum { field(property) alias alias }

    return aggregate(sumOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any, reified K : Any, reified C : Any> MongoTemplate.sum(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "total",
): Map<K, C> {
    val sumOfGroup = group sum { field(property) type castType alias alias }

    return aggregate(sumOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any> MongoTemplate.average(
    query: BasicQuery,
    property: KProperty1<T, R>,
    alias: String = "average",
): Double {
    val averageOfAll = query average { field(property) alias alias }

    return aggregate(averageOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias] as? Double
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified C : Any> MongoTemplate.average(
    query: BasicQuery,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "average",
): C {
    val averageOfAll = query average { field(property) type castType alias alias }

    return aggregate(averageOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified K : Any> MongoTemplate.average(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    alias: String = "average",
): Map<K, Double> {
    val averageOfGroup = group average { field(property) alias alias }

    return aggregate(averageOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias] as? Double
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any, reified K : Any, reified C : Any> MongoTemplate.average(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "average",
): Map<K, C> {
    val averageOfGroup = group average { field(property) type castType alias alias }

    return aggregate(averageOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any> MongoTemplate.max(
    query: BasicQuery,
    property: KProperty1<T, R>,
    alias: String = "max",
): R {
    val maxOfAll = query max { field(property) alias alias }

    return aggregate(maxOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified C : Any> MongoTemplate.max(
    query: BasicQuery,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "max",
): C {
    val maxOfAll = query max { field(property) type castType alias alias }

    return aggregate(maxOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified K : Any> MongoTemplate.max(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    alias: String = "max",
): Map<K, R> {
    val maxOfGroup = group max { field(property) alias alias }

    return aggregate(maxOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any, reified K : Any, reified C : Any> MongoTemplate.max(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "max",
): Map<K, C> {
    val maxOfGroup = group max { field(property) type castType alias alias }

    return aggregate(maxOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any> MongoTemplate.min(
    query: BasicQuery,
    property: KProperty1<T, R>,
    alias: String = "min",
): R {
    val minOfAll = query min { field(property) alias alias }

    return aggregate(minOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified C : Any> MongoTemplate.min(
    query: BasicQuery,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "min",
): C {
    val minOfAll = query min { field(property) type castType alias alias }

    return aggregate(minOfAll.toAggregation(), T::class.java, Map::class.java)
        .uniqueMappedResult?.let { result ->
            result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
        }
        ?: throw NoSuchElementException("No element found")
}

inline fun <reified T : Any, reified R : Any, reified K : Any> MongoTemplate.min(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    alias: String = "min",
): Map<K, R> {
    val minOfGroup = group min { field(property) alias alias }

    return aggregate(minOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias] as? R
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

inline fun <reified T : Any, reified R : Any, reified K : Any, reified C : Any> MongoTemplate.min(
    group: Group<T, K>,
    property: KProperty1<T, R>,
    castType: KClass<C>,
    alias: String = "min",
): Map<K, C> {
    val minOfGroup = group min { field(property) type castType alias alias }

    return aggregate(minOfGroup.toAggregation(), T::class.java, Map::class.java)
        .mappedResults.associate { result ->
            val key = castIfEnum<K, T>(result, T::class)
            val value = result[alias].cast<C>()
                ?: throw TypeCastException("null cannot be cast to non-null type ${property.returnType}")
            key to value
        }
}

val KClass<*>.fieldName
    get() = this.java.declaredFields.first { it.isAnnotationPresent(Id::class.java) }
        ?.run {
            isAccessible = true
            val hasFieldAnnotation = annotations.any { it is Field }
            if (hasFieldAnnotation) annotations.filterIsInstance<Field>().first().value
            else "_id"
        }
        ?: this.simpleName!!