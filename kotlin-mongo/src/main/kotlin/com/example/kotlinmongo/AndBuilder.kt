package com.example.kotlinmongo

import org.bson.Document

class AndBuilder private constructor(
    private val document: Document,
) {
    fun and(
        function: Document.() -> (Document),
    ) {
        andCollection.add(function)
    }

    companion object {
        private val andCollection = mutableListOf<Document.() -> (Document)>()

        fun open(
            document: Document,
            function: AndBuilder.() -> Unit,
        ): Document {
            val andBuilder = AndBuilder(document)
            andBuilder.function()

            val andValue = andCollection.map {
                val doc = Document()
                doc.it()
            }

            andBuilder.document.append("\$and", andValue)
            return andBuilder.document
        }
    }
}