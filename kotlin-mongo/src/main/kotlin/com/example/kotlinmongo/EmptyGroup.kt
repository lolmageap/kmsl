package com.example.kotlinmongo

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import kotlin.reflect.KProperty

class EmptyGroup(
    private val document: Document,
) {
    fun sumOf(
        sumField: () -> KProperty<*>,
        alias: String = "total",
    ): Aggregation {
        return Aggregation.newAggregation(
            Aggregation.group().sum(sumField.invoke().name).`as`(alias)
        )
    }

    fun sumOfInt(
        alias: String = "total",
        sumField: () -> KProperty<*>,
    ): Aggregation {
        val fieldName = sumField.invoke().name
        val toIntExpression = AggregationExpression {
            Document("\$toLong", "\$$fieldName")
        }

        val matchStage = matchOperation()

        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group().sum(toIntExpression).`as`(alias)
        )
    }

    fun count(
        alias: String = "count",
    ): Aggregation {
        return Aggregation.newAggregation(
            Aggregation.group().count().`as`(alias)
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