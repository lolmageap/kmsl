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

fun Document.orScope(
    function: OrBuilder.() -> Unit,
): Document {
    return OrBuilder.open(this, function)
}

fun Document.andScope(
    function: AndBuilder.() -> Unit,
): Document {
    return AndBuilder.open(this, function)
}

fun <T, R> Document.field(
    key: KProperty1<T, R>,
): Field<T, R> {
    return Field(key, this)
}