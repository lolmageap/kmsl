package com.kmsl.dsl

import com.kmsl.dsl.collection.Author
import com.kmsl.dsl.collection.Status.ACTIVE
import com.kmsl.dsl.collection.Status.RETIREMENT
import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.extension.document
import com.kmsl.dsl.extension.find
import com.kmsl.dsl.util.WithTestContainers
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [KmslApplication::class])
class ArrayTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : WithTestContainers, StringSpec({
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

    "Equal operation on array fields" {
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

    "In operations on array fields" {
        val book = mutableListOf(createBook("book1"))

        val document = document {
            field(Author::books) `in` book
        }

        val author = mongoTemplate.find(document, Author::class).first()
        val titles = author.books.map { it.title }
        titles shouldBe listOf("book1", "book2")
    }
})