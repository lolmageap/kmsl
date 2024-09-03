package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.EmptyGroup
import com.example.kotlinmongo.clazz.Field
import com.example.kotlinmongo.clazz.Group
import com.example.kotlinmongo.clazz.Order
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

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

fun <T, R> EmptyGroup.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> EmptyGroup.Sum.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> EmptyGroup.Average.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> EmptyGroup.Max.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> EmptyGroup.Min.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

fun <T, R> EmptyGroup.Count.field(
    key: KProperty1<T, R>,
) = Field(key, mutableListOf())

infix fun BasicQuery.sum(
    block: EmptyGroup.Sum.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Sum(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.average(
    block: EmptyGroup.Average.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Average(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.max(
    block: EmptyGroup.Max.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Max(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.min(
    block: EmptyGroup.Min.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Min(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.count(
    block: EmptyGroup.Count.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Count(this.queryObject.copy(), Aggregation.group()).block()

fun BasicQuery.orderBy(
    key: KProperty1<*, *>,
) = Order(this, key)