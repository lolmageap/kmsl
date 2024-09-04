package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.extension.copy
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.query.BasicQuery
import kotlin.reflect.KProperty1

class Order {
    private val sortingList =
        mutableListOf<Pair<KProperty1<*, *>, Direction>>()

    infix fun <T, R> Field<T, R>.by(
        direction: Direction,
    ) {
        sortingList.add(this.key to direction)
    }

    fun sorting(
        basicQuery: BasicQuery,
    ): BasicQuery {
        val document = basicQuery.queryObject.copy()
        val existingSort = basicQuery.extractSortObject()
        val newBasicQuery = BasicQuery(document)

        sortingList.forEach {
            Sort.by(it.second, it.first.toDotPath())
            val combinedSort = existingSort.and(Sort.by(it.second, it.first.toDotPath()))
            newBasicQuery.with(combinedSort)
        }
        return newBasicQuery
    }

    private fun BasicQuery.extractSortObject() =
        Sort.by(
            this.sortObject.entries.map {
                val sort = when (it.value) {
                    ASC -> Direction.ASC
                    else -> Direction.DESC
                }
                Sort.Order(sort, it.key)
            }
        )

    companion object {
        private const val DESC = "-1"
        private const val ASC = "1"
    }
}