package com.example.kotlinmongo.clazz

import org.bson.Document

class DocumentOperatorBuilder(
    val document: Document,
) {
    fun and(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            document.append("\$and", nonEmptyBlocks)
        }

        return document
    }

    fun or(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            document.append("\$or", nonEmptyBlocks)
        }

        return document
    }

    fun nor(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            document.append("\$nor", nonEmptyBlocks)
        }

        return document
    }

    fun not(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            document.append("\$not", nonEmptyBlocks)
        }

        return document
    }

    fun Document.and(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$and", nonEmptyBlocks)
        }

        return this
    }

    fun Document.or(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$or", nonEmptyBlocks)
        }

        return this
    }

    fun Document.nor(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$nor", nonEmptyBlocks)
        }

        return this
    }

    fun Document.not(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = block.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$not", nonEmptyBlocks)
        }

        return this
    }

    fun Document.elemMatch(
        block: Document.() -> (Document),
    ): Document {
        val doc = Document()
        val result = doc.block()
        if (result.isNotEmpty()) {
            this.append("\$elemMatch", result)
        }

        return this
    }

    fun Document.and(
        function: List<Document.() -> (Document)>,
    ): Document {
        if (function.isEmpty()) return this

        val nonEmptyBlocks = function.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$and", nonEmptyBlocks)
        }

        return this
    }

    fun Document.or(
        function: List<Document.() -> (Document)>,
    ): Document {
        if (function.isEmpty()) return this

        val nonEmptyBlocks = function.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$or", nonEmptyBlocks)
        }

        return this
    }

    fun Document.nor(
        function: List<Document.() -> (Document)>,
    ): Document {
        if (function.isEmpty()) return this

        val nonEmptyBlocks = function.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$nor", nonEmptyBlocks)
        }

        return this
    }

    fun Document.not(
        function: List<Document.() -> (Document)>,
    ): Document {
        if (function.isEmpty()) return this

        val nonEmptyBlocks = function.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$not", nonEmptyBlocks)
        }

        return this
    }

    fun Document.elemMatch(
        function: List<Document.() -> (Document)>,
    ): Document {
        if (function.isEmpty()) return this

        val nonEmptyBlocks = function.mapNotNull {
            val doc = Document()
            val result = doc.it()
            if (result.isEmpty()) null else result
        }

        if (nonEmptyBlocks.isNotEmpty()) {
            this.append("\$elemMatch", nonEmptyBlocks)
        }

        return this
    }

    private fun run(
        block: DocumentOperatorBuilder.() -> Unit,
    ): Document {
        block.invoke(this)
        return document
    }

    companion object {
        operator fun invoke(
            document: Document,
            block: DocumentOperatorBuilder.() -> Unit,
        ): Document {
            return DocumentOperatorBuilder(document)
                .run(block)
        }
    }
}