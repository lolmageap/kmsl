package com.example.kotlinmongo

import com.example.kotlinmongo.clazz.field
import com.example.kotlinmongo.collection.Author
import com.example.kotlinmongo.extension.RootDocumentOperator.NOR
import com.example.kotlinmongo.extension.RootDocumentOperator.OR
import com.example.kotlinmongo.extension.document
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class DocumentScopeTest : StringSpec({
    "document scope 를 열 때 매개 변수를 전달하지 않으면 AND 연산자로 변환 된다." {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "document scope 를 열 때 매개 변수로 OR 를 전달하면 OR 연산자로 변환 된다." {
        val document = document(OR) {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$or\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "document scope 를 열 때 매개 변수로 NOR 를 전달하면 NOR 연산자로 변환 된다." {
        val document = document(NOR) {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"\$nor\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }
})