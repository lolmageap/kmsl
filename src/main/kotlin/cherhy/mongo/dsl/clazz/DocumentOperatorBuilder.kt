package cherhy.mongo.dsl.clazz

import cherhy.mongo.dsl.clazz.DocumentOperator.AND
import cherhy.mongo.dsl.clazz.DocumentOperator.NOR
import cherhy.mongo.dsl.clazz.DocumentOperator.OR
import org.bson.Document
import kotlin.reflect.KProperty1

class DocumentOperatorBuilder(
    var isEmbeddedDocument: Boolean = false,
) {
    val documents = mutableListOf<Document>()

    fun and(
        block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
    ) {
        val andDocumentOperatorBuilder =
            cherhy.mongo.dsl.clazz.DocumentOperatorBuilder(this.isEmbeddedDocument)
        andDocumentOperatorBuilder.block()
        val andDocuments = andDocumentOperatorBuilder.documents
        if (andDocuments.isNotEmpty()) documents.add(Document(AND, andDocuments))
    }

    fun or(
        block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
    ) {
        val orDocumentOperatorBuilder =
            cherhy.mongo.dsl.clazz.DocumentOperatorBuilder(this.isEmbeddedDocument)
        orDocumentOperatorBuilder.block()
        val orDocuments = orDocumentOperatorBuilder.documents
        if (orDocuments.isNotEmpty()) documents.add(Document(OR, orDocuments))
    }

    fun nor(
        block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
    ) {
        val norDocumentOperatorBuilder =
            cherhy.mongo.dsl.clazz.DocumentOperatorBuilder(this.isEmbeddedDocument)
        norDocumentOperatorBuilder.block()
        val norDocuments = norDocumentOperatorBuilder.documents
        if (norDocuments.isNotEmpty()) documents.add(Document(NOR, norDocuments))
    }

    fun <T, R> embeddedDocument(
        property: KProperty1<T, R>,
    ) = cherhy.mongo.dsl.clazz.EmbeddedDocument.Companion.of(property)

    inline infix fun <T, reified R> cherhy.mongo.dsl.clazz.EmbeddedDocument<T, R?>.where(
        noinline block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
    ) {
        val embeddedDocumentOperatorBuilder =
            cherhy.mongo.dsl.clazz.DocumentOperatorBuilder(this@DocumentOperatorBuilder.isEmbeddedDocument)
        embeddedDocumentOperatorBuilder.isEmbeddedDocument = true
        embeddedDocumentOperatorBuilder.block()
        embeddedDocumentOperatorBuilder.isEmbeddedDocument = false
        val whereDocuments = embeddedDocumentOperatorBuilder.documents
        if (whereDocuments.isNotEmpty()) documents.add(Document(AND, whereDocuments))
    }

    fun <T, R> embeddedDocument(
        property: KProperty1<T, List<R>>,
    ) = cherhy.mongo.dsl.clazz.EmbeddedDocuments.Companion.of(property)

    inline infix fun <T, reified R> cherhy.mongo.dsl.clazz.EmbeddedDocuments<T, R>.elemMatch(
        noinline block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
    ) {
        val embeddedDocumentsOperatorBuilder =
            cherhy.mongo.dsl.clazz.DocumentOperatorBuilder(this@DocumentOperatorBuilder.isEmbeddedDocument)
        embeddedDocumentsOperatorBuilder.block()
        val elemMatchDocuments = embeddedDocumentsOperatorBuilder.documents
        if (elemMatchDocuments.isNotEmpty()) {
            documents.add(
                Document(
                    this.name,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.ELEM_MATCH, Document(AND, elemMatchDocuments))
                )
            )
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.eq(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(key, value.convertIfId()).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.ne(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.NOT_EQUAL, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.lt(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.lte(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN_EQUAL, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.gt(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.gte(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN_EQUAL, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.between(
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
                Document(key, Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN, values.second?.convertIfId())).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            values.second == null -> {
                Document(
                    key,
                    Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN, values.first?.convertIfId())
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            else -> {
                Document(
                    key,
                    Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN, values.first?.convertIfId()).append(
                        cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN, values.second?.convertIfId()
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.notBetween(
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
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN, values.second?.convertIfId())
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            values.second == null -> {
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN, values.first?.convertIfId())
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            else -> {
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN, values.first?.convertIfId()).append(
                            cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN, values.second?.convertIfId()
                        )
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.betweenInclusive(
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
                Document(
                    key,
                    Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId())
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            values.second == null -> {
                Document(
                    key,
                    Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId())
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            else -> {
                Document(
                    key,
                    Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId()).append(
                        cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId()
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.notBetweenInclusive(
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
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId())
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            values.second == null -> {
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId())
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }

            else -> {
                Document(
                    key,
                    Document(
                        cherhy.mongo.dsl.clazz.DocumentOperator.NOT,
                        Document(cherhy.mongo.dsl.clazz.DocumentOperator.GREATER_THAN_EQUAL, values.first?.convertIfId()).append(
                            cherhy.mongo.dsl.clazz.DocumentOperator.LESS_THAN_EQUAL, values.second?.convertIfId()
                        )
                    )
                ).apply {
                    if (isNotEmpty()) documents.add(this)
                }
            }
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.`in`(
        values: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.IN, values.map { it.convertIfId() }),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.`in`(
        values: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.IN, values.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.notIn(
        values: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.NOT_IN, values.map { it.convertIfId() }),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.notIn(
        values: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.NOT_IN, values.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.contains(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.containsIgnoreCase(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, value.convertIfId()).append(
                cherhy.mongo.dsl.clazz.DocumentOperator.OPTIONS,
                cherhy.mongo.dsl.clazz.DocumentOperator.CASE_INSENSITIVE
            )
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.containsNot(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.NOT, Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, value.convertIfId()))
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.containsNotIgnoreCase(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(
                cherhy.mongo.dsl.clazz.DocumentOperator.NOT, Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, value.convertIfId()).append(
                    cherhy.mongo.dsl.clazz.DocumentOperator.OPTIONS,
                    cherhy.mongo.dsl.clazz.DocumentOperator.CASE_INSENSITIVE
                )
            )
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.startsWith(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, "^${value.convertIfId()}"),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.endsWith(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.REGEX, "${value.convertIfId()}$"),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.match(
        value: R,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.MATCH, value.convertIfId()),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.all(
        value: Iterable<R>,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.ALL, value.map { it.convertIfId() }),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.size(
        value: Int,
    ): Document {
        val key =
            if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
            else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.SIZE, value),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }

    inline infix fun <reified T : Any, R> cherhy.mongo.dsl.clazz.Field<T, R>.exists(
        value: Boolean,
    ): Document {
        val key = if (isEmbeddedDocument) "${this.getClassName(T::class)}.${key.fieldName}"
        else key.fieldName

        return Document(
            key,
            Document(cherhy.mongo.dsl.clazz.DocumentOperator.EXISTS, value),
        ).apply {
            if (isNotEmpty()) documents.add(this)
        }
    }
}