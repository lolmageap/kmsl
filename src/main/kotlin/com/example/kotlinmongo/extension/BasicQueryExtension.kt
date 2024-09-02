package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

//fun <T, R> BasicQuery.groupBy(
//    key: KProperty1<T, R>,
//) = Group(
//    key,
//    this.queryObject.copy(),
//)

infix fun <T, R> BasicQuery.group(
    block: Group<T, R>.() -> Unit,
): Group<T, R> {
    val group = Group<T, R>(this.queryObject.copy())
    group.block()
    return group
}

fun <T, R> Group<T, R>.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> Group.Sum.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> Group.Average.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> Group.Max.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> Group.Min.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> Group.Count.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun BasicQuery.groupBy() = EmptyGroup(
    this.queryObject.copy(),
)

fun BasicQuery.sumOf(
    alias: String,
    type: KClass<*>? = null,
    sumField: Document.() -> Field<*, *>,
) =
    EmptyGroup(
        this.queryObject.copy()
    ).sumOf(type) { sumField.invoke(Document()) }

fun BasicQuery.avgOf(
    alias: String,
    type: KClass<*>? = null,
    avgField: Document.() -> Field<*, *>,
) =
    EmptyGroup(
        this.queryObject.copy()
    ).avgOf(type) { avgField.invoke(Document()) }

fun BasicQuery.maxOf(
    alias: String,
    type: KClass<*>? = null,
    maxField: Document.() -> Field<*, *>,
) =
    EmptyGroup(
        this.queryObject.copy()
    ).maxOf(type) { maxField.invoke(Document()) }

fun <T, R> BasicQuery.minOf(
    alias: String,
    type: KClass<*>? = null,
    minField: Document.() -> Field<T, R>,
) =
    EmptyGroup(
        this.queryObject.copy()
    ).minOf(type) { minField.invoke(Document()) }

fun BasicQuery.orderBy(
    key: KProperty1<*, *>,
) = Order(this, key)