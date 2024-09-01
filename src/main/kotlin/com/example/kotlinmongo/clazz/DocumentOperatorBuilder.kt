package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.clazz.DocumentOperator.AND
import com.example.kotlinmongo.clazz.DocumentOperator.NOR
import com.example.kotlinmongo.clazz.DocumentOperator.OR
import org.bson.Document

class DocumentOperatorBuilder {
    open class RootDocumentOperatorBuilder {
        val documents = mutableListOf<Document>()

        fun and(
            block: AndDocumentOperatorBuilder.() -> Unit,
        ) {
            val andDocumentOperatorBuilder = AndDocumentOperatorBuilder()
            andDocumentOperatorBuilder.block()
            val and = andDocumentOperatorBuilder.documents
            documents.add(Document().append(AND, and))
        }

        fun or(
            block: OrDocumentOperatorBuilder.() -> Unit,
        ) {
            val orDocumentOperatorBuilder = OrDocumentOperatorBuilder()
            orDocumentOperatorBuilder.block()
            val or = orDocumentOperatorBuilder.documents
            documents.add(Document().append(OR, or))
        }

        fun nor(
            block: NorDocumentOperatorBuilder.() -> Unit,
        ) {
            val norDocumentOperatorBuilder = NorDocumentOperatorBuilder()
            norDocumentOperatorBuilder.block()
            val nor = norDocumentOperatorBuilder.documents
            documents.add(Document().append(NOR, nor))
        }
    }

    class AndDocumentOperatorBuilder: RootDocumentOperatorBuilder()
    class OrDocumentOperatorBuilder: RootDocumentOperatorBuilder()
    class NorDocumentOperatorBuilder: RootDocumentOperatorBuilder()
}