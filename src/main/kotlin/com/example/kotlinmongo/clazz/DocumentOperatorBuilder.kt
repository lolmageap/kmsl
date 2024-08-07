package com.example.kotlinmongo.clazz

import com.example.kotlinmongo.clazz.DocumentOperator.AND
import com.example.kotlinmongo.clazz.DocumentOperator.ELEM_MATCH
import com.example.kotlinmongo.clazz.DocumentOperator.NOR
import com.example.kotlinmongo.clazz.DocumentOperator.NOT
import com.example.kotlinmongo.clazz.DocumentOperator.OR
import org.bson.Document

class DocumentOperatorBuilder(
    val document: Document,
) {
    fun and(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) document.append(AND, nonEmptyBlocks)
        else document
    }

    fun or(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) document.append(OR, nonEmptyBlocks)
        else document
    }

    fun nor(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) document.append(NOR, nonEmptyBlocks)
        else document
    }

    fun not(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) document.append(NOT, nonEmptyBlocks)
        else document
    }

    fun Document.and(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(AND, nonEmptyBlocks)
        else this
    }

    fun Document.or(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(OR, nonEmptyBlocks)
        else this
    }

    fun Document.nor(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(NOR, nonEmptyBlocks)
        else this
    }

    fun Document.not(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(NOT, nonEmptyBlocks)
        else this
    }

    // elemMatch 는 아직 테스트를 해보지 않았습니다.
    fun Document.elemMatch(
        vararg block: Document.() -> Document,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)
        return if (nonEmptyBlocks.isNotEmpty()) this.append(ELEM_MATCH, nonEmptyBlocks)
        else this
    }

    fun Document.and(
        block: List<Document.() -> Document>,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(AND, nonEmptyBlocks)
        else this
    }

    fun Document.or(
        block: List<Document.() -> Document>,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(OR, nonEmptyBlocks)
        else this
    }

    fun Document.nor(
        block: List<Document.() -> Document>,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(NOR, nonEmptyBlocks)
        else this
    }

    fun Document.not(
        block: List<Document.() -> Document>,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(NOT, nonEmptyBlocks)
        else this
    }

    // elemMatch 는 아직 테스트를 해보지 않았습니다.
    fun Document.elemMatch(
        block: List<Document.() -> Document>,
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        return if (nonEmptyBlocks.isNotEmpty()) this.append(ELEM_MATCH, nonEmptyBlocks)
        else this
    }

    private fun applyNotEmptyFunctions(
        block: Array<out Document.() -> Document>,
    ) = block.mapNotNull {
        val doc = Document().it()
        doc.ifEmpty { null }
    }

    private fun applyNotEmptyFunctions(
        block: List<Document.() -> Document>,
    ) = block.mapNotNull {
        val doc = Document().it()
        doc.ifEmpty { null }
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