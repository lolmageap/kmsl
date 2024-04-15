package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.EmptyGroup
import com.example.kotlinmongo.clazz.Field
import com.example.kotlinmongo.clazz.Group
import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun <T, R> BasicQuery.groupBy(
    key: KProperty1<T, R>,
): Group<T, R> {
    return Group(key, this.queryObject)
}

fun BasicQuery.groupBy(): EmptyGroup {
    return EmptyGroup(this.queryObject)
}

fun BasicQuery.where(
    document: Document.() -> Document,
): BasicQuery {
    val queryObject = this.queryObject
    return BasicQuery(document.invoke(queryObject))
}

fun BasicQuery.sumOf(
    sumField: Document.() -> Field<*, *>,
): Aggregation {
    val document = this.queryObject
    return EmptyGroup(document).sumOf { sumField.invoke(Document()) }
}