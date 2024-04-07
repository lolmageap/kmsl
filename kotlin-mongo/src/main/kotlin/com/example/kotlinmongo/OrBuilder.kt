package com.example.kotlinmongo

import org.bson.Document

class OrBuilder private constructor(
    private val document: Document,
) {
    /**
     * or 함수 스코프 안에 두 개 이상의 함수를 넣게 되면 두 함수는 and 조건으로 연결됩니다.
     *
     * or 함수를 각각 사용한다면 or 조건으로 연결됩니다.
     */
    fun or(
        function: Document.() -> (Document),
    ) {
        orCollection.add(function)
    }

    companion object {
        private val orCollection = mutableListOf<Document.() -> (Document)>()
        fun open(
            document: Document,
            function: OrBuilder.() -> Unit,
        ): Document {
            val orBuilder = OrBuilder(document)
            orBuilder.function()

            val orValue = orCollection.map {
                val doc = Document()
                doc.it()
            }

            orBuilder.document.append("\$or", orValue)
            return orBuilder.document
        }
    }
}