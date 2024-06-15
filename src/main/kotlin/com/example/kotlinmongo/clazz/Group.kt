package com.example.kotlinmongo.clazz

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * grouping 을 할때 내부적으로 matchOperation 을 사용합니다.
 * side effect 가 발생하지 않으려면 Group By 는 마지막 줄에 위치 시키는 것이 좋습니다.
 */
class Group<T, R>(
    private val key: KProperty1<T, R>,
    private val document: Document,
) {
    fun sumOf(
        type: KClass<*> = Long::class,
        alias: String = "total",
        sumField: Document.() -> Field<T, *>,
    ): Aggregation {
        val fieldName = sumField.invoke(Document()).key.name
        val expression = AggregationExpression {
            Document(MongoAggregateTypeFactory.exchange(type), "\$$fieldName")
        }

        val matchStage = matchOperation()

        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).sum(expression).`as`(alias)
        )
    }

    fun avgOf(
        type: KClass<*> = Long::class,
        alias: String = "avg",
        avgField: Document.() -> Field<T, *>,
    ): Aggregation {
        val fieldName = avgField.invoke(Document()).key.name
        val expression = AggregationExpression {
            Document(MongoAggregateTypeFactory.exchange(type), "\$$fieldName")
        }

        val matchStage = matchOperation()

        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).avg(expression).`as`(alias)
        )
    }

    fun maxOf(
        type: KClass<*> = Long::class,
        alias: String = "max",
        maxField: Document.() -> Field<T, *>,
    ): Aggregation {
        val fieldName = maxField.invoke(Document()).key.name
        val expression = AggregationExpression {
            Document(MongoAggregateTypeFactory.exchange(type), "\$$fieldName")
        }

        val matchStage = matchOperation()

        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).max(expression).`as`(alias)
        )
    }

    fun minOf(
        type: KClass<*> = Long::class,
        alias: String = "min",
        minField: Document.() -> Field<T, *>,
    ): Aggregation {
        val fieldName = minField.invoke(Document()).key.name
        val expression = AggregationExpression {
            Document(MongoAggregateTypeFactory.exchange(type), "\$$fieldName")
        }

        val matchStage = matchOperation()

        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).min(expression).`as`(alias)
        )
    }

    fun count(
        alias: String = "count",
    ): Aggregation {
        val matchStage = matchOperation()
        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).count().`as`(alias)
        )
    }

    private fun matchOperation(): MatchOperation {
        val criteria = Criteria()
        for ((key, value) in document) {
            criteria.and(key).`is`(value)
        }
        return Aggregation.match(criteria)
    }
}