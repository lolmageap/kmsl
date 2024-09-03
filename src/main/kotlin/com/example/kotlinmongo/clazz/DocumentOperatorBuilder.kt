package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.clazz.DocumentOperator.AND
import com.example.kotlinmongo.clazz.DocumentOperator.NOR
import com.example.kotlinmongo.clazz.DocumentOperator.OR
import org.bson.Document

class DocumentOperatorBuilder {
    open class RootDocumentOperatorBuilder {
        val documents = mutableListOf<Document>()

        fun and(
            block: AndDocumentOperatorBuilder.() -> Unit,
        ) {
            val andDocumentOperatorBuilder = AndDocumentOperatorBuilder()
            andDocumentOperatorBuilder.block()
            val and = andDocumentOperatorBuilder.documents
            documents.add(Document().append(AND, and))
        }

        fun or(
            block: OrDocumentOperatorBuilder.() -> Unit,
        ) {
            val orDocumentOperatorBuilder = OrDocumentOperatorBuilder()
            orDocumentOperatorBuilder.block()
            val or = orDocumentOperatorBuilder.documents
            documents.add(Document().append(OR, or))
        }

        fun nor(
            block: NorDocumentOperatorBuilder.() -> Unit,
        ) {
            val norDocumentOperatorBuilder = NorDocumentOperatorBuilder()
            norDocumentOperatorBuilder.block()
            val nor = norDocumentOperatorBuilder.documents
            documents.add(Document().append(NOR, nor))
        }

        infix fun <T, R> Field<T, R>.eq(value: R): Document {
            val document = Document(key.fieldName, value.convertIfId())
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.ne(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.NOT_EQUAL, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.lt(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.LESS_THAN, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.lte(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.LESS_THAN_EQUAL, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.gt(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.GREATER_THAN, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.gte(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.GREATER_THAN_EQUAL, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.between(values: Pair<R?, R?>): Document {
            return when {
                values.first == null && values.second == null -> {
                    Document()
                }

                values.first == null -> {
                    val document = Document(key.fieldName, Document(DocumentOperator.GREATER_THAN, values.second?.let { it.convertIfId() }))
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                values.second == null -> {
                    val document = Document(key.fieldName, Document(DocumentOperator.LESS_THAN, values.first?.let { it.convertIfId() }))
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                else -> {
                    val document = Document(key.fieldName,
                        Document(DocumentOperator.LESS_THAN, values.first?.let { it.convertIfId() }).append(
                            DocumentOperator.GREATER_THAN,
                            values.second?.let { it.convertIfId() })
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }
            }
        }

        infix fun <T, R> Field<T, R>.notBetween(values: Pair<R?, R?>): Document {
            return when {
                values.first == null && values.second == null -> {
                    Document()
                }

                values.first == null -> {
                    val document = Document(
                        key.fieldName, Document(DocumentOperator.NOT, Document(DocumentOperator.GREATER_THAN, values.second?.let { it.convertIfId() }))
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                values.second == null -> {
                    val document =
                        Document(key.fieldName, Document(DocumentOperator.NOT, Document(DocumentOperator.LESS_THAN, values.first?.let { it.convertIfId() })))
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                else -> {
                    val document = Document(
                        key.fieldName, Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.LESS_THAN, values.first?.let { it.convertIfId() }).append(
                                DocumentOperator.GREATER_THAN,
                                values.second?.let { it.convertIfId() })
                        )
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }
            }
        }

        infix fun <T, R> Field<T, R>.betweenInclusive(values: Pair<R?, R?>): Document {
            return when {
                values.first == null && values.second == null -> {
                    Document()
                }

                values.first == null -> {
                    val document =
                        Document(key.fieldName, Document(DocumentOperator.GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() }))
                    if (document.isNotEmpty()) documents.add(document)
                    return document
                }

                values.second == null -> {
                    val document =
                        Document(key.fieldName, Document(DocumentOperator.LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }))
                    if (document.isNotEmpty()) documents.add(document)
                    return document
                }

                else -> {
                    val document = Document(key.fieldName,
                        Document(DocumentOperator.LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }).append(
                            DocumentOperator.GREATER_THAN_EQUAL,
                            values.second?.let { it.convertIfId() })
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    return document
                }
            }
        }

        infix fun <T, R> Field<T, R>.notBetweenInclusive(values: Pair<R?, R?>): Document {
            return when {
                values.first == null && values.second == null -> {
                    Document()
                }

                values.first == null -> {
                    val document = Document(
                        key.fieldName, Document(DocumentOperator.NOT, Document(DocumentOperator.GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() }))
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                values.second == null -> {
                    val document = Document(
                        key.fieldName, Document(DocumentOperator.NOT, Document(DocumentOperator.LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }))
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }

                else -> {
                    val document = Document(
                        key.fieldName, Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }).append(
                                DocumentOperator.GREATER_THAN_EQUAL,
                                values.second?.let { it.convertIfId() })
                        )
                    )
                    if (document.isNotEmpty()) documents.add(document)
                    document
                }
            }
        }

        infix fun <T, R> Field<T, R>.`in`(values: Iterable<R>): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.IN, values.map { it.convertIfId() }))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.`in`(values: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.IN, values.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.notIn(values: Iterable<R>): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.NOT_IN, values.map { it.convertIfId() }))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.notIn(values: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.NOT_IN, values.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.contains(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.REGEX, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.containsIgnoreCase(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.REGEX, value.convertIfId()).append(
                DocumentOperator.OPTIONS,
                DocumentOperator.CASE_INSENSITIVE
            ))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.containsNot(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.NOT, Document(DocumentOperator.REGEX, value.convertIfId())))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.containsNotIgnoreCase(value: R): Document {
            val document = Document(
                key.fieldName, Document(
                    DocumentOperator.NOT, Document(DocumentOperator.REGEX, value.convertIfId()).append(
                        DocumentOperator.OPTIONS,
                        DocumentOperator.CASE_INSENSITIVE
                    ))
            )
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.startsWith(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.REGEX, "^${value.convertIfId()}"))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.endsWith(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.REGEX, "${value.convertIfId()}$"))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.match(value: R): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.MATCH, value.convertIfId()))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        // 이 아래 3개의 operator는 테스트가 더 필요하다.
        infix fun <T, R> Field<T, R>.all(value: Iterable<R>): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.ALL, value.map { it.convertIfId() }))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.size(value: Int): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.SIZE, value))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }

        infix fun <T, R> Field<T, R>.exists(value: Boolean): Document {
            val document = Document(key.fieldName, Document(DocumentOperator.EXISTS, value))
            if (document.isNotEmpty()) documents.add(document)
            return document
        }
    }

    class AndDocumentOperatorBuilder: RootDocumentOperatorBuilder()
    class OrDocumentOperatorBuilder: RootDocumentOperatorBuilder()
    class NorDocumentOperatorBuilder: RootDocumentOperatorBuilder()
    class EmbeddedDocumentOperatorBuilder: RootDocumentOperatorBuilder()
}