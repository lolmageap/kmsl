package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.EmptyGroup
import com.example.kotlinmongo.clazz.Field
import com.example.kotlinmongo.clazz.Group
import com.example.kotlinmongo.clazz.Order
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun <T, R> BasicQuery.groupBy(
    key: KProperty1<T, R>,
) = Group(
    key,
    this.queryObject.copy(),
)

fun BasicQuery.groupBy() = EmptyGroup(
    this.queryObject.copy(),
)

fun BasicQuery.where(
    document: Document.() -> Document,
) =
    BasicQuery(
        document.invoke(
            this.queryObject.copy()
        ),
    )

fun BasicQuery.sumOf(
    sumField: Document.() -> Field<*, *>,
) =
    EmptyGroup(
        this.queryObject.copy()
    ).sumOf { sumField.invoke(Document()) }

fun BasicQuery.orderBy(
    key: KProperty1<*, *>,
) = Order(this, key)