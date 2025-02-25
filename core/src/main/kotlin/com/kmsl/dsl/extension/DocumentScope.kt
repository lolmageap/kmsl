package com.kmsl.dsl.extension

import com.kmsl.dsl.annotation.DocumentScope
import com.kmsl.dsl.clazz.DocumentOperator
import com.kmsl.dsl.clazz.DocumentOperatorBuilder
import com.kmsl.dsl.extension.RootDocumentOperator.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery

@DocumentScope
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