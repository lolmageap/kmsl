package com.example.kotlinmongo.clazz

import org.bson.Document

class AndOperatorBuilder private constructor(
    private val document: Document,
) {
    /**
     * 동일한 필드를 중복해서 조건 걸어 사용하려면 and scope 를 사용 해야 합니다.
     */
    fun and(
        function: Document.() -> (Document),
    ) {
        andCollection.add(function)
    }

    companion object {
        private val andCollection = mutableListOf<Document.() -> (Document)>()

        fun open(
            document: Document,
            function: AndOperatorBuilder.() -> Unit,
        ): Document {
            val orOperatorBuilder = AndOperatorBuilder(document)
            orOperatorBuilder.function()

            if (andCollection.isNotEmpty()) {
                val orValue = andCollection.map { invoke ->
                    Document().invoke()
                }

                orOperatorBuilder.document.append("\$and", orValue)
            }

            return orOperatorBuilder.document
        }
    }
}