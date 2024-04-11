package com.example.kotlinmongo

import org.bson.Document

class OperatorBuilder private constructor(
    private val document: Document,
) {
    /**
     * or 함수 스코프 안에 두 개 이상의 함수를 넣게 되면 두 함수는 and 조건으로 연결됩니다.
     *
     * or 함수를 각각 사용한다면 or 조건으로 연결됩니다.
     *
     * 하나의 or scope 에서 동일한 field 를 사용하면 마지막에 사용한 field 가 적용 됩니다.
     *
     * TODO: 위와 같은 상황을 해결 하려면 or scope 내에서 and scope 를 여러번 사용하면 됩니다. (아직 미구현)
     */
    fun or(
        function: Document.() -> (Document),
    ) {
        orCollection.add(function)
    }

    fun and(
        function: Document.() -> (Document),
    ) {
        andCollection.add(function)
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
        private val andCollection = mutableListOf<Document.() -> (Document)>()

        fun open(
            document: Document,
            function: OperatorBuilder.() -> Unit,
        ): Document {
            val operatorBuilder = OperatorBuilder(document)
            operatorBuilder.function()

            val andValue = andCollection.map {
                val doc = Document()
                doc.it()
            }

            val orValue = orCollection.map {
                val doc = Document()
                doc.it()
            }

            operatorBuilder.document.append("\$and", andValue)
            operatorBuilder.document.append("\$or", orValue)
            return operatorBuilder.document
        }
    }
}