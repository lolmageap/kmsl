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
import jakarta.persistence.Id
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

class Field<T, R>(
    val key: KProperty1<T, R>,
    private val document: Document,
) {
    infix fun eq(value: R): Document {
        return document.append(key.getName(), value.convertIfId())
    }

    infix fun ne(value: R): Document {
        return document.append(key.getName(), Document(NOT_EQUAL, value.convertIfId()))
    }

    infix fun lt(value: R): Document {
        return document.append(key.getName(), Document(LESS_THAN, value.convertIfId()))
    }

    infix fun lte(value: R): Document {
        return document.append(key.getName(), Document(LESS_THAN_EQUAL, value.convertIfId()))
    }

    infix fun gt(value: R): Document {
        return document.append(key.getName(), Document(GREATER_THAN, value.convertIfId()))
    }

    infix fun gte(value: R): Document {
        return document.append(key.getName(), Document(GREATER_THAN_EQUAL, value.convertIfId()))
    }

    infix fun between(values: Pair<R?, R?>): Document {
        return when {
            values.first == null && values.second == null -> {
                document
            }

            values.first == null -> {
                document.append(key.getName(), Document(LESS_THAN, values.second?.let { it.convertIfId() }))
            }

            values.second == null -> {
                document.append(key.getName(), Document(GREATER_THAN, values.first?.let { it.convertIfId() }))
            }

            else -> {
                document.append(
                    key.getName(), Document(GREATER_THAN, values.first?.let { it.convertIfId() })
                        .append(LESS_THAN, values.second?.let { it.convertIfId() })
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
                document.append(key.getName(), Document(NOT, Document(LESS_THAN_EQUAL, values.second?.let { it.convertIfId() })))
            }

            values.second == null -> {
                document.append(key.getName(), Document(NOT, Document(GREATER_THAN_EQUAL, values.first?.let { it.convertIfId() })))
            }

            else -> {
                document.append(
                    key.getName(), Document(
                        NOT, Document(GREATER_THAN_EQUAL, values.first?.let { it.convertIfId() })
                            .append(LESS_THAN_EQUAL, values.second?.let { it.convertIfId() })
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
                document.append(key.getName(), Document(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() }))
            }

            values.second == null -> {
                document.append(key.getName(), Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() }))
            }

            else -> {
                document.append(
                    key.getName(), Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() })
                        .append(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() })
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
                document.append(key.getName(), Document(NOT, Document(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() })))
            }

            values.second == null -> {
                document.append(key.getName(), Document(NOT, Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() })))
            }

            else -> {
                document.append(
                    key.getName(), Document(
                        NOT, Document(LESS_THAN_EQUAL, values.first?.let { it.convertIfId() })
                            .append(GREATER_THAN_EQUAL, values.second?.let { it.convertIfId() })
                    )
                )
            }
        }
    }

    infix fun `in`(values: Iterable<R>): Document {
        return document.append(key.getName(), Document(IN, values.map { it.convertIfId() }))
    }

    infix fun notIn(values: Iterable<R>): Document {
        return document.append(key.getName(), Document(NOT_IN, values.map { it.convertIfId() }))
    }

    infix fun contains(value: R): Document {
        return document.append(key.getName(), Document(REGEX, value.convertIfId()))
    }

    infix fun containsIgnoreCase(value: R): Document {
        return document.append(key.getName(), Document(REGEX, value.convertIfId()).append(OPTIONS, CASE_INSENSITIVE))
    }

    infix fun containsNot(value: R): Document {
        return document.append(key.getName(), Document(NOT, Document(REGEX, value.convertIfId())))
    }

    infix fun containsNotIgnoreCase(value: R): Document {
        return document.append(key.getName(), Document(NOT, Document(REGEX, value.convertIfId()).append(OPTIONS, CASE_INSENSITIVE)))
    }

    infix fun startsWith(value: R): Document {
        return document.append(key.getName(), Document(REGEX, "^${value.convertIfId()}"))
    }

    infix fun endsWith(value: R): Document {
        return document.append(key.getName(), Document(REGEX, "${value.convertIfId()}$"))
    }

    infix fun match(value: R): Document {
        return document.append(key.getName(), Document(MATCH, value.convertIfId()))
    }

    // 이 아래 3개의 operator는 테스트가 더 필요하다.
    infix fun all(value: Iterable<R>): Document {
        return document.append(key.getName(), Document(ALL, value.map { it.convertIfId() }))
    }

    infix fun size(value: Int): Document {
        return document.append(key.getName(), Document(SIZE, value))
    }

    infix fun exists(value: Boolean): Document {
        return document.append(key.getName(), Document(EXISTS, value))
    }

    private fun KProperty1<T, R>.getName(): String {
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
        else if(this is Enum<*>) this.name
        else this
    }
}