package com.kmsl.dsl.clazz

import org.springframework.data.mongodb.core.query.Update

// TODO : isEmbeddedDocument 도 추가 해야함
class UpdateDocumentOperatorBuilder {
    val update = Update()
    var count = 0

    inline infix fun <reified T : Any, R> Field<T, R>.set(
        value: R
    ) {
        update.set(
            key.fieldName,
            value.convertIfId()
        )
        count++
    }

    inline infix fun <reified T : Any, R> Field<T, R>.unset(
        value: R
    ) {
        update.unset(
            key.fieldName
        )
        count++
    }
}

