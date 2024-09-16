package com.example.kotlinmongo

import com.example.kotlinmongo.clazz.field
import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Book
import com.example.kotlinmongo.collection.Receipt
import com.example.kotlinmongo.collection.Status
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.find
import com.example.kotlinmongo.extension.order
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
class EmbeddedDocumentTest(
    private val mongoTemplate: MongoTemplate,
) : StringSpec({
    beforeTest {
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 10,
                weight = 70.0,
                height = 170f,
                status = Status.RETIREMENT,
                books = mutableListOf(
                    createBook("book1"),
                    createBook("book2"),
                ),
                receipt = createReceipt("신한"),
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 20,
                weight = 80.0,
                height = 180f,
                status = Status.ACTIVE,
                books = mutableListOf(
                    createBook("book3"),
                    createBook("book4"),
                ),
                receipt = createReceipt("국민"),
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 30,
                weight = 90.0,
                height = 190f,
                status = Status.ACTIVE,
                books = mutableListOf(
                    createBook("book5"),
                    createBook("book6"),
                ),
                receipt = null,
            )
        )
        mongoTemplate.insert(
            Author.of(
                name = "John",
                age = 40,
                weight = 100.0,
                height = 200f,
                status = Status.ACTIVE,
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

    "내부 배열 오브젝트 필드에 대한 연산" {
        val document = document {
            embeddedDocument(Author::books) elemMatch {
                or {
                    field(Book::title) eq "book1"
                    field(Book::title) eq "book3"
                }
            }
        } order {
            field(Author::age) by ASC
        }

        val authors = mongoTemplate.find(document, Author::class)
        authors.size shouldBe 2

        val titles = authors.first().books.map { it.title }

        titles.size shouldBe 2
        titles shouldBe mutableListOf("book1", "book2")
    }

    "내부 단일 오브젝트 필드에 대한 연산" {
        val document = document {
            embeddedDocument(Author::receipt) where {
                and {
                    field(Receipt::card) eq "신한"
                    field(Receipt::price) gte 10000L
                }
            }
        } order {
            field(Author::age) by ASC
        }

        val authors = mongoTemplate.find(document, Author::class)
        authors.size shouldBe 1
    }
})

private fun createBook(
    title: String,
    price: Long = 10000L,
    isbn: String = "isbn",
    description: String? = null,
) =
    Book.of(
        title = title,
        price = price,
        isbn = isbn,
        description = description,
    )

private fun createReceipt(
    card: String,
) =
    Receipt.of(
        date = "2024-09-14",
        card = card,
        price = 10000L,
    )