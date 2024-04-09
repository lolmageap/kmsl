package com.example.kotlinmongo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class OrBuilderTest : StringSpec({
    "document scope 내에서 orBuilder 를 사용 하면 BasicQuery 로 변환 된다." {
        val document = document {
            orScope {
                or {
                    field(Author::name) eq "John"
                    field(Author::age) eq 18
                }
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\", \"age\" : 18 } ] }")
    }

    "document scope 내에서 orBuilder 의 or 를 여러번 사용하면 or scope 내부는 and 로 연결된다." {
        val document = document {
            orScope {
                or {
                    field(Author::name) eq "John"
                    field(Author::age) eq 18
                }
                or {
                    field(Author::name) eq "Jane"
                    field(Author::age) eq 20
                }
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\", \"age\" : 18 }, { \"name\" : \"Jane\", \"age\" : 20 } ] }")
    }
})