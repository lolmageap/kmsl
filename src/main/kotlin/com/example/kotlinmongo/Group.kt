package com.example.kotlinmongo

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import kotlin.reflect.KProperty1

// side effect 가 발생하지 않으려면 Group By 는 마지막 줄에 위치 시키는 것이 좋습니다.
// grouping 을 할때 내부적으로 matchOperation 을 사용하기 때문에...
class Group<T, R>(
    private val key: KProperty1<T, R>,
    private val document: Document,
) {
    fun sumOf(
        alias: String = "total",
        sumField: () -> KProperty1<T, *>,
    ): Aggregation {
        val matchStage = matchOperation()
        return Aggregation.newAggregation(
            matchStage,
            Aggregation.group(key.name).sum(sumField.invoke().name).`as`(alias)
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