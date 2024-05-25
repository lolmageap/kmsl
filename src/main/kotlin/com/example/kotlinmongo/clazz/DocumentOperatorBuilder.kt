package com.example.kotlinmongo.clazz

import org.bson.Document

class DocumentOperatorBuilder {
    fun and(
        vararg block: Document.() -> (Document),
    ): Document {
        return Document().append("\$and", block.map {
            val doc = Document()
            doc.it()
        })
    }

    fun or(
        vararg block: Document.() -> (Document),
    ): Document {
        return Document().append("\$or", block.map {
            val doc = Document()
            doc.it()
        })
    }

    fun nor(
        vararg block: Document.() -> (Document),
    ): Document {
        return Document().append("\$nor", block.map {
            val doc = Document()
            doc.it()
        })
    }

    fun not(
        vararg block: Document.() -> (Document),
    ): Document {
        return Document().append("\$not", block.map {
            val doc = Document()
            doc.it()
        })
    }

    fun Document.and(
        vararg block: Document.() -> (Document),
    ): Document {
        this.append("\$and", block.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun Document.or(
        vararg block: Document.() -> (Document),
    ): Document {
        this.append("\$or", block.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun Document.nor(
        vararg block: Document.() -> (Document),
    ): Document {
        this.append("\$nor", block.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun Document.not(
        vararg block: Document.() -> (Document),
    ): Document {
        this.append("\$not", block.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    private fun run(
        document: Document,
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
            return DocumentOperatorBuilder()
                .run(document, block)
        }
    }
}