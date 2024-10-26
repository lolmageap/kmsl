package com.example.kotlinmongo

import com.kmsl.dsl.clazz.FieldName.AVERAGE_FIELD
import com.kmsl.dsl.clazz.FieldName.COUNT_FIELD
import com.kmsl.dsl.clazz.FieldName._ID
import com.kmsl.dsl.clazz.FieldName.MAX_FIELD
import com.kmsl.dsl.clazz.FieldName.MIN_FIELD
import com.kmsl.dsl.clazz.FieldName.SUM_FIELD
import com.kmsl.dsl.clazz.GroupType.SINGLE
import com.kmsl.dsl.clazz.field
import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Book
import com.example.kotlinmongo.collection.Status
import com.example.kotlinmongo.collection.Status.*
import com.kmsl.dsl.KmslApplication
import com.kmsl.dsl.extension.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.core.MongoTemplate
import java.math.BigDecimal

@SpringBootTest(classes = [KmslApplication::class])
class GroupTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : StringSpec({
    beforeTest {
        mongoTemplate.dropCollection(Author::class.java)

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

    "Total count" {
        val document = document {
            field(Author::name) eq "John"
        }

        val count = mongoTemplate.count(document, Author::class)
        count shouldBe 4
    }

    "Count of grouping" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        }

        val countOfGroup = mongoTemplate.count(document, Author::class).map {
            Status.valueOf(it.key) to it.value.toLong()
        }.toMap()

        countOfGroup shouldBe mapOf(ACTIVE to 3, RETIREMENT to 1)
    }

    "Count of grouping2" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        } count {
            field(Author::status) alias COUNT_FIELD
        }

        val countOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[COUNT_FIELD].toLong() }
            .mapKeys { Status.valueOf(it.key.toString()) }

        countOfGroup shouldBe mapOf(ACTIVE to 3, RETIREMENT to 1)
    }

    "Sum of all fields" {
        val document = document {
            field(Author::name) eq "John"
        } sum {
            field(Author::age) alias SUM_FIELD
        }

        val sum = mongoTemplate.aggregate(document, Author::class)[SUM_FIELD].toLong()
        sum shouldBe 100
    }

    "Sum of grouping" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        } sum {
            field(Author::age) alias SUM_FIELD
        }

        val sumOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[SUM_FIELD].toLong() }
            .mapKeys { Status.valueOf(it.key.toString()) }

        sumOfGroup shouldBe mapOf(ACTIVE to 90, RETIREMENT to 10)
    }

    "Sum of grouping with mongoTemplate" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        }

        val statusToTotalAge = mongoTemplate.sum(document, Author::age)
        statusToTotalAge shouldBe mapOf(ACTIVE to 90, RETIREMENT to 10)
    }

    "Average of all fields" {
        val document = document {
            field(Author::name) eq "John"
        } average {
            field(Author::age) alias AVERAGE_FIELD
        }

        val avg = mongoTemplate.aggregate(document, Author::class)[AVERAGE_FIELD].toDouble()
        avg shouldBe 25.0
    }

    "Average of grouping" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        } average {
            field(Author::age) alias AVERAGE_FIELD
        }

        val avgOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[AVERAGE_FIELD].toDouble() }
            .mapKeys { Status.valueOf(it.key.toString()) }

        avgOfGroup shouldBe mapOf(ACTIVE to 30.0, RETIREMENT to 10.0)
    }

    "Max of all fields" {
        val document = document {
            field(Author::name) eq "John"
        } max {
            field(Author::age) alias MAX_FIELD
        }

        val max = mongoTemplate.aggregate(document, Author::class)[MAX_FIELD].toLong()
        max shouldBe 40
    }

    "Max of grouping" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        } max {
            field(Author::age) alias MAX_FIELD
        }

        val maxOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[MAX_FIELD].toLong() }
            .mapKeys { Status.valueOf(it.key.toString()) }

        maxOfGroup shouldBe mapOf(ACTIVE to 40, RETIREMENT to 10)
    }

    "Min of all fields" {
        val document = document {
            field(Author::name) eq "John"
        } min {
            field(Author::age) alias MIN_FIELD
        }

        val min = mongoTemplate.aggregate(document, Author::class)[MIN_FIELD].toLong()
        min shouldBe 10
    }

    "Min of grouping" {
        val document = document {
            field(Author::name) eq "John"
        } group {
            field(Author::status) by SINGLE
        } min {
            field(Author::age) alias MIN_FIELD
        }

        val minOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[MIN_FIELD].toInt() }
            .mapKeys { Status.valueOf(it.key.toString()) }

        minOfGroup shouldBe mapOf(ACTIVE to 20, RETIREMENT to 10)
    }

    "Sum of all fields with different type" {
        val document = document {
            field(Author::name) eq "John"
        } sum {
            field(Author::age) type BigDecimal::class alias SUM_FIELD
        }

        val sum = mongoTemplate.aggregate(document, Author::class)[SUM_FIELD].toBigDecimal()
        sum shouldBe 100.toBigDecimal()
    }

    "Aggregate query after grouping" {
        val document =
            document {
                field(Author::age) eq 30
                embeddedDocument(Author::books) elemMatch {
                    field(Book::price) exists false
                    field(Book::description) startsWith "test"
                }
            } order {
                field(Author::age) by DESC
                field(Author::weight) by ASC
            } group {
                field(Author::status) and field(Author::age)
            } sum {
                field(Author::age) alias SUM_FIELD
            } average {
                field(Author::weight) alias AVERAGE_FIELD
            } max {
                field(Author::height) alias MAX_FIELD
            } min {
                field(Author::height) alias MIN_FIELD
            } count {
                field(Author::id) alias COUNT_FIELD
            }

        val result = mongoTemplate.aggregate(document, Author::class)

        result.first { it[_ID] == ACTIVE }[SUM_FIELD].toLong() shouldBe 90
        result.first { it[_ID] == ACTIVE }[AVERAGE_FIELD].toDouble() shouldBe 90.0
        result.first { it[_ID] == ACTIVE }[MAX_FIELD].toInt() shouldBe 190
        result.first { it[_ID] == ACTIVE }[MIN_FIELD].toInt() shouldBe 190
        result.first { it[_ID] == ACTIVE }[COUNT_FIELD].toLong() shouldBe 1

        result.first { it[_ID] == RETIREMENT }[SUM_FIELD].toLong() shouldBe 10
        result.first { it[_ID] == RETIREMENT }[AVERAGE_FIELD].toDouble() shouldBe 90.0
        result.first { it[_ID] == RETIREMENT }[MAX_FIELD].toInt() shouldBe 190
        result.first { it[_ID] == RETIREMENT }[MIN_FIELD].toInt() shouldBe 190
        result.first { it[_ID] == RETIREMENT }[COUNT_FIELD].toLong() shouldBe 1

        result.first { it[_ID] == REST }[SUM_FIELD].toLong() shouldBe 0
        result.first { it[_ID] == REST }[AVERAGE_FIELD].toDouble() shouldBe 0.0
        result.first { it[_ID] == REST }[MAX_FIELD].toInt() shouldBe 0
        result.first { it[_ID] == REST }[MIN_FIELD].toInt() shouldBe 0
        result.first { it[_ID] == REST }[COUNT_FIELD].toLong() shouldBe 0
    }

    "Aggregate query without grouping" {
        val author = mongoTemplate.insert(
            Author.of(
                name = "Test",
                age = 100,
                weight = 170.0,
                height = 70f,
                status = RETIREMENT,
                books = mutableListOf(),
            )
        )

        val document = document {
            field(Author::id) eq author.id
        } sum {
            field(Author::age) alias SUM_FIELD
        } average {
            field(Author::weight) alias AVERAGE_FIELD
        } max {
            field(Author::height) alias MAX_FIELD
        } min {
            field(Author::height) alias MIN_FIELD
        } count {
            field(Author::id) alias COUNT_FIELD
        }

        val result = mongoTemplate.aggregate(document, Author::class)
        result[_ID] shouldBe null
        result[SUM_FIELD].toLong() shouldBe 100
        result[AVERAGE_FIELD].toDouble() shouldBe 170.0
        result[MAX_FIELD].toInt() shouldBe 70
        result[MIN_FIELD].toInt() shouldBe 70
        result[COUNT_FIELD].toLong() shouldBe 1
    }
})