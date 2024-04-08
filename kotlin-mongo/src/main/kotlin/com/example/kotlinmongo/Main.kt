package com.example.kotlinmongo

fun main() {
    val document = document {
        field(Author::name) eq "John"
        field(Author::age) gt 18

        groupBy(Author::name).count()
    }

    println(document)
}