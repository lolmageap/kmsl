package com.example.kotlinmongo

import com.kmsl.dsl.clazz.field
import com.example.kotlinmongo.collection.Author
import com.kmsl.dsl.extension.RootDocumentOperator.OR
import com.kmsl.dsl.extension.document
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class FieldTest : StringSpec({
    "equal operator test" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "not equal operator test" {
        val document = document {
            field(Author::name) ne "John"
            field(Author::age) ne 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$ne\" : \"John\"}}, { \"age\" : {\"\$ne\" : 18}}]}")
    }

    "greater than operator test" {
        val document = document {
            field(Author::age) gt 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gt\" : 18}}]}")
    }

    "converting type greater than operator test" {
        val document = document {
            field(Author::age) type Double::class gt 12.0
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"\$expr\" : { \"\$gt\" : [{ \"\$toDouble\" : \"\$age\" }, 12.0]}}]}")
    }

    "greater than or equal operator test" {
        val document = document {
            field(Author::age) gte 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gte\" : 18}}]}")
    }

    "converting type greater than or equal operator test" {
        val document = document {
            field(Author::age) type Double::class gte 12.0
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"\$expr\" : { \"\$gte\" : [{ \"\$toDouble\" : \"\$age\" }, 12.0]}}]}")
    }

    "less than operator test" {
        val document = document {
            field(Author::age) lt 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lt\" : 18}}]}")
    }

    "converting type less than operator test" {
        val document = document {
            field(Author::age) type Double::class lt 12.0
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"\$expr\" : { \"\$lt\" : [{ \"\$toDouble\" : \"\$age\" }, 12.0]}}]}")
    }

    "less than or equal operator test" {
        val document = document {
            field(Author::age) lte 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lte\" : 18}}]}")
    }

    "converting type less than or equal operator test" {
        val document = document {
            field(Author::age) type Double::class lte 12.0
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"\$expr\" : { \"\$lte\" : [{ \"\$toDouble\" : \"\$age\" }, 12.0]}}]}")
    }

    "between operator test" {
        val document = document {
            field(Author::age) between (18 to 30)
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gt\" : 18, \"\$lt\" : 30}}]}")
    }

    "between inclusive operator test" {
        val document = document {
            field(Author::age) betweenInclusive (18 to 30)
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gte\" : 18, \"\$lte\" : 30}}]}")
    }

    "greater than and less than or equal operator test" {
        val document = document {
            field(Author::age) gt 18
            field(Author::age) lte 30
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : { \"\$gt\" : 18}}, { \"age\" : { \"\$lte\" : 30}}]}")
    }

    "greater than or equal and less than operator test" {
        val document = document {
            field(Author::age) gte 18
            field(Author::age) lt 30
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : { \"\$gte\" : 18}}, { \"age\" : { \"\$lt\" : 30}}]}")
    }

    "in operator test" {
        val document = document {
            field(Author::age) `in` listOf(18, 19, 20)
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$in\" : [18, 19, 20]}}]}")
    }

    "not in operator test" {
        val document = document {
            field(Author::age) notIn listOf(18, 19, 20)
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$nin\" : [18, 19, 20]}}]}")
    }

    "contains operator test" {
        val document = document {
            field(Author::name) contains "John"
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"John\"}}]}")
    }

    "contains not operator test" {
        val document = document {
            field(Author::name) containsNot "John"
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$not\" : {\"\$regex\" : \"John\"}}}]}")
    }

    "starts with operator test" {
        val document = document {
            field(Author::name) startsWith "John"
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"^John\"}}]}")
    }

    "ends with operator test" {
        val document = document {
            field(Author::name) endsWith "John"
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"John$\"}}]}")
    }

    "match operator test" {
        val document = document {
            field(Author::name) match "John"
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$match\" : \"John\"}}]}")
    }

    "or operator test" {
        val document = document(OR) {
            and {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
            and {
                field(Author::name) eq "Any"
                field(Author::age) eq 81
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"\$and\" : [ { \"name\" : \"John\" }, { \"age\" : 18 } ] }, { \"\$and\" : [ { \"name\" : \"Any\" }, { \"age\" : 81 } ] } ] }")
    }
})