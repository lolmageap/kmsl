package com.example.kotlinmongo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
class GroupTest(
    private val mongoTemplate: MongoTemplate,
): StringSpec( {
    "단일 필드에 대한 합을 구할 수 있다" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 30
        }

        val aggregation = document.groupBy().sumOf { Author::age }

        mongoTemplate.sumOfSingle(aggregation, Author::class)
    }

    "grouping 된 필드에 대한 합을 구할 수 있다" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 30
        }

        val aggregation = document.groupBy(Author::name).sumOf { Author::age }

        mongoTemplate.sumOfGroup(aggregation, Author::class)
    }

    "비슷한 조건을 재사용 해서 합계를 구할 수 있다" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 30
        }

        val heavyMan = document.where { field(Author::weight) gt 70.0 }
        val smallMan = document.where { field(Author::height) lt 160f }

        heavyMan shouldBe "{ \"name\" : \"John\", \"age\" : 30, \"weight\" : { \"\$gt\" : 70.0 } }"
        smallMan shouldBe "{ \"name\" : \"John\", \"age\" : 30, \"height\" : { \"\$lt\" : 160.0 } }"
    }

    "mongoDB 에 값이 문자일 때 합을 구할 수 있다" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 30
        }

        val aggregation = document.groupBy().sumOfNumber { Author::name }

        mongoTemplate.sumOfSingle(aggregation, Author::class)
    }
})