package com.example.kotlinmongo

import com.example.kotlinmongo.extension.andOperator
import com.example.kotlinmongo.extension.field
import com.example.kotlinmongo.extension.orOperator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.mongodb.core.query.BasicQuery

class OperatorTest: StringSpec({
    "document scope 내에서 동일한 field 를 중복해서 사용 하려면 and scope 에 add 로 각각 사용 해야 한다." {
        val document = andOperator {
            and { field(Author::name) eq "John" }
            and { field(Author::name) eq "Doe" }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"John\" }, { \"name\" : \"Doe\" } ] }")
    }

    "document scope 내에서 동일한 field 를 중복해서 or 연산을 사용 하려면 or scope 에 add 로 각각 사용 해야 한다." {
        val document = orOperator {
            or { field(Author::name) eq "John" }
            or { field(Author::name) eq "Doe" }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\" }, { \"name\" : \"Doe\" } ] }")
    }

    "or scope 내에서 and scope 를 사용하면 and 조건으로 연결된다." {
        val document = orOperator {
            or {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\", \"age\" : 18 } ] }")
    }

    "or scope 내에서 or 를 여러번 사용하면 or 조건으로 연결된다." {
        val document = orOperator {
            or {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
            or {
                field(Author::name) eq "Jane"
                field(Author::age) eq 20
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\", \"age\" : 18 }, { \"name\" : \"Jane\", \"age\" : 20 } ] }")
    }

    "or scope 내에 하나의 add 에 동일한 field 를 여러번 사용하면 마지막 값으로 덮어 씌워진다." {
        val document = orOperator {
            or {
                field(Author::name) eq "John"
                field(Author::name) eq "Doe"
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"Doe\" } ] }")
    }

    "복잡한 조회 연산도 가능하다." {
        val document = orOperator {
            or {
                field(Author::name) eq "Jane"
                field(Author::age) eq 20
            }

            or {
                field(Author::name) eq "John"
                field(Author::age) eq 18
            }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"Jane\", \"age\" : 20 } ], \"\$or\" : [ { \"name\" : \"John\", \"age\" : 18 } ] }")
    }

    "and 연산을 사용하면 and 조건으로 연결된다." {
        val document = andOperator {
            and { field(Author::name) eq "Jane" }
            and { field(Author::name) eq "John" }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"Jane\" }, { \"name\" : \"John\" } ], \"\$or\" : [] }")
    }

    "or 연산을 사용하면 or 조건으로 연결된다." {
        val document = orOperator {
            or {
                field(Author::name) eq "Jane"
            }
            or {
                field(Author::name) eq "John"
            }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [],  \"\$or\" : [ { \"name\" : \"Jane\" }, { \"name\" : \"John\" } ] }")
    }

    "or scope 내에서 동일한 필드명 을 and 연산 으로 사용 하려면 and method 매개 변수에 함수 인자 상태로 값을 넣어야 한다." {
        val document = orOperator {
            or {
                and(
                    { field(Author::name) eq "Jane" },
                    { field(Author::name) eq "John" },
                )
            }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [], \"\$or\" : [ { \"\$and\" : [ { \"name\" : \"Jane\", \"name\" : \"John\" } ] } ] }")
    }
})