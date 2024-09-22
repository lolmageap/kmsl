package cherhy.mongo.dsl.extension

import cherhy.mongo.dsl.clazz.*
import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.BasicQuery

infix fun <T, R> BasicQuery.group(
    block: Group<T, R>.() -> Unit,
): Group<T, R> {
    val group = Group<T, R>(this.queryObject.copy())
    group.block()
    return group
}

infix fun BasicQuery.order(
    block: Order.() -> Unit,
): BasicQuery {
    val order = Order()
    order.block()
    return order.sorting(this)
}

infix fun BasicQuery.sum(
    block: EmptyGroup.Sum.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Sum(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.average(
    block: EmptyGroup.Average.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Average(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.max(
    block: EmptyGroup.Max.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Max(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.min(
    block: EmptyGroup.Min.() -> EmptyGroup.GroupOperationWrapper,
) =
    EmptyGroup.Min(this.queryObject.copy(), Aggregation.group()).block()

infix fun BasicQuery.where(
    block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val originalQuery = this.queryObject.copy()
    val newQuery = cherhy.mongo.dsl.clazz.DocumentOperatorBuilder().let {
        it.block()
        if (it.documents.isEmpty()) BasicQuery(Document())
        Document().append(cherhy.mongo.dsl.clazz.DocumentOperator.AND, it.documents)
    }

    return BasicQuery(Document().append(cherhy.mongo.dsl.clazz.DocumentOperator.AND, listOf(originalQuery, newQuery)))
}