package com.example.kotlinmongo

import org.bson.Document
import kotlin.reflect.KProperty1

class Field<T, R>(
    private val key: KProperty1<T, R>,
    private val document: Document,
) {
    infix fun eq(value: R): Document {
        return document.append(key.name, value)
    }

    infix fun lt(value: R): Document {
        return document.append(key.name, Document("\$lt", value))
    }

    infix fun lte(value: R): Document {
        return document.append(key.name, Document("\$lte", value))
    }

    infix fun gt(value: R): Document {
        return document.append(key.name, Document("\$gt", value))
    }

    infix fun gte(value: R): Document {
        return document.append(key.name, Document("\$gte", value))
    }

    infix fun ne(value: R): Document {
        return document.append(key.name, Document("\$ne", value))
    }

    infix fun `in`(values: List<R>): Document {
        return document.append(key.name, Document("\$in", values))
    }

    infix fun nin(values: List<R>): Document {
        return document.append(key.name, Document("\$nin", values))
    }
}