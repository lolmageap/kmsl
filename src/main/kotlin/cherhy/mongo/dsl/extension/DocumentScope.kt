package cherhy.mongo.dsl.extension

import cherhy.mongo.dsl.extension.RootDocumentOperator.*
import org.bson.Document
import org.springframework.data.mongodb.core.query.BasicQuery

fun document(
    documentOperator: RootDocumentOperator = AND,
    block: cherhy.mongo.dsl.clazz.DocumentOperatorBuilder.() -> Unit,
): BasicQuery {
    val document = cherhy.mongo.dsl.clazz.DocumentOperatorBuilder().let {
        it.block()
        if (it.documents.isEmpty()) return BasicQuery(Document())

        when (documentOperator) {
            AND -> Document().append(cherhy.mongo.dsl.clazz.DocumentOperator.AND, it.documents)
            OR -> Document().append(cherhy.mongo.dsl.clazz.DocumentOperator.OR, it.documents)
            NOR -> Document().append(cherhy.mongo.dsl.clazz.DocumentOperator.NOR, it.documents)
        }
    }

    return BasicQuery(document)
}

fun Document.copy() = Document(this)

enum class RootDocumentOperator {
    AND,
    OR,
    NOR,
}