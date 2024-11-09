package com.kmsl.dsl

import com.kmsl.dsl.clazz.FieldName.AVERAGE_FIELD
import com.kmsl.dsl.clazz.FieldName.COUNT_FIELD
import com.kmsl.dsl.clazz.FieldName.MAX_FIELD
import com.kmsl.dsl.clazz.FieldName.MIN_FIELD
import com.kmsl.dsl.clazz.FieldName.SUM_FIELD
import com.kmsl.dsl.clazz.FieldName._ID
import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.collection.Author
import com.kmsl.dsl.collection.Book
import com.kmsl.dsl.collection.Status
import com.kmsl.dsl.collection.Status.*
import com.kmsl.dsl.extension.*
import com.kmsl.dsl.util.WithTestContainers
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import java.math.BigDecimal

@SpringBootTest(classes = [KmslApplication::class])
class GroupTest(
    @Autowired private val mongoTemplate: MongoTemplate,
) : WithTestContainers, StringSpec({
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
            field(Author::status)
        } count {
            field(Author::status) alias COUNT_FIELD
        }

        val countOfGroup = mongoTemplate.aggregate(document, Author::class)
            .associate { it[_ID] to it[COUNT_FIELD].toInt() }
            .mapKeys {
                Status.valueOf(it.key.toString())
            }

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
            field(Author::status)
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
            field(Author::status)
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
            field(Author::status)
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
            field(Author::status)
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
            field(Author::status)
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
                field(Author::name) eq "John"
                embeddedDocument(Author::books) elemMatch {
                    field(Book::description) exists false
                    field(Book::price) eq 10000L
                }
            } group {
                field(Author::status)
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

        result.first { it[_ID] == ACTIVE.name }[SUM_FIELD].toLong() shouldBe 90
        result.first { it[_ID] == ACTIVE.name }[AVERAGE_FIELD].toDouble() shouldBe 90.0
        result.first { it[_ID] == ACTIVE.name }[MAX_FIELD].toFloat() shouldBe 200f
        result.first { it[_ID] == ACTIVE.name }[MIN_FIELD].toFloat() shouldBe 180f
        result.first { it[_ID] == ACTIVE.name }[COUNT_FIELD].toLong() shouldBe 3

        result.first { it[_ID] == RETIREMENT.name }[SUM_FIELD].toLong() shouldBe 10
        result.first { it[_ID] == RETIREMENT.name }[AVERAGE_FIELD].toDouble() shouldBe 70.0
        result.first { it[_ID] == RETIREMENT.name }[MAX_FIELD].toFloat() shouldBe 170f
        result.first { it[_ID] == RETIREMENT.name }[MIN_FIELD].toFloat() shouldBe 170f
        result.first { it[_ID] == RETIREMENT.name }[COUNT_FIELD].toLong() shouldBe 1

        result.firstOrNull { it[_ID] == REST.name } shouldBe null
    }

    "Aggregate query without grouping" {
        val document = document {
            field(Author::name) eq "John"
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
        result[AVERAGE_FIELD].toDouble() shouldBe 85.0
        result[MAX_FIELD].toFloat() shouldBe 200f
        result[MIN_FIELD].toFloat() shouldBe 170f
        result[COUNT_FIELD].toLong() shouldBe 4
    }
})

private fun Any?.toLong() =
    this?.toString()
        ?.replaceAfter(".", "")
        ?.toLongOrNull()
        ?: 0

private fun Any?.toInt(): Int =
    this?.toString()
        ?.replaceAfter(".", "")
        ?.toIntOrNull()
        ?: 0

private fun Any?.toDouble() =
    this?.toString()?.toDoubleOrNull() ?: 0.0

private fun Any?.toFloat() =
    this?.toString()?.toFloatOrNull() ?: 0f

private fun Any?.toBigDecimal() =
    this?.toString()?.toBigDecimalOrNull() ?: BigDecimal.ZERO