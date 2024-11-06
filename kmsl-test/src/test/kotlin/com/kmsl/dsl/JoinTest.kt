package com.kmsl.dsl

import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.collection.Author
import com.kmsl.dsl.collection.AuthorAndSeller
import com.kmsl.dsl.collection.Seller
import com.kmsl.dsl.collection.Status.ACTIVE
import com.kmsl.dsl.collection.Status.RETIREMENT
import com.kmsl.dsl.extension.aggregate
import com.kmsl.dsl.extension.document
import com.kmsl.dsl.extension.join
import com.kmsl.dsl.extension.projection
import com.kmsl.dsl.util.WithTestContainers
import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [KmslApplication::class])
class JoinTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : WithTestContainers, StringSpec({
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

        val seller = createSeller(author.id)
        val seller2 = createSeller(author2.id)
        mongoTemplate.insert(seller)
        mongoTemplate.insert(seller2)
    }

    afterTest {
        mongoTemplate.dropCollection(Author::class.java)
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