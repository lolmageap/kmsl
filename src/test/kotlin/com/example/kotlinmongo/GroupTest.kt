package com.example.kotlinmongo

import com.example.kotlinmongo.entity.Author
import com.example.kotlinmongo.extension.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.BasicQuery
import java.util.*

@SpringBootTest
class GroupTest(
    private val mongoTemplate: MongoTemplate,
) : StringSpec({
    "비슷한 조건을 재사용 해서 합계를 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val heavyMan = document.where { field(Author::weight) gt 70.0 }
        val smallMan = document.where { field(Author::height) lt 160f }

        heavyMan shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 30}, { \"weight\" : {\"\$gt\" : 70.0}}]}")
        smallMan shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 30}, { \"height\" : {\"\$lt\" : 160.0}}]}")
    }

    "전체에 대한 합을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.sumOf { field(Author::age) }
        mongoTemplate.sum(aggregation, Author::class)
    }

    "grouping 된 필드에 대한 합을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy(Author::name).sumOf { field(Author::age) }
        mongoTemplate.sumOfGroup(aggregation, Author::class)
    }

    "mongoDB 에 값이 날짜 형식과 같은 다른 형식으로 저장이 되어 있어도 합을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy().sumOf(Date::class) { field(Author::birthday) }
        mongoTemplate.sum(aggregation, Author::class)
    }

    "전체에 대한 count 를 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        mongoTemplate.count(document, Author::class)
    }

    "grouping 된 count 를 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy(Author::name).count()
        mongoTemplate.countOfGroup(aggregation, Author::class)
    }

    "전체에 대한 평균을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.avgOf { field(Author::age) }
        mongoTemplate.avg(aggregation, Author::class)
    }

    "grouping 된 평균을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy(Author::name).avgOf { field(Author::age) }
        mongoTemplate.avgOfGroup(aggregation, Author::class)
    }

    "전체에 대한 최대값을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.maxOf { field(Author::age) }
        mongoTemplate.max(aggregation, Author::class)
    }

    "grouping 된 최대값을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy(Author::name).maxOf { field(Author::age) }
        mongoTemplate.maxOfGroup(aggregation, Author::class)
    }

    "전체에 대한 최소값을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.minOf { field(Author::age) }
        mongoTemplate.min(aggregation, Author::class)
    }

    "grouping 된 최소값을 구할 수 있다" {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 30 },
            )
        }

        val aggregation = document.groupBy(Author::name).minOf { field(Author::age) }
        mongoTemplate.minOfGroup(aggregation, Author::class)
    }
})