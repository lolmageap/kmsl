package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.DocumentOperator
import com.example.kotlinmongo.clazz.DocumentOperatorBuilder
import com.example.kotlinmongo.extension.RootDocumentOperator.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery

fun document(
    documentOperator: RootDocumentOperator = AND,
    block: DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = DocumentOperatorBuilder().let {
        it.block()
        if (it.documents.isEmpty()) return BasicQuery(Document())

        when (documentOperator) {
            AND -> Document().append(DocumentOperator.AND, it.documents)
            OR -> Document().append(DocumentOperator.OR, it.documents)
            NOR -> Document().append(DocumentOperator.NOR, it.documents)
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