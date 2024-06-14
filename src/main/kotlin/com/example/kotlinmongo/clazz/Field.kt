package com.example.kotlinmongo.clazz

import org.bson.Document
import org.bson.types.ObjectId
import kotlin.reflect.KProperty1

class Field<T, R>(
    val key: KProperty1<T, R>,
    private val document: Document,
) {
    operator fun not(): Field<T, R> {
        return Field(key, document.append(name, Document("\$not", Document())))
    }

    infix fun eq(value: R): Document {
        return document.append(name, getValue(value))
    }

    infix fun ne(value: R): Document {
        return document.append(name, Document("\$ne", getValue(value)))
    }

    infix fun lt(value: R): Document {
        return document.append(name, Document("\$lt", getValue(value)))
    }

    infix fun lte(value: R): Document {
        return document.append(name, Document("\$lte", getValue(value)))
    }

    infix fun gt(value: R): Document {
        return document.append(name, Document("\$gt", getValue(value)))
    }

    infix fun gte(value: R): Document {
        return document.append(name, Document("\$gte", getValue(value)))
    }

    infix fun between(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }

            values.first == null -> {
                document.append(name, Document("\$lt", values.second?.let { getValue(it) }))
            }

            values.second == null -> {
                document.append(name, Document("\$gt", values.first?.let { getValue(it) }))
            }

            else -> {
                document.append(
                    name, Document("\$gt", values.first?.let { getValue(it) })
                        .append("\$lt", values.second?.let { getValue(it) })
                )
            }
        }
    }

    infix fun notBetween(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }

            values.first == null -> {
                document.append(name, Document("\$gte", values.second?.let { getValue(it) }))
            }

            values.second == null -> {
                document.append(name, Document("\$lte", values.first?.let { getValue(it) }))
            }

            else -> {
                document.append(
                    name, Document("\$lte", values.first?.let { getValue(it) })
                        .append("\$gte", values.second?.let { getValue(it) })
                )
            }
        }
    }

    infix fun betweenInclusive(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }
            values.first == null -> {
                document.append(name, Document("\$not", Document("\$lte", values.second?.let { getValue(it) })))
            }
            values.second == null -> {
                document.append(name, Document("\$not", Document("\$gte", values.first?.let { getValue(it) })))
            }
            else -> {
                document.append(
                    name, Document(
                        "\$not", Document("\$gte", values.first?.let { getValue(it) })
                            .append("\$lte", values.second?.let { getValue(it) })
                    )
                )
            }
        }
    }

    infix fun notBetweenInclusive(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }
            values.first == null -> {
                document.append(name, Document("\$gt", values.second?.let { getValue(it) }))
            }
            values.second == null -> {
                document.append(name, Document("\$lt", values.first?.let { getValue(it) }))
            }
            else -> {
                document.append(
                    name, Document("\$lt", values.first?.let { getValue(it) })
                        .append("\$gt", values.second?.let { getValue(it) })
                )
            }
        }
    }

    infix fun gtAndLte(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }
            values.first == null -> {
                document.append(name, Document("\$lte", values.second?.let { getValue(it) }))
            }
            values.second == null -> {
                document.append(name, Document("\$gt", values.first?.let { getValue(it) }))
            }
            else -> {
                document.append(name, Document("\$gt", values.first?.let { getValue(it) })
                    .append("\$lte", values.second?.let { getValue(it) })
                )
            }
        }
    }

    infix fun gteAndLt(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }
            values.first == null -> {
                document.append(name, Document("\$lt", values.second?.let { getValue(it) }))
            }
            values.second == null -> {
                document.append(name, Document("\$gte", values.first?.let { getValue(it) }))
            }
            else -> {
                document.append(
                    name, Document("\$gte", values.first?.let { getValue(it) })
                        .append("\$lt", values.second?.let { getValue(it) })
                )
            }
        }
    }

    infix fun `in`(values: Iterable<R>): Document {
        return document.append(name, Document("\$in", values.map { getValue(it) }))
    }

    infix fun nin(values: Iterable<R>): Document {
        return document.append(name, Document("\$nin", values.map { getValue(it) }))
    }

    infix fun contains(value: R): Document {
        return document.append(name, Document("\$regex", getValue(value)))
    }

    infix fun containsIgnoreCase(value: R): Document {
        return document.append(name, Document("\$regex", getValue(value)).append("\$options", "i"))
    }

    infix fun containsNot(value: R): Document {
        return document.append(name, Document("\$not", Document("\$regex", getValue(value))))
    }

    infix fun containsNotIgnoreCase(value: R): Document {
        return document.append(name, Document("\$not", Document("\$regex", getValue(value)).append("\$options", "i")))
    }

    infix fun startsWith(value: R): Document {
        return document.append(name, Document("\$regex", "^${getValue(value)}"))
    }

    infix fun endsWith(value: R): Document {
        return document.append(name, Document("\$regex", "${getValue(value)}$"))
    }

    infix fun match(value: R): Document {
        return document.append(name, Document("\$match", getValue(value)))
    }

    infix fun all(value: Iterable<R>): Document {
        return document.append(name, Document("\$all", value.map { getValue(it) }))
    }

    infix fun size(value: Int): Document {
        return document.append(name, Document("\$size", value))
    }

    infix fun exists(value: Boolean): Document {
        return document.append(name, Document("\$exists", value))
    }

    private val name: String
        get() {
            return if (key.name == "id") "_id"
            else key.name
        }

    private fun getValue(value: R): Any? {
        if (key.name == "id") {
            return ObjectId(value.toString())
        }
        return value
    }
}