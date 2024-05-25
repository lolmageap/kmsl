package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.DocumentOperatorBuilder
import com.example.kotlinmongo.clazz.Field
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun document(
    function: DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = Document()

    val documentOperatorBuilder = DocumentOperatorBuilder(document, function)

    return BasicQuery(
        documentOperatorBuilder.toJson()
    )
}

fun <T, R> Document.field(
    key: KProperty1<T, R>,
): Field<T, R> {
    return Field(key, this)
}

fun Document.copy(): Document {
    return Document(this)
}