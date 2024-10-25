package com.example.kotlinmongo

import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.AuthorAndSeller
import com.example.kotlinmongo.collection.Seller
import com.example.kotlinmongo.collection.Status.ACTIVE
import com.example.kotlinmongo.collection.Status.RETIREMENT
import com.kmsl.dsl.KmslApplication
import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.extension.aggregate
import com.kmsl.dsl.extension.document
import com.kmsl.dsl.extension.join
import com.kmsl.dsl.extension.projection
import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [KmslApplication::class])
class JoinTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : StringSpec({
    beforeTest {
        val author = mongoTemplate.insert(
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
        val author2 = mongoTemplate.insert(
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

        val seller = createSeller(author.id!!)
        val seller2 = createSeller(author2.id!!)
        mongoTemplate.insert(seller)
        mongoTemplate.insert(seller2)
    }

    afterTest {
        mongoTemplate.dropCollection(Author::class.java)
    }

    "Join" {
        val projection = document {
            field(Author::name) eq "John"
        } join {
            field(Author::id) eq field(Seller::authorId)
        } projection {
            constructor(Author::class)
        }

        val result = mongoTemplate.aggregate(projection, Author::class)
        println(result)
    }

    "Join with projection" {
        val projection = document {
            field(Author::name) eq "John"
        } join {
            field(Author::id) eq field(Seller::authorId)
        } projection {
            constructor(AuthorAndSeller::class)
        }

        val result = mongoTemplate.aggregate(projection, Author::class)
        println(result)
    }
})