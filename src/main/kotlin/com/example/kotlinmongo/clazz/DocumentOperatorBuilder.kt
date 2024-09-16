package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.clazz.DocumentOperator.AND
import com.example.kotlinmongo.clazz.DocumentOperator.NOR
import com.example.kotlinmongo.clazz.DocumentOperator.OR
import org.bson.Document
import kotlin.reflect.KProperty1

class DocumentOperatorBuilder(
    var isEmbeddedDocument: Boolean = false,
) {
    val documents = mutableListOf<Document>()

    fun and(
        block: DocumentOperatorBuilder.() -> Unit,
    ) {
        val andDocumentOperatorBuilder = DocumentOperatorBuilder(this.isEmbeddedDocument)
        andDocumentOperatorBuilder.block()
        val andDocuments = andDocumentOperatorBuilder.documents
        documents.add(Document().append(AND, andDocuments))
    }

    fun or(
        block: DocumentOperatorBuilder.() -> Unit,
    ) {
        val orDocumentOperatorBuilder = DocumentOperatorBuilder(this.isEmbeddedDocument)
        orDocumentOperatorBuilder.block()
        val orDocuments = orDocumentOperatorBuilder.documents
        documents.add(Document().append(OR, orDocuments))
    }

    fun nor(
        block: DocumentOperatorBuilder.() -> Unit,
    ) {
        val norDocumentOperatorBuilder = DocumentOperatorBuilder(this.isEmbeddedDocument)
        norDocumentOperatorBuilder.block()
        val norDocuments = norDocumentOperatorBuilder.documents
        documents.add(Document().append(NOR, norDocuments))
    }

    fun <T, R> embeddedDocument(
        property: KProperty1<T, R>,
    ) = EmbeddedDocument.of(property)

    inline infix fun <T, reified R> EmbeddedDocument<T, R?>.where(
        noinline block: DocumentOperatorBuilder.() -> Unit,
    ) {
        val embeddedDocumentOperatorBuilder = DocumentOperatorBuilder(this@DocumentOperatorBuilder.isEmbeddedDocument)
        embeddedDocumentOperatorBuilder.isEmbeddedDocument = true
        embeddedDocumentOperatorBuilder.block()
        embeddedDocumentOperatorBuilder.isEmbeddedDocument = false
        val whereDocuments = embeddedDocumentOperatorBuilder.documents
        documents.add(Document(AND, whereDocuments))
    }

    fun <T, R> embeddedDocument(
        property: KProperty1<T, List<R>>,
    ) = EmbeddedDocuments.of(property)

    inline infix fun <T, reified R> EmbeddedDocuments<T, R>.elemMatch(
        noinline block: DocumentOperatorBuilder.() -> Unit,
    ) {
        val embeddedDocumentsOperatorBuilder = DocumentOperatorBuilder(this@DocumentOperatorBuilder.isEmbeddedDocument)
        embeddedDocumentsOperatorBuilder.block()
        val elemMatchDocuments = embeddedDocumentsOperatorBuilder.documents
        documents.add(
            Document(
                this.name, Document(DocumentOperator.ELEM_MATCH, Document().append(AND, elemMatchDocuments))
            )
        )
    }

    inline infix fun <reified T : Any, R> Field<T, R>.eq(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, value.convertIfId())
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.ne(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.NOT_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.lt(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.LESS_THAN, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.lte(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.LESS_THAN_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.gt(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.GREATER_THAN, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.gte(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.GREATER_THAN_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.between(
        values: Pair<R?, R?>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document =
                    Document(key, Document(DocumentOperator.LESS_THAN, values.second?.convertIfId()))
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document =
                    Document(
                        key,
                        Document(DocumentOperator.GREATER_THAN, values.first?.convertIfId())
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document =
                    Document(
                        key,
                        Document(DocumentOperator.GREATER_THAN, values.first?.convertIfId()).append(
                            DocumentOperator.LESS_THAN, values.second?.convertIfId()
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    inline infix fun <reified T : Any, R> Field<T, R>.notBetween(
        values: Pair<R?, R?>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.LESS_THAN, values.second?.convertIfId())
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.GREATER_THAN, values.first?.convertIfId())
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.GREATER_THAN, values.first?.convertIfId()).append(
                                DocumentOperator.LESS_THAN, values.second?.convertIfId()
                            )
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    inline infix fun <reified T : Any, R> Field<T, R>.betweenInclusive(
        values: Pair<R?, R?>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document =
                    Document(
                        key,
                        Document(DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId())
                    )
                if (document.isNotEmpty()) documents.add(document)
                return document
            }

            values.second == null -> {
                val document =
                    Document(
                        key,
                        Document(DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId())
                    )
                if (document.isNotEmpty()) documents.add(document)
                return document
            }

            else -> {
                val document =
                    Document(
                        key,
                        Document(DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId()).append(
                            DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId()
                        )
                    )

                if (document.isNotEmpty()) documents.add(document)
                return document
            }
        }
    }

    inline infix fun <reified T : Any, R> Field<T, R>.notBetweenInclusive(
        values: Pair<R?, R?>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId())
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId())
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document =
                    Document(
                        key,
                        Document(
                            DocumentOperator.NOT,
                            Document(DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId()).append(
                                DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId()
                            )
                        )
                    )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    inline infix fun <reified T : Any, R> Field<T, R>.`in`(
        values: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.IN, values.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.`in`(
        values: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.IN, values.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.notIn(
        values: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.NOT_IN, values.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.notIn(
        values: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.NOT_IN, values.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.contains(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.REGEX, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.containsIgnoreCase(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document =
            Document(
                key,
                Document(DocumentOperator.REGEX, value.convertIfId()).append(
                    DocumentOperator.OPTIONS, DocumentOperator.CASE_INSENSITIVE
                )
            )

        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.containsNot(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document =
            Document(
                key,
                Document(DocumentOperator.NOT, Document(DocumentOperator.REGEX, value.convertIfId()))
            )
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.containsNotIgnoreCase(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document =
            Document(
                key,
                Document(
                    DocumentOperator.NOT, Document(DocumentOperator.REGEX, value.convertIfId()).append(
                        DocumentOperator.OPTIONS, DocumentOperator.CASE_INSENSITIVE
                    )
                )
            )
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.startsWith(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.REGEX, "^${value.convertIfId()}"))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.endsWith(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.REGEX, "${value.convertIfId()}$"))

        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.match(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.MATCH, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.all(
        value: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.ALL, value.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.size(
        value: Int,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        val document = Document(key, Document(DocumentOperator.SIZE, value))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    inline infix fun <reified T : Any, R> Field<T, R>.exists(
        value: Boolean,
    ): Document {
        val key = if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
        else key.fieldName
        val document = Document(key, Document(DocumentOperator.EXISTS, value))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }
}