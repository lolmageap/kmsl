package com.example.kotlinmongo

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria

class EmptyGroup(
    private val document: Document,
) {
    fun sumOf(
        alias: String = "total",
        sumField: Document.() -> Field<*, *>,
    ): Aggregation {
        val matchStage = matchOperation()
        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group().sum(sumField.invoke(Document()).key.name).`as`(alias)
        )
    }

    fun sumOfNumber(
        alias: String = "total",
        sumField: Document.() -> Field<*, *>,
    ): Aggregation {
        val fieldName = sumField.invoke(Document()).key.name
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
        val matchStage = matchOperation()
        return Aggregation.newAggregation(
            matchStage,
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