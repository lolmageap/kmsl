package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun document(
    rootOperatorType: RootOperatorType = RootOperatorType.AND,
    function: DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = Document()

    val documentOperatorBuilder = DocumentOperatorBuilder(
        document = document,
        rootOperatorType = rootOperatorType,
        function = function,
    ).run()

    return BasicQuery(
        documentOperatorBuilder.toJson()
    )
}

fun orOperator(
    function: OrOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = Document()
    val orOperatorBuilder = OrOperatorBuilder(document, function).run()
    return BasicQuery(
        orOperatorBuilder.toJson()
    )
}

fun andOperator(
    function: AndOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = Document()
    val orOperatorBuilder = AndOperatorBuilder(document, function).run()
    return BasicQuery(
        orOperatorBuilder.toJson()
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