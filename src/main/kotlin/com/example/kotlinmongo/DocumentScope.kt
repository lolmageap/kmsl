package com.example.kotlinmongo

import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun document(
    function: Document.() -> Document,
): BasicQuery {
    val document = Document()
    val json = document.function().toJson()
    return BasicQuery(json)
}

fun operator(
    function: OperatorBuilder.() -> Unit,
): BasicQuery {
    val document = Document()
    val operatorBuilder = OperatorBuilder.open(document, function)
    return BasicQuery(
        operatorBuilder.toJson()
    )
}

fun <T, R> Document.field(
    key: KProperty1<T, R>,
): Field<T, R> {
    return Field(key, this)
}