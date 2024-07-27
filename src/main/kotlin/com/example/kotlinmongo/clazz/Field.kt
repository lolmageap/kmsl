package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.clazz.DocumentOperator.ALL
import com.example.kotlinmongo.clazz.DocumentOperator.CASE_INSENSITIVE
import com.example.kotlinmongo.clazz.DocumentOperator.EXISTS
import com.example.kotlinmongo.clazz.DocumentOperator.GREATER_THAN
import com.example.kotlinmongo.clazz.DocumentOperator.GREATER_THAN_EQUAL
import com.example.kotlinmongo.clazz.DocumentOperator.IN
import com.example.kotlinmongo.clazz.DocumentOperator.LESS_THAN
import com.example.kotlinmongo.clazz.DocumentOperator.LESS_THAN_EQUAL
import com.example.kotlinmongo.clazz.DocumentOperator.MATCH
import com.example.kotlinmongo.clazz.DocumentOperator.NOT
import com.example.kotlinmongo.clazz.DocumentOperator.NOT_EQUAL
import com.example.kotlinmongo.clazz.DocumentOperator.NOT_IN
import com.example.kotlinmongo.clazz.DocumentOperator.OPTIONS
import com.example.kotlinmongo.clazz.DocumentOperator.REGEX
import com.example.kotlinmongo.clazz.DocumentOperator.SIZE
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.reflect.KProperty1

class Field<T, R>(
    val key: KProperty1<T, R>,
    private val document: Document,
) {
    infix fun eq(value: R): Document {
        return document.append(name, getValue(value))
    }

    infix fun ne(value: R): Document {
        return document.append(name, Document(NOT_EQUAL, getValue(value)))
    }

    infix fun lt(value: R): Document {
        return document.append(name, Document(LESS_THAN, getValue(value)))
    }

    infix fun lte(value: R): Document {
        return document.append(name, Document(LESS_THAN_EQUAL, getValue(value)))
    }

    infix fun gt(value: R): Document {
        return document.append(name, Document(GREATER_THAN, getValue(value)))
    }

    infix fun gte(value: R): Document {
        return document.append(name, Document(GREATER_THAN_EQUAL, getValue(value)))
    }

    infix fun between(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }

            values.first == null -> {
                document.append(name, Document(LESS_THAN, values.second?.let { getValue(it) }))
            }

            values.second == null -> {
                document.append(name, Document(GREATER_THAN, values.first?.let { getValue(it) }))
            }

            else -> {
                document.append(
                    name, Document(GREATER_THAN, values.first?.let { getValue(it) })
                        .append(LESS_THAN, values.second?.let { getValue(it) })
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
                document.append(name, Document(NOT, Document(LESS_THAN_EQUAL, values.second?.let { getValue(it) })))
            }

            values.second == null -> {
                document.append(name, Document(NOT, Document(GREATER_THAN_EQUAL, values.first?.let { getValue(it) })))
            }

            else -> {
                document.append(
                    name, Document(
                        NOT, Document(GREATER_THAN_EQUAL, values.first?.let { getValue(it) })
                            .append(LESS_THAN_EQUAL, values.second?.let { getValue(it) })
                    )
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
                document.append(name, Document(GREATER_THAN_EQUAL, values.second?.let { getValue(it) }))
            }

            values.second == null -> {
                document.append(name, Document(LESS_THAN_EQUAL, values.first?.let { getValue(it) }))
            }

            else -> {
                document.append(
                    name, Document(LESS_THAN_EQUAL, values.first?.let { getValue(it) })
                        .append(GREATER_THAN_EQUAL, values.second?.let { getValue(it) })
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
                document.append(name, Document(NOT, Document(GREATER_THAN_EQUAL, values.second?.let { getValue(it) })))
            }

            values.second == null -> {
                document.append(name, Document(NOT, Document(LESS_THAN_EQUAL, values.first?.let { getValue(it) })))
            }

            else -> {
                document.append(
                    name, Document(
                        NOT, Document(LESS_THAN_EQUAL, values.first?.let { getValue(it) })
                            .append(GREATER_THAN_EQUAL, values.second?.let { getValue(it) })
                    )
                )
            }
        }
    }

    infix fun `in`(values: Iterable<R>): Document {
        return document.append(name, Document(IN, values.map { getValue(it) }))
    }

    infix fun nin(values: Iterable<R>): Document {
        return document.append(name, Document(NOT_IN, values.map { getValue(it) }))
    }

    infix fun contains(value: R): Document {
        return document.append(name, Document(REGEX, getValue(value)))
    }

    infix fun containsIgnoreCase(value: R): Document {
        return document.append(name, Document(REGEX, getValue(value)).append(OPTIONS, CASE_INSENSITIVE))
    }

    infix fun containsNot(value: R): Document {
        return document.append(name, Document(NOT, Document(REGEX, getValue(value))))
    }

    infix fun containsNotIgnoreCase(value: R): Document {
        return document.append(name, Document(NOT, Document(REGEX, getValue(value)).append(OPTIONS, CASE_INSENSITIVE)))
    }

    infix fun startsWith(value: R): Document {
        return document.append(name, Document(REGEX, "^${getValue(value)}"))
    }

    infix fun endsWith(value: R): Document {
        return document.append(name, Document(REGEX, "${getValue(value)}$"))
    }

    infix fun match(value: R): Document {
        return document.append(name, Document(MATCH, getValue(value)))
    }

    // 이 아래 3개의 operator는 테스트가 더 필요하다.
    infix fun all(value: Iterable<R>): Document {
        return document.append(name, Document(ALL, value.map { getValue(it) }))
    }

    infix fun size(value: Int): Document {
        return document.append(name, Document(SIZE, value))
    }

    infix fun exists(value: Boolean): Document {
        return document.append(name, Document(EXISTS, value))
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