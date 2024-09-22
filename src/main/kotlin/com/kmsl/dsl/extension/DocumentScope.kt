package com.kmsl.dsl.extension

import com.kmsl.dsl.extension.RootDocumentOperator.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery

fun document(
    documentOperator: RootDocumentOperator = AND,
    block: com.kmsl.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = com.kmsl.dsl.clazz.DocumentOperatorBuilder().let {
        it.block()
        if (it.documents.isEmpty()) return BasicQuery(Document())

        when (documentOperator) {
            AND -> Document().append(com.kmsl.dsl.clazz.DocumentOperator.AND, it.documents)
            OR -> Document().append(com.kmsl.dsl.clazz.DocumentOperator.OR, it.documents)
            NOR -> Document().append(com.kmsl.dsl.clazz.DocumentOperator.NOR, it.documents)
        }
    }

    return BasicQuery(document)
}

fun Document.copy() = Document(this)

enum class RootDocumentOperator {
    AND,
    OR,
    NOR,
}