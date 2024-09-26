package com.kmsl.dsl.clazz

import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Update

data class UpdateQuery(
    val query: BasicQuery,
    val update: Update,
) {
    companion object {
        fun of(
            query: BasicQuery,
            update: Update,
        ) = UpdateQuery(query, update)
    }
}