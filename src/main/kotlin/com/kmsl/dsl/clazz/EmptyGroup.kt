package com.kmsl.dsl.clazz

import com.kmsl.dsl.extension.matchOperation
import com.kmsl.dsl.extension.toSnakeCase
import org.bson.Document
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import kotlin.reflect.KClass

class EmptyGroup {
    class Sum(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        infix fun <T, R> Field<T, R>.type(
            type: KClass<*>,
        ) =
            AggregationExpression {
                Document(MongoTypeCaster.cast(type), "\$${this.key.toDotPath().toSnakeCase()}")
            }

        infix fun AggregationExpression.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.sum(this).`as`(value))

        infix fun <T, R> Field<T, R>.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.sum(this.key.toDotPath()).`as`(value))
    }

    class Average(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        infix fun <T, R> Field<T, R>.type(
            type: KClass<*>,
        ) =
            AggregationExpression {
                Document(MongoTypeCaster.cast(type), "\$${this.key.toDotPath()}")
            }

        infix fun AggregationExpression.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.avg(this).`as`(value))

        infix fun <T, R> Field<T, R>.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.min(this.key.toDotPath()).`as`(value))
    }

    class Max(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        infix fun <T, R> Field<T, R>.type(
            type: KClass<*>,
        ) =
            AggregationExpression {
                Document(MongoTypeCaster.cast(type), "\$${this.key.toDotPath()}")
            }

        infix fun AggregationExpression.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.max(this).`as`(value))

        infix fun <T, R> Field<T, R>.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.min(this.key.toDotPath()).`as`(value))
    }

    class Min(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        infix fun <T, R> Field<T, R>.type(
            type: KClass<*>,
        ) =
            AggregationExpression {
                Document(MongoTypeCaster.cast(type), "\$${this.key.toDotPath()}")
            }

        infix fun AggregationExpression.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.min(this).`as`(value))

        infix fun <T, R> Field<T, R>.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.min(this.key.toDotPath()).`as`(value))
    }

    class Count(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        infix fun <T, R> Field<T, R>.alias(
            value: String,
        ) =
            GroupOperationWrapper(document, groupOperation.count().`as`(value))
    }

    class GroupOperationWrapper(
        private val document: Document,
        private val groupOperation: GroupOperation,
    ) {
        fun toAggregation(): Aggregation {
            val matchOperation = document.matchOperation()
            return Aggregation.newAggregation(matchOperation, groupOperation)
        }

        infix fun sum(
            block: Sum.() -> GroupOperationWrapper,
        ) =
            Sum(document, this.groupOperation).block()

        infix fun average(
            block: Average.() -> GroupOperationWrapper,
        ) =
            Average(document, this.groupOperation).block()

        infix fun max(
            block: Max.() -> GroupOperationWrapper,
        ) =
            Max(document, this.groupOperation).block()

        infix fun min(
            block: Min.() -> GroupOperationWrapper,
        ) =
            Min(document, this.groupOperation).block()

        infix fun count(
            block: Count.() -> GroupOperationWrapper,
        ) =
            Count(document, this.groupOperation).block()
    }
}