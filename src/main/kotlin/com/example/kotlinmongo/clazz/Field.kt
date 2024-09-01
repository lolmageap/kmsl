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
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class Field<T, R>(
    val key: KProperty1<T, R>,
    private val documents: MutableList<Document>,
) {
    infix fun eq(value: R): Document {
        val document = Document(key.fieldName, value.convertIfId())
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun ne(value: R): Document {
        val document = Document(key.fieldName, Document(NOT_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun lt(value: R): Document {
        val document = Document(key.fieldName, Document(LESS_THAN, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun lte(value: R): Document {
        val document = Document(key.fieldName, Document(LESS_THAN_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun gt(value: R): Document {
        val document = Document(key.fieldName, Document(GREATER_THAN, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun gte(value: R): Document {
        val document = Document(key.fieldName, Document(GREATER_THAN_EQUAL, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun between(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document = Document(key.fieldName, Document(GREATER_THAN, values.second?.let { it.convertIfId() }))
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document = Document(key.fieldName, Document(LESS_THAN, values.first?.let { it.convertIfId() }))
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document = Document(key.fieldName,
                    Document(LESS_THAN, values.first?.let { it.convertIfId() }).append(
                        GREATER_THAN,
                        values.second?.let { it.convertIfId() })
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    infix fun notBetween(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document = Document(
                    key.fieldName, Document(NOT, Document(GREATER_THAN, values.second?.let { it.convertIfId() }))
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document =
                    Document(key.fieldName, Document(NOT, Document(LESS_THAN, values.first?.let { it.convertIfId() })))
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document = Document(
                    key.fieldName, Document(NOT,
                        Document(LESS_THAN, values.first?.let { it.convertIfId() }).append(
                            GREATER_THAN,
                            values.second?.let { it.convertIfId() })
                    )
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    infix fun betweenInclusive(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document =
                    Document(key.fieldName, Document(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() }))
                if (document.isNotEmpty()) documents.add(document)
                return document
            }

            values.second == null -> {
                val document =
                    Document(key.fieldName, Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }))
                if (document.isNotEmpty()) documents.add(document)
                return document
            }

            else -> {
                val document = Document(key.fieldName,
                    Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }).append(
                        GREATER_THAN_EQUAL,
                        values.second?.let { it.convertIfId() })
                )
                if (document.isNotEmpty()) documents.add(document)
                return document
            }
        }
    }

    infix fun notBetweenInclusive(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                Document()
            }

            values.first == null -> {
                val document = Document(
                    key.fieldName, Document(NOT, Document(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() }))
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            values.second == null -> {
                val document = Document(
                    key.fieldName, Document(NOT, Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }))
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }

            else -> {
                val document = Document(
                    key.fieldName, Document(NOT,
                        Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }).append(
                            GREATER_THAN_EQUAL,
                            values.second?.let { it.convertIfId() })
                    )
                )
                if (document.isNotEmpty()) documents.add(document)
                document
            }
        }
    }

    infix fun `in`(values: Iterable<R>): Document {
        val document = Document(key.fieldName, Document(IN, values.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun `in`(values: R): Document {
        val document = Document(key.fieldName, Document(IN, values.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun notIn(values: Iterable<R>): Document {
        val document = Document(key.fieldName, Document(NOT_IN, values.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun notIn(values: R): Document {
        val document = Document(key.fieldName, Document(NOT_IN, values.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun contains(value: R): Document {
        val document = Document(key.fieldName, Document(REGEX, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun containsIgnoreCase(value: R): Document {
        val document = Document(key.fieldName, Document(REGEX, value.convertIfId()).append(OPTIONS, CASE_INSENSITIVE))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun containsNot(value: R): Document {
        val document = Document(key.fieldName, Document(NOT, Document(REGEX, value.convertIfId())))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun containsNotIgnoreCase(value: R): Document {
        val document = Document(
            key.fieldName, Document(NOT, Document(REGEX, value.convertIfId()).append(OPTIONS, CASE_INSENSITIVE))
        )
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun startsWith(value: R): Document {
        val document = Document(key.fieldName, Document(REGEX, "^${value.convertIfId()}"))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun endsWith(value: R): Document {
        val document = Document(key.fieldName, Document(REGEX, "${value.convertIfId()}$"))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun match(value: R): Document {
        val document = Document(key.fieldName, Document(MATCH, value.convertIfId()))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    // 이 아래 3개의 operator는 테스트가 더 필요하다.
    infix fun all(value: Iterable<R>): Document {
        val document = Document(key.fieldName, Document(ALL, value.map { it.convertIfId() }))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun size(value: Int): Document {
        val document = Document(key.fieldName, Document(SIZE, value))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    infix fun exists(value: Boolean): Document {
        val document = Document(key.fieldName, Document(EXISTS, value))
        if (document.isNotEmpty()) documents.add(document)
        return document
    }

    private val KProperty1<T, R>.fieldName: String
        get() {
            val javaField = this.javaField!!
            javaField.isAccessible = true

            val hasIdAnnotation = javaField.annotations.any { it is Id }
            return if (hasIdAnnotation) {
                val hasFieldAnnotation = javaField.annotations.any { it is Field }
                if (hasFieldAnnotation) javaField.annotations.filterIsInstance<Field>().first().value
                else "_id"
            } else this.name
        }

    private fun R.convertIfId(): Any? {
        val javaField = key.javaField!!
        javaField.isAccessible = true

        val hasIdAnnotation = javaField.annotations.any { it is Id }
        return if (hasIdAnnotation) ObjectId(this.toString())
        else if (this is Enum<*>) this.name
        else this
    }
}