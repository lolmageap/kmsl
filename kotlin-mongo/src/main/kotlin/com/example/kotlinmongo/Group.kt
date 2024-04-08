package com.example.kotlinmongo

import org.bson.Document
import kotlin.reflect.KProperty1

// side effect 가 발생하지 않으려면 Group By 는 마지막 줄에 위치 시키는 것이 좋습니다.
class Group<T, R>(
    private val key: KProperty1<T, R>,
    private val document: Document,
) {
    fun sumOf(
        sumField: () -> KProperty1<T, *>,
    ): Document {
        return document.append("\$group", Document("_id", "\$${key.name}")
            .append(
                sumField.invoke().name,
                Document("\$sum", "\$${sumField.invoke().name}")
            )
        )
    }

    fun count(): Document {
        return document.append("\$group", Document("_id", "\$${key.name}").append("count", Document("\$sum", 1)))
    }
}