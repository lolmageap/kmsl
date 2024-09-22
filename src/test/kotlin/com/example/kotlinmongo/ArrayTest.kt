package com.example.kotlinmongo

import cherhy.mongo.dsl.clazz.field
import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Status.ACTIVE
import com.example.kotlinmongo.collection.Status.RETIREMENT
import cherhy.mongo.dsl.extension.document
import cherhy.mongo.dsl.extension.find
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
class ArrayTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : StringSpec({
    beforeTest {
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 10,
                weight = 70.0,
                height = 170f,
                status = RETIREMENT,
                books = mutableListOf(
                    createBook("book1"),
                    createBook("book2"),
                ),
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 20,
                weight = 80.0,
                height = 180f,
                status = ACTIVE,
                books = mutableListOf(
                    createBook("book3"),
                    createBook("book4"),
                ),
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 30,
                weight = 90.0,
                height = 190f,
                status = ACTIVE,
                books = mutableListOf(
                    createBook("book5"),
                    createBook("book6"),
                ),
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 40,
                weight = 100.0,
                height = 200f,
                status = ACTIVE,
                books = mutableListOf(
                    createBook("book7"),
                    createBook("book8"),
                ),
            )
        )
    }

    afterTest {
        mongoTemplate.dropCollection(Author::class.java)
    }

    "배열 필드에 대한 equal 연산" {
        val books = mutableListOf(
            createBook("book1"),
            createBook("book2"),
        )

        val document = document {
            field(Author::books) eq books
        }

        val author = mongoTemplate.find(document, Author::class).first()
        val titles = author.books.map { it.title }
        titles shouldBe listOf("book1", "book2")
    }

    "배열 필드에 대한 in 연산" {
        val book = mutableListOf(createBook("book1"))

        val document = document {
            field(Author::books) `in` book
        }

        val author = mongoTemplate.find(document, Author::class).first()
        val titles = author.books.map { it.title }
        titles shouldBe listOf("book1", "book2")
    }
})