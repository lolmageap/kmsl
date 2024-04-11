package com.example.kotlinmongo

import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.BasicQuery

private lateinit var mongoTemplate: MongoTemplate

fun main() {
    val name = "Jane Doe"
    val age = 30
    val weights = listOf(70.0, 80.0)
    val heights = listOf(170.0f, 180.0f)
    val pageable = PageRequest.of(0, 10)

    val document = getAll(name, age, weights, heights)

    val authors = mongoTemplate.find(document, pageable, Author::class)
    val count = mongoTemplate.count(document, Author::class)

    println(authors)
    println(count)
}

private fun getAll(
    name: String,
    age: Int,
    weights: List<Double>,
    heights: List<Float>,
): BasicQuery {
    return document {
        field(Author::name) eq name
        field(Author::age) eq age
        field(Author::weight) nin weights
        field(Author::height) `in` heights
    }
}