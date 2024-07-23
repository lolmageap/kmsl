package com.example.kotlinmongo

import com.example.kotlinmongo.entity.Author
import com.example.kotlinmongo.extension.document
import com.example.kotlinmongo.extension.field
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class DocumentScopeTest : StringSpec({
    "document scope 내에서 field 를 사용 하면 BasicQuery 로 변환 된다." {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }
})