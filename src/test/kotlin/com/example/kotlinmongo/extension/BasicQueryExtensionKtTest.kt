package com.example.kotlinmongo.extension

import com.example.kotlinmongo.Author
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.BasicQuery

class BasicQueryExtensionKtTest: StringSpec({
    "orderBy 정렬 테스트" {
        val result = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 18 },
            )
        }.orderBy(Author::name, Sort.Direction.DESC)

        result shouldBe BasicQuery("{ \"name\" : \"John\", \"age\" : 18 }").with(Sort.by(Sort.Direction.DESC, "name"))
    }
})