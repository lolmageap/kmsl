package com.example.kotlinmongo

import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Status
import com.kmsl.dsl.KmslApplication
import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.extension.document
import com.kmsl.dsl.extension.findOne
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [KmslApplication::class])
class IdTest(
    private val mongoTemplate: MongoTemplate,
) : StringSpec({
    "Lookup by ID value" {
        val author = mongoTemplate.insert(
            Author.of(
                name = "Test",
                age = 100,
                weight = 170.0,
                height = 70f,
                status = Status.RETIREMENT,
                books = mutableListOf(),
            )
        )

        val document = document {
            field(Author::id) eq author.id
        }

        val result = mongoTemplate.findOne(document, Author::class)
        result shouldBe author
    }
})