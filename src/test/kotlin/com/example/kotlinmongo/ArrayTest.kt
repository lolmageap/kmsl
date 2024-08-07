package com.example.kotlinmongo

import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Status
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.field
import com.example.kotlinmongo.extension.find
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.BasicQuery

@SpringBootTest
class ArrayTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : StringSpec({
    beforeTest {
        mongoTemplate.insert(
            Author(
                name = "John",
                age = 10,
                weight = 70.0,
                height = 170f,
                status = Status.INACTIVE,
                books = listOf(
                    "book1",
                    "book2",
                ),
            )
        )
        mongoTemplate.insert(
            Author(
                name = "John",
                age = 20,
                weight = 80.0,
                height = 180f,
                status = Status.ACTIVE,
                books = listOf(
                    "book3",
                    "book4",
                ),
            )
        )
        mongoTemplate.insert(
            Author(
                name = "John",
                age = 30,
                weight = 90.0,
                height = 190f,
                status = Status.ACTIVE,
                books = listOf(
                    "book5",
                    "book6",
                ),
            )
        )
        mongoTemplate.insert(
            Author(
                name = "John",
                age = 40,
                weight = 100.0,
                height = 200f,
                status = Status.ACTIVE,
                books = listOf(
                    "book7",
                    "book8",
                ),
            )
        )
    }

    afterTest {
        mongoTemplate.dropCollection(Author::class.java)
    }

    "배열 필드에 대한 equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::books) eq listOf("book1", "book2") },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"books\" : [\"book1\", \"book2\"]}]}")
        val author = mongoTemplate.find(document, Author::class).first()
        author.books shouldBe listOf("book1", "book2")
    }

    "배열 필드에 대한 in 연산 테스트" {
        val document = document {
            and(
                { field(Author::books) `in` listOf("book1") },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"books\" : {\"\$in\" : [\"book1\"]}}]}")
        val author = mongoTemplate.find(document, Author::class).first()
        author.books shouldBe listOf("book1", "book2")
    }
})