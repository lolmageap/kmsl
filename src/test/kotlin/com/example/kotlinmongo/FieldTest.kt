package com.example.kotlinmongo

import com.example.kotlinmongo.entity.Author
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.field
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class FieldTest : StringSpec({
    "equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 18 },
            )
        }
        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "not equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) ne "John" },
                { field(Author::age) ne 18 }
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$ne\" : \"John\"}}, { \"age\" : {\"\$ne\" : 18}}]}")
    }

    "greater than 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) gt 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gt\" : 18}}]}")
    }

    "greater than or equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) gte 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gte\" : 18}}]}")
    }

    "less than 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) lt 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lt\" : 18}}]}")
    }

    "less than or equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) lte 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lte\" : 18}}]}")
    }

    "between 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) between (18 to 30) },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gt\" : 18, \"\$lt\" : 30}}]}")
    }

    "between inclusive 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) betweenInclusive (18 to 30) },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lte\" : 18, \"\$gte\" : 30}}]}")
    }

    "greater than and less than or equal 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) gt 18 },
                { field(Author::age) lte 30 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : { \"\$gt\" : 18}}, { \"age\" : { \"\$lte\" : 30}}]}")
    }

    "greater than or equal and less than 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) gte 18 },
                { field(Author::age) lt 30 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : { \"\$gte\" : 18}}, { \"age\" : { \"\$lt\" : 30}}]}")
    }

    "in 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) `in` listOf(18, 19, 20) },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$in\" : [18, 19, 20]}}]}")
    }

    "not in 연산 테스트" {
        val document = document {
            and(
                { field(Author::age) notIn listOf(18, 19, 20) },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$nin\" : [18, 19, 20]}}]}")
    }

    "contains 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) contains "John" },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"John\"}}]}")
    }

    "contains not 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) containsNot "John" },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$not\" : {\"\$regex\" : \"John\"}}}]}")
    }

    "starts with 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) startsWith "John" },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"^John\"}}]}")
    }

    "ends with 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) endsWith "John" },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"John$\"}}]}")
    }

    "match 연산 테스트" {
        val document = document {
            and(
                { field(Author::name) match "John" },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$match\" : \"John\"}}]}")
    }

    "or 연산 테스트" {
        val document = document {
            or(
                {
                    and(
                        { field(Author::name) eq "John" },
                        { field(Author::age) eq 18 },
                    )
                },
                {
                    and(
                        { field(Author::name) eq "Any" },
                        { field(Author::age) eq 81 },
                    )
                },
            )
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"\$and\" : [ { \"name\" : \"John\" }, { \"age\" : 18 } ] }, { \"\$and\" : [ { \"name\" : \"Any\" }, { \"age\" : 81 } ] } ] }")
    }
})