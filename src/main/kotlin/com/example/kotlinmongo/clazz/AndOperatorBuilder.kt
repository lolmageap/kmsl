package com.example.kotlinmongo.clazz

import org.bson.Document

class AndOperatorBuilder(
    private val document: Document,
    private val function: AndOperatorBuilder.() -> Unit,
) {
    private val andCollection = mutableListOf<Document.() -> (Document)>()

    /**
     * 동일한 필드를 중복해서 조건 걸어 사용하려면 and scope 를 사용 해야 합니다.
     */
    fun and(
        function: Document.() -> (Document),
    ) {
        andCollection.add(function)
    }

    fun nor(
        function: Document.() -> (Document),
    ) {
        document.append("\$nor", function.invoke(Document()))
    }

    fun run(): Document {
        function.invoke(this)

        if (andCollection.isNotEmpty()) {
            val andValue = andCollection.map { invoke ->
                Document().invoke()
            }

            document.append("\$and", andValue)
        }

        return document
    }
}