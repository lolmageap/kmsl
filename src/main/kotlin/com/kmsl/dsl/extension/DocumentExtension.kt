package com.kmsl.dsl.extension

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria

fun Document.toMatchOperation(): MatchOperation {
    val criteria = Criteria()
    for ((key, value) in this) {
        criteria.and(key).`is`(value)
    }
    return Aggregation.match(criteria)
}