package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.extension.copy
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

data class Order(
    val basicQuery: BasicQuery,
    val key: KProperty1<*, *>,
) {
    fun asc(): BasicQuery {
        val document = basicQuery.queryObject.copy()
        val existingSort = basicQuery.extractSortObject()
        val newSort = Sort.by(Sort.Direction.ASC, key.name)
        val combinedSort = existingSort.and(newSort)
        val newBasicQuery = BasicQuery(document)
        newBasicQuery.with(combinedSort)
        return newBasicQuery
    }

    fun desc(): BasicQuery {
        val document = basicQuery.queryObject.copy()
        val existingSort = basicQuery.extractSortObject()
        val newSort = Sort.by(Sort.Direction.DESC, key.name)
        val combinedSort = existingSort.and(newSort)
        val newBasicQuery = BasicQuery(document)
        newBasicQuery.with(combinedSort)
        return newBasicQuery
    }

    private fun BasicQuery.extractSortObject() =
        Sort.by(
            this.sortObject.entries.map {
                val sort = when (it.value) {
                    IS_ASC -> Sort.Direction.ASC
                    else -> Sort.Direction.DESC
                }
                Sort.Order(sort, it.key)
            }
        )

    companion object {
        private const val IS_DESC = "-1"
        private const val IS_ASC = "1"
    }
}