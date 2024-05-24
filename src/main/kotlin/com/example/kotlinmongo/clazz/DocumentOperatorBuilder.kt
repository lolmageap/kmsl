package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.Author
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.field
import org.bson.Document

class DocumentOperatorBuilder(
    private val document: Document,
    private val rootOperatorType: RootOperatorType,
    private val function: DocumentOperatorBuilder.() -> Unit,
) {
    private val andCollection = mutableListOf<Document.() -> (Document)>()
    private val orCollection = mutableListOf<Document.() -> (Document)>()
    private val norCollection = mutableListOf<Document.() -> (Document)>()
    private val notCollection = mutableListOf<Document.() -> (Document)>()
    private val callStack = mutableListOf<RootOperatorType>()

    fun and(
        function: Document.() -> (Document),
    ) {
        andCollection.add(function)
        callStack.add(RootOperatorType.AND)
    }

    fun or(
        function: Document.() -> (Document),
    ) {
        orCollection.add(function)
        callStack.add(RootOperatorType.OR)
    }

    fun nor(
        function: Document.() -> (Document),
    ) {
        norCollection.add(function)
        callStack.add(RootOperatorType.NOR)
    }

    fun not(
        function: Document.() -> (Document),
    ) {
        notCollection.add(function)
        callStack.add(RootOperatorType.NOT)
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

    fun Document.or(
        vararg function: Document.() -> (Document),
    ): Document {
        this.append("\$or", function.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun Document.nor(
        vararg function: Document.() -> (Document),
    ): Document {
        this.append("\$nor", function.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun Document.not(
        vararg function: Document.() -> (Document),
    ): Document {
        this.append("\$not", function.map {
            val doc = Document()
            doc.it()
        })
        return this
    }

    fun run(): Document {
        when (rootOperatorType) {
            RootOperatorType.AND -> document.append("\$and", Document())
            RootOperatorType.OR -> document.append("\$or", Document())
            RootOperatorType.NOR -> document.append("\$nor", Document())
            RootOperatorType.NOT -> document.append("\$not", Document())
        }

        function.invoke(this)

        callStack.forEach {
            when (it) {
                RootOperatorType.AND -> {
                    if (andCollection.isNotEmpty()) {
                        val andValue = andCollection.map { invoke ->
                            Document().invoke()
                        }
                        document.append("\$and", andValue)
                    }
                }

                RootOperatorType.OR -> {
                    if (orCollection.isNotEmpty()) {
                        val orValue = orCollection.map { invoke ->
                            Document().invoke()
                        }
                        document.append("\$or", orValue)
                    }
                }

                RootOperatorType.NOR -> {
                    if (norCollection.isNotEmpty()) {
                        val norValue = norCollection.map { invoke ->
                            Document().invoke()
                        }
                        document.append("\$nor", norValue)
                    }
                }

                RootOperatorType.NOT -> {
                    if (notCollection.isNotEmpty()) {
                        val notValue = notCollection.map { invoke ->
                            Document().invoke()
                        }
                        document.append("\$not", notValue)
                    }
                }
            }
        }
        return document
    }
}