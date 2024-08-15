package com.example.kotlinmongo

import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Status
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.field
import com.example.kotlinmongo.extension.find
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
class IdTest(
    private val mongoTemplate: MongoTemplate,
): StringSpec({
    "id 값으로 조회" {
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
            and { field(Author::id) eq author.id }
        }

        val result = mongoTemplate.find(document, Author::class).first()
        result shouldBe author
    }
})