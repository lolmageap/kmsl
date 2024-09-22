package com.example.kotlinmongo

import com.kmsl.dsl.clazz.field
import com.example.kotlinmongo.collection.Author
import com.kmsl.dsl.extension.RootDocumentOperator.OR
import com.kmsl.dsl.extension.document
import com.kmsl.dsl.extension.order
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.bson.Document
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC

class OrderByTest : StringSpec({
    "단일 orderBy 정렬 테스트" {
        val result = document(OR) {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        } order {
            field(Author::name) by DESC
        }

        result.sortObject shouldBe Document("name", -1)
    }

    "다중 orderBy 정렬 테스트" {
        val result = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        } order {
            field(Author::name) by DESC
            field(Author::age) by ASC
        }

        result.sortObject shouldBe Document("name", -1).append("age", 1)
    }
})