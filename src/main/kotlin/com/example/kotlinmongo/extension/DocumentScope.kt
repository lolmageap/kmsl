package com.example.kotlinmongo.extension

import com.example.kotlinmongo.clazz.DocumentOperator
import com.example.kotlinmongo.clazz.DocumentOperatorBuilder
import com.example.kotlinmongo.clazz.Field
import com.example.kotlinmongo.extension.RootDocumentOperator.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

fun document(
    documentOperator: RootDocumentOperator = AND,
    block: DocumentOperatorBuilder.RootDocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = DocumentOperatorBuilder.RootDocumentOperatorBuilder().let {
        it.block()
        when (documentOperator) {
            AND -> Document().append(DocumentOperator.AND, it.documents)
            OR -> Document().append(DocumentOperator.OR, it.documents)
            NOR -> Document().append(DocumentOperator.NOR, it.documents)
        }
    }

    return BasicQuery(document.toJson())
}

fun <T, R> DocumentOperatorBuilder.RootDocumentOperatorBuilder.field(
    key: KProperty1<T, R>,
) = Field(key, this.documents)

fun <T, R> DocumentOperatorBuilder.AndDocumentOperatorBuilder.field(
    key: KProperty1<T, R>,
) = Field(key, this.documents)

fun <T, R> DocumentOperatorBuilder.OrDocumentOperatorBuilder.field(
    key: KProperty1<T, R>,
) = Field(key, this.documents)

fun <T, R> DocumentOperatorBuilder.NorDocumentOperatorBuilder.field(
    key: KProperty1<T, R>,
) = Field(key, this.documents)

fun Document.copy() = Document(this)

enum class RootDocumentOperator {
    AND,
    OR,
    NOR,
}