package com.kmsl.dsl

import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.collection.Author
import com.kmsl.dsl.collection.Status
import com.kmsl.dsl.extension.*
import com.kmsl.dsl.util.WithTestContainers
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [KmslApplication::class])
class UpdateTest(
    private val mongoTemplate: MongoTemplate,
) : WithTestContainers, StringSpec({
    beforeEach {
        mongoTemplate.insertAll(
            listOf(
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
                    nickname = "NotNull"
                ),
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
                    nickname = "Any"
                )
            )
        )
    }

    afterEach {
        mongoTemplate.dropCollection(Author::class.java)
    }

    "Update first document" {
        val document = document {
            field(Author::name) eq "John"
        } order {
            field(Author::age) by DESC
        } update {
            field(Author::age) set 50
        }

        mongoTemplate.updateFirst(document, Author::class)

        val authors = mongoTemplate.findAll(Author::class)
        authors.size shouldBe 2
        authors.map { it.age } shouldContainAll listOf(10, 50)
    }

    "Update all documents" {
        val document = document {
            field(Author::name) eq "John"
        } order {
            field(Author::age) by DESC
        } update {
            field(Author::age) set 50
        }

        mongoTemplate.updateAll(document, Author::class)

        val authors = mongoTemplate.findAll(Author::class)
        authors.size shouldBe 2
        authors.map { it.age }.shouldContainAll(50)
    }

    "Update unset" {
        val document = document {
            field(Author::name) eq "John"
        } order {
            field(Author::age) by DESC
        } update {
            field(Author::nickname) unset Unit
        }

        mongoTemplate.updateFirst(document, Author::class)

        val authors = mongoTemplate.findAll(Author::class)
        authors.size shouldBe 2
        authors[0].nickname shouldBe "NotNull"
        authors[1].nickname shouldBe null
    }
})