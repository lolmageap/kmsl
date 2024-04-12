package com.example.kotlinmongo

import org.bson.Document

class OrOperatorBuilder private constructor(
    private val document: Document,
) {
    /**
     * or 함수 스코프 안에 두 개 이상의 함수를 넣게 되면 두 함수는 and 조건으로 연결됩니다.
     *
     * or 함수를 각각 사용한다면 or 조건으로 연결됩니다.
     *
     * 하나의 or scope 에서 동일한 field 를 사용하면 마지막에 사용한 field 가 적용 됩니다.
     *
     * 위 문제를 해결 하려면 or scope 내에서 and 함수를 사용 하여 각각의 field 에 대한 조건을 걸어야 합니다.
     */
    fun or(
        function: Document.() -> (Document),
    ) {
        orCollection.add(function)
    }

    fun Document.and(
        vararg function: Document.() -> (Document),
    ): Document {
        this.append("\$and", function.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    companion object {
        private val orCollection = mutableListOf<Document.() -> (Document)>()

        fun open(
            document: Document,
            function: OrOperatorBuilder.() -> Unit,
        ): Document {
            val orOperatorBuilder = OrOperatorBuilder(document)
            orOperatorBuilder.function()

            if (orCollection.isNotEmpty()) {
                val orValue = orCollection.map {
                    val doc = Document()
                    doc.it()
                }

                orOperatorBuilder.document.append("\$or", orValue)
            }

            return orOperatorBuilder.document
        }
    }
}