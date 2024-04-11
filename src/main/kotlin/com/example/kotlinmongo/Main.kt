package com.example.kotlinmongo

import org.springframework.data.mongodb.core.MongoTemplate

private lateinit var mongoTemplate: MongoTemplate

fun main() {
    val document = document {
        field(Author::name) eq "John"
        field(Author::age) gt 18
    }

    val countToName = document.groupBy(Author::name).count()
    mongoTemplate.sumOfSingle(countToName, Author::class)
}