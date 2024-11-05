package com.kmsl.dsl

import com.kmsl.dsl.clazz.field
import com.kmsl.dsl.collection.Author
import com.kmsl.dsl.extension.RootDocumentOperator.NOR
import com.kmsl.dsl.extension.RootDocumentOperator.OR
import com.kmsl.dsl.extension.document
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class DocumentScopeTest : StringSpec({
    "Converting to AND operator when no parameters are passed while opening document scope" {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "Converting to OR operator when OR is passed as a parameter while opening document scope" {
        val document = document(OR) {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$or\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "Converting to NOR operator when OR is passed as a parameter while opening document scope" {
        val document = document(NOR) {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$nor\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }
})