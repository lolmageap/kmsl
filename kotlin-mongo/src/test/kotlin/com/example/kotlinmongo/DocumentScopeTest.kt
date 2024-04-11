package com.example.kotlinmongo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class DocumentScopeTest : StringSpec({
    "document scope 내에서 field 를 사용 하면 BasicQuery 로 변환 된다." {
        val document = document {
            field(Author::name) eq "John"
            field(Author::age) eq 18
        }

        document shouldBe BasicQuery("{ \"name\" : \"John\", \"age\" : 18 }")
    }

    "document scope 내에서 동일한 field 를 여러번 사용 하면 마지막에 사용된 값으로 덮어 씌워진다." {
        val document = document {
            field(Author::name) eq "John"
            field(Author::name) eq "Doe"
        }

        document shouldBe BasicQuery("{ \"name\" : \"Doe\" }")
    }
    // TIP : 이 문제를 해결 하기 위해서 OperatorBuilder 클래스 를 사용 해야 합니다.
})