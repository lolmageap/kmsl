package com.kmsl.dsl.clazz

import com.kmsl.dsl.extension.matchOperation
import org.bson.Document
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * grouping 을 할때 내부적으로 matchOperation 을 사용합니다.
 * side effect 가 발생하지 않으려면 Group By 는 마지막 줄에 위치 시키는 것이 좋습니다.
 */
open class Group<T, R>(
    val document: Document,
    private val groupProperties: MutableSet<KProperty1<T, R>> = mutableSetOf(),
) {
    private var isSingleGroup: Boolean? = null

    class Sum(
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
            GroupOperationWrapper(document, groupOperation.avg(this.key.toDotPath()).`as`(value))
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
            GroupOperationWrapper(document, groupOperation.max(this.key.toDotPath()).`as`(value))
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

    infix fun Field<T, R>.by(
        type: GroupType,
    ) {
        isSingleGroup = if (type == GroupType.SINGLE) {
            if (isSingleGroup == false) {
                error("Single Group으로 선언되어 있지 않습니다.")
            }
            true
        } else {
            if (isSingleGroup == true) {
                error("Single Group으로 선언되어 있습니다.")
            }
            false
        }

        groupProperties.add(this.key)
    }

    infix fun Field<T, R>.and(
        field: Field<T, R>,
    ): Group<T, R> {
        if (isSingleGroup == true) {
            error("Single Group으로 선언되어 있습니다.")
        }

        isSingleGroup = false
        groupProperties.add(this.key)
        groupProperties.add(field.key)

        return this@Group
    }

    infix fun and(
        field: Field<T, R>,
    ): Group<T, R> {
        if (isSingleGroup == true) {
            error("Single Group으로 선언되어 있습니다.")
        }

        isSingleGroup = false
        groupProperties.add(field.key)

        return this@Group
    }

    class GroupOperationWrapper(
        val document: Document,
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

    infix fun sum(
        block: Sum.() -> GroupOperationWrapper,
    ) =
        Sum(document, Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())).block()

    infix fun average(
        block: Average.() -> GroupOperationWrapper,
    ) =
        Average(document, Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())).block()

    infix fun max(
        block: Max.() -> GroupOperationWrapper,
    ) =
        Max(document, Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())).block()

    infix fun min(
        block: Min.() -> GroupOperationWrapper,
    ) =
        Min(document, Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())).block()

    infix fun count(
        block: Count.() -> GroupOperationWrapper,
    ) =
        Count(document, Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())).block()

    fun toAggregation(): Aggregation {
        val matchOperation = document.matchOperation()
        return Aggregation.newAggregation(
            matchOperation,
            Aggregation.group(*groupProperties.map { "\$${it.toDotPath()}" }.toTypedArray())
        )
    }
}