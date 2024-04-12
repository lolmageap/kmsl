package com.example.kotlinmongo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.data.mongodb.core.query.BasicQuery

class FieldTest: StringSpec({
    "equal 연산 테스트" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"name\" : \"John\", \"age\" : 18 }")
    }

    "not equal 연산 테스트" {
        val document = document {
            field(Author::name) ne "John"
            field(Author::age) ne 18
        }

        document shouldBe BasicQuery("{ \"name\" : { \"\$ne\" : \"John\" }, \"age\" : { \"\$ne\" : 18 } }")
    }

    "greater than 연산 테스트" {
        val document = document {
            field(Author::age) gt 18
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gt\" : 18 } }")
    }

    "greater than or equal 연산 테스트" {
        val document = document {
            field(Author::age) gte 18
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gte\" : 18 } }")
    }

    "less than 연산 테스트" {
        val document = document {
            field(Author::age) lt 18
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$lt\" : 18 } }")
    }

    "less than or equal 연산 테스트" {
        val document = document {
            field(Author::age) lte 18
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$lte\" : 18 } }")
    }

    "between 연산 테스트" {
        val document = document {
            field(Author::age) between (18 to 30)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gt\" : 18, \"\$lt\" : 30 } }")
    }

    "between inclusive 연산 테스트" {
        val document = document {
            field(Author::age) betweenInclusive (18 to 30)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gte\" : 18, \"\$lte\" : 30 } }")
    }

    "greater than and less than 실패 케이스 (동일한 필드명을 중복해서 사용하면 마지막 값으로 덮어 씌워진다.)" {
        val document = document {
            field(Author::age) gt 18
            field(Author::age) lt 30
        }

        document shouldNotBe BasicQuery("{ \"age\" : { \"\$gt\" : 18, \"\$lt\" : 30 } }")
    }

    "greater than or equal and less than 성공 케이스" {

        val document = andOperator {
            and { field(Author::age) gte 18 }
            and { field(Author::age) lt 30 }
        }
        document shouldBe BasicQuery("{ \"\$and\" : [ { \"age\" : { \"\$gte\" : 18 } }, { \"age\" : { \"\$lt\" : 30 } } ] }")
    }

    "greater than and less than or equal 연산 테스트" {
        val document = document {
            field(Author::age) gtAndLte (18 to 30)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gt\" : 18, \"\$lte\" : 30 } }")
    }

    "greater than or equal and less than 연산 테스트" {
        val document = document {
            field(Author::age) gteAndLt (18 to 30)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$gte\" : 18, \"\$lt\" : 30 } }")
    }

    "in 연산 테스트" {
        val document = document {
            field(Author::age) `in` listOf(18, 19, 20)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$in\" : [ 18, 19, 20 ] } }")
    }

    "not in 연산 테스트" {
        val document = document {
            field(Author::age) nin listOf(18, 19, 20)
        }

        document shouldBe BasicQuery("{ \"age\" : { \"\$nin\" : [ 18, 19, 20 ] } }")
    }

    "contains 연산 테스트" {
        val document = document {
            field(Author::name) contains "John"
        }

        document shouldBe BasicQuery("{ \"name\" : { \"\$regex\" : \"John\" } }")
    }

    "contains not 연산 테스트" {
        val document = document {
            field(Author::name) containsNot "John"
        }

        document shouldBe BasicQuery("{ \"name\" : { \"\$not\" : { \"\$regex\" : \"John\" } } }")
    }

    "starts with 연산 테스트" {
        val document = document {
            field(Author::name) startsWith "John"
        }

        document shouldBe BasicQuery("{ \"name\" : { \"\$regex\" : \"^John\" } }")
    }

    "ends with 연산 테스트" {
        val document = document {
            field(Author::name) endsWith "John"
        }

        document shouldBe BasicQuery("{ \"name\" : { \"\$regex\" : \"John$\" } }")
    }

    "match 연산 테스트" {
        val document = document {
            field(Author::name) match "John"
        }

        document shouldBe BasicQuery("{ \"name\" : \"John\" }")
    }

    "and 연산 테스트" {
        val document = andOperator {
            and {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"John\" }, { \"age\" : 18 } ] }")
    }

    "or 연산 테스트" {
        val document = orOperator {
            or {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\" }, { \"age\" : 18 } ] }")
    }
})