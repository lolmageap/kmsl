package com.example.kotlinmongo

import com.example.kotlinmongo.clazz.field
import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.collection.Status
import com.example.kotlinmongo.extension.*
import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
class IdTest(
    private val mongoTemplate: MongoTemplate,
) : StringSpec({
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
            field(Author::id) eq author.id
        } order {
            field(Author::age) by DESC
            field(Author::weight) by ASC
        } group {
            field(Author::status) and field(Author::age)
        } sum {
            field(Author::age) type Double::class alias "total"
        } average {
            field(Author::weight) alias "avg"
        } max {
            field(Author::height) alias "max"
        } min {
            field(Author::height) alias "min"
        }

        val map = mongoTemplate.aggregate(document, Author::class)
        println("map: $map")
    }

    "id 값으로 조회2" {
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
            field(Author::id) eq author.id
        } sum {
            field(Author::age) alias "total"
        } average {
            field(Author::weight) alias "avg"
        } max {
            field(Author::height) alias "max"
        } min {
            field(Author::height) alias "min"
        }

        val map = mongoTemplate.aggregate(document, Author::class)
        println("map: $map")
        println(map["total"])
        println(map["avg"])
        println(map["max"])
        println(map["min"])
    }
})