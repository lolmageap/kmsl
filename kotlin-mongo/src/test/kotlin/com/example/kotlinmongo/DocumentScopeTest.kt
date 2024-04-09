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

    "document scope 내에서 동일한 field 를 중복해서 사용 하려면 and scope 에 add 로 각각 사용 해야 한다." {
        val document = document {
            andScope {
                and { field(Author::name) eq "John" }
                and { field(Author::name) eq "Doe" }
            }
        }

        document shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"John\" }, { \"name\" : \"Doe\" } ] }")
    }

    "document scope 내에서 동일한 field 를 중복해서 or 연산을 사용 하려면 or scope 에 add 로 각각 사용 해야 한다." {
        val document = document {
            orScope {
                or { field(Author::name) eq "John" }
                or { field(Author::name) eq "Doe" }
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"John\" }, { \"name\" : \"Doe\" } ] }")
    }

    "or scope 내에서 and scope 를 사용하면 and 조건으로 연결된다." {
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

    "or scope 내에서 or 를 여러번 사용하면 or 조건으로 연결된다." {
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

    "or scope 내에 하나의 add 에 동일한 field 를 여러번 사용하면 마지막 값으로 덮어 씌워진다." {
        val document = document {
            orScope {
                or {
                    field(Author::name) eq "John"
                    field(Author::name) eq "Doe"
                }
            }
        }

        document shouldBe BasicQuery("{ \"\$or\" : [ { \"name\" : \"Doe\" } ] }")
    }
})