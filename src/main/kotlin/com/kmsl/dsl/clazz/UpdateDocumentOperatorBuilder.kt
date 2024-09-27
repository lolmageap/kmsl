package com.kmsl.dsl.clazz

import org.springframework.data.mongodb.core.query.Update

// TODO : isEmbeddedDocument 도 추가 해야함
class UpdateDocumentOperatorBuilder {
    val update = Update()
    var count = 0

    inline infix fun <reified T : Any, R> Field<T, R>.set(
        value: R,
    ) {
        update.set(
            key.fieldName,
            value.convertIfId()
        )
        count++
    }

    inline infix fun <reified T : Any, R> Field<T, R>.set(
        nothing: Unit,
    ) {
        update.unset(key.fieldName)
        count++
    }

    inline infix fun <reified T : Any, R> Field<T, R>.inc(
        value: Number,
    ) {
        update.inc(
            key.fieldName,
            value
        )
        count++
    }
}

